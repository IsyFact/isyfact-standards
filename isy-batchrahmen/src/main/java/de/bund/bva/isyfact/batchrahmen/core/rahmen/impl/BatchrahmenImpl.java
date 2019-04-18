/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.util.Date;
import java.util.UUID;
import javax.persistence.EntityManager;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.AuthenticationCredentials;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenAbbruchException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenKonfigurationException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.jmx.BatchRahmenMBean;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchStatus;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Implementierung der Batchrahmen-Funktionalitaet.
 * <p>
 * T ist der Typ des zu verwendenden AufrufKontextes.
 *
 *
 */
public class BatchrahmenImpl<T extends AufrufKontext> implements Batchrahmen, InitializingBean,
    ApplicationContextAware, DisposableBean {

    /** Der Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(BatchrahmenImpl.class);

    /** Referenz auf den TransaktionsManager. */
    private JpaTransactionManager transactionManager;

    /** Die Kapsel rund um die BatchStatus Tabelle. */
    private StatusHandler statusHandler;

    /** Der ApplicationContext, benoetigt zum Auslesen der Bean. */
    private ApplicationContext applicationContext;

    /** Die JMX-Bean. */
    private BatchRahmenMBean jmxBean;

    /** Kennzeichen, ob der Batch abgebrochen wurde. */
    private volatile boolean batchAbgebrochen;

    /** Kennzeichen, wenn der Batch beendet wurde. */
    private volatile boolean batchLaeuft;

    /* Flag ob die maximale Laufzeit überschritten wurde. */
    private boolean maximaleLaufzeitUeberschritten;

    /** Sichert den vorigen BatchStatus. **/
    private String vorigerBatchStatus;

    /** Referenz auf Komponente AufrufKontextVerwalter */
    private AufrufKontextVerwalter<T> aufrufKontextVerwalter;

    /** Referenz auf die AufrufKontextFactory. */
    private AufrufKontextFactory<T> aufrufKontextFactory;

    /**
     * @return TransactionStatus
     */
    public TransactionStatus starteTransaktion() {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return this.transactionManager.getTransaction(transDef);
    }

    /**
     * {@inheritDoc}
     */
    public void runBatch(BatchKonfiguration konfiguration, BatchErgebnisProtokoll protokoll)
        throws BatchAusfuehrungsException {
        VerarbeitungsInformationen verarbInfo = new VerarbeitungsInformationen(konfiguration);
        boolean erfolgreich = false;
        boolean initErfolgreich = false;

        if (verarbInfo.istMaximaleLaufzeitKonfiguriert()) {
            LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Die maximale Laufzeit ist als {} Minuten konfiguriert.",
                verarbInfo.getMaximaleLaufzeitLimitInMinuten());
        } else {
            LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Es wurde keine maximale Laufzeit konfiguriert.");
        }

        // Status-Satz initialisieren
        try {
            verarbInfo.setTransactionStatus(starteTransaktion());
            this.statusHandler.statusSatzInitialisieren(konfiguration);
            this.transactionManager.commit(verarbInfo.getTransactionStatus());
            erfolgreich = true;
        } finally {
            if (!erfolgreich && verarbInfo.getTransactionStatus() != null) {
                this.transactionManager.rollback(verarbInfo.getTransactionStatus());
            }
        }
        erfolgreich = false;

        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());
            // Initialisierungsphase
            T aufrufKontext = this.aufrufKontextFactory.erzeugeAufrufKontext();
            aufrufKontext.setKorrelationsId(MdcHelper.liesKorrelationsId());
            this.aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);
            this.batchLaeuft = true;

            AuthenticationCredentials authentifikation =
                getBatchAusfuehrer(verarbInfo.getKonfiguration()).getAuthenticationCredentials(konfiguration);
            if (authentifikation != null) {
                aufrufKontext.setDurchfuehrendeBehoerde(authentifikation.getBehoerdenkennzeichen());
                aufrufKontext.setDurchfuehrenderBenutzerKennung(authentifikation.getBenutzerkennung());
                aufrufKontext.setDurchfuehrenderBenutzerPasswort(authentifikation.getPasswort());
            }
            aufrufKontext.setDurchfuehrenderSachbearbeiterName(konfiguration
                .getAsString(KonfigurationSchluessel.PROPERTY_BATCH_NAME));
            this.aufrufKontextFactory.nachAufrufKontextVerarbeitung(aufrufKontext);
            initialisiereBatch(verarbInfo, protokoll);

            initErfolgreich = true;

            LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Beginne Batch-Satzverarbeitung...");
            // Transaktion starten
            verarbInfo.setTransactionStatus(starteTransaktion());
            VerarbeitungsErgebnis ergebnis = null;
            // Ausfuehren, bis Bean keine Datensaetze mehr verarbeitet.
            while ((ergebnis == null || !ergebnis.isAlleSaetzeVerarbeitet())
                && !(verarbInfo.getLetzterDatensatzNummer() != 0 && verarbInfo.getLetzterDatensatzNummer() == verarbInfo
                    .getSatzNummer()) && !this.batchAbgebrochen
                && !(this.maximaleLaufzeitUeberschritten = istMaximaleLaufzeitUeberschritten(verarbInfo))) {
                verarbInfo.incSatzNummer();

                MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

                ergebnis = verarbInfo.getBean().verarbeiteSatz();

                MdcHelper.entferneKorrelationsId();

                this.jmxBean.satzVerarbeitet(ergebnis.getDatenbankSchluessel());
                if ((verarbInfo.getCommitIntervall() > 0)
                    && (verarbInfo.getSatzNummer() % verarbInfo.getCommitIntervall() == 0)) {
                    // Checkpunkt verarbeiten
                    verarbeiteCheckpunkt(verarbInfo, ergebnis.getDatenbankSchluessel());
                }
                if ((verarbInfo.getClearIntervall() > 0)
                    && (verarbInfo.getSatzNummer() % verarbInfo.getClearIntervall() == 0)) {
                    // Den aktuellen EntityManager holen
                    EntityManager entityManager =
                        EntityManagerFactoryUtils.getTransactionalEntityManager(this.transactionManager
                            .getEntityManagerFactory());
                    // Session-Cache clearen
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            if (verarbInfo.getLetzterDatensatzNummer() != 0
                && verarbInfo.getLetzterDatensatzNummer() == verarbInfo.getSatzNummer()) {
                throw new BatchrahmenAbbruchException(NachrichtenSchluessel.ERR_BATCH_UNVOLLSTAENDIG);
            }
            String dbschl = null;
            if (ergebnis != null) {
                dbschl = ergebnis.getDatenbankSchluessel();
            }
            beendeBatch(verarbInfo, protokoll, dbschl);
            erfolgreich = true;
        } finally {
            try {
                // Prüfe auf Fehler während Init
                if (!initErfolgreich) {
                    LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                        "Fehler während der Initialisierung erkannt, beende Batch...");
                    // Batchausführung abwickeln und beenden
                    rollbackTransaction(verarbInfo.getTransactionStatus(), verarbInfo.getBean());
                    beendeBatchMitVorigemStatus();
                } else if (!erfolgreich) {
                    // Fehler während Verarbeitung
                    rollbackTransaction(verarbInfo.getTransactionStatus(), verarbInfo.getBean());
                    setzeStatusSatzAufAbbruch();
                }
            } finally {
                this.batchLaeuft = false;
                MdcHelper.entferneKorrelationsId();
            }
        }
    }

    /**
     * Prüft, ob die Laufzeit überschritten wurde, falls die Laufzeit konfiguriert ist.
     * @return true, wenn die Laufzeit konfiguriert ist und überschritten wurde; sonst false
     */
    private boolean istMaximaleLaufzeitUeberschritten(VerarbeitungsInformationen verarbInfo) {
        if (!verarbInfo.istMaximaleLaufzeitKonfiguriert()) {
            return false;
        }
        return verarbInfo.getAktuelleLaufzeitInMinuten() >= verarbInfo.getMaximaleLaufzeitLimitInMinuten();
    }

    /**
     * Logik fuer die Verarbeitung eines Checkpunkts: Der Status-Datenbanksatz wird aktualisiert, die
     * Transaktion beendet, eine neue gestartet, der Status-Satz neu geladen und die Ausfuehrungs-Bean
     * informiert.
     *
     * @param verarbInfo
     *            die Verarbeitungs-Informationen des Batches
     * @param dbSchluessel
     *            der Datenbank-Schluessel des letzten Satzes
     * @throws BatchAusfuehrungsException
     *             Wenn beim verarbeiten des Checkpoints ein Fehler auftritt.
     *
     * @throws BatchAusfuehrungsException
     *             bei Fehlern beim Informieren der Bean.
     */
    private void verarbeiteCheckpunkt(VerarbeitungsInformationen verarbInfo, String dbSchluessel)
        throws BatchAusfuehrungsException {
        // Status-Tabelle aktualisieren
        BatchStatus status = this.statusHandler.leseBatchStatus();
        status.setSatzNummerLetztesCommit(verarbInfo.getSatzNummer());
        status.setSchluesselLetztesCommit(dbSchluessel);

        // Neue Transaktion beginnen, Cache leeren.
        // Bean über anstehenden Checkpunkt informieren.
        verarbInfo.getBean().vorCheckpointGeschrieben(verarbInfo.getSatzNummer());
        this.transactionManager.commit(verarbInfo.getTransactionStatus());
        verarbInfo.setTransactionStatus(starteTransaktion());

        // Bean ueber Checkpunkt informieren.
        verarbInfo.getBean().checkpointGeschrieben(verarbInfo.getSatzNummer());
    }

    /**
     * Initialisiert die Verarbeitungs-Informationen des Batches, die Status-Tabelle sowie die
     * Ausfuehrungsbean.
     *
     * @param verarbInfo
     *            die zu befuellenden Verarbeitungs-Informationen.
     * @param protokoll
     *            das zu befuellende BatchErgebnisProtokollImpl
     * @throws BatchAusfuehrungsException
     *             bei Initialisierungs-Fehlern der Ausfuehrungs-Bean.
     */
    private void initialisiereBatch(VerarbeitungsInformationen verarbInfo, BatchErgebnisProtokoll protokoll)
        throws BatchAusfuehrungsException {

        // Transaktion starten

        verarbInfo.setTransactionStatus(starteTransaktion());
        this.vorigerBatchStatus = this.statusHandler.leseBatchStatus().getBatchStatus();
        this.statusHandler.setzteStatusSatzAufLaufend(verarbInfo.getKonfiguration());
        BatchStatus status = this.statusHandler.leseBatchStatus();

        if (verarbInfo.getStartTyp().equals(BatchStartTyp.RESTART)) {
            protokoll.ergaenzeMeldung(new VerarbeitungsMeldung("RESTART", MeldungTyp.INFO,
                "Batch wurde im Restart-Modus gestartet."));
            LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Batch wurde im Restart-Modus gestartet.");
        }

        // Bean lesen und initialisieren
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
            "Batch Initialisierungsphase beginnt...");
        verarbInfo.setBean(getBatchAusfuehrer(verarbInfo.getKonfiguration()));
        int anzSaetze =
            verarbInfo.getBean().initialisieren(verarbInfo.getKonfiguration(),
                status.getSatzNummerLetztesCommit(), status.getSchluesselLetztesCommit(),
                verarbInfo.getStartTyp(), status.getDatumLetzterErfolg(), protokoll);
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
            "Batch Initialisierungsphase beendet.");
        verarbInfo.setSatzNummer(status.getSatzNummerLetztesCommit());
        long anzahlDatensaetze =
            verarbInfo.getKonfiguration().getAsLong(
                KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_ZU_VERARBEITENDE_DATENSAETZE_ANZAHL, -1);
        if (anzahlDatensaetze > 0) {
            verarbInfo.setLetzterDatensatzNummer(anzahlDatensaetze + verarbInfo.getSatzNummer());
        } else {
            verarbInfo.setLetzterDatensatzNummer(0);
        }

        this.jmxBean.init(anzSaetze, verarbInfo.getSatzNummer(), status.getBatchId(), status.getBatchName());
        // Transaktion beenden.
        this.transactionManager.commit(verarbInfo.getTransactionStatus());
    }

    /**
     * Aktualisiert den Status-Datenbanksatz und informiert die Bean ueber das Ende der Verarbeitung.
     * Schliesst die letzte Transaktion.
     *
     * @param verarbInfo
     *            die Verarbeitungs-Informationen.
     * @param dbschl
     *            der Datenbankschluessel, der zuletzt verarbeitet wurde.
     */
    private void beendeBatch(VerarbeitungsInformationen verarbInfo, BatchErgebnisProtokoll protokoll,
        String dbschl) {
        BatchStatus status = this.statusHandler.leseBatchStatus();
        if (!this.batchAbgebrochen && !this.maximaleLaufzeitUeberschritten) {
            LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Batch beendet. Setze Status auf beendet.");
            status.setBatchStatus(BatchStatusTyp.BEENDET.getName());
            status.setDatumLetzterErfolg(new Date());
            verarbInfo.getBean().batchBeendet();
        } else {
            if (this.batchAbgebrochen) {
                protokoll.setBatchAbgebrochen(true);
                LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                    "Batch wurde abgebrochen. Setze Status auf abgebrochen.");
                protokoll.ergaenzeMeldung(new VerarbeitungsMeldung("BENUTZERABBRUCH", MeldungTyp.INFO,
                    "Batch wurde manuell abgebrochen."));
            } else if (this.maximaleLaufzeitUeberschritten) {
                protokoll.setMaximaleLaufzeitUeberschritten(true);
                LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                    "Batch wurde wegen Laufzeitüberschreitung abgebrochen. Setze Status auf abgebrochen.");
                protokoll.ergaenzeMeldung(new VerarbeitungsMeldung("MAX_LAUFZEIT_UEBERSCHRITTEN",
                    MeldungTyp.INFO, "Batch wurde durch die Überschreitung der maximalen Laufzeit ("
                        + verarbInfo.getMaximaleLaufzeitLimitInMinuten() + " Minuten) abgebrochen."));
            }
            status.setBatchStatus(BatchStatusTyp.ABGEBROCHEN.getName());
            status.setDatumLetzterAbbruch(new Date());
        }
        // Es folgt der letzte Commit: Aktualisiere letzte verarbeitete Satznummer und Datenbankschluessel
        status.setSatzNummerLetztesCommit(verarbInfo.getSatzNummer());
        status.setSchluesselLetztesCommit(dbschl);
        this.transactionManager.commit(verarbInfo.getTransactionStatus());
    }

    /**
     * setzt in Tabelle BatchStatus den Status auf abgebrochen und das Datum des letzten Abbruchs auf das
     * aktuelle Datum. Dabei auftretende Fehler werden geloggt. In diesem Fall wird die Transaktion
     * zurückgerollt.
     */
    private void setzeStatusSatzAufAbbruch() {
        TransactionStatus transactionStatus = null;
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
            "Setze Batch-Status auf Abbruch...");
        try {
            transactionStatus = starteTransaktion();
            BatchStatus status = this.statusHandler.leseBatchStatus();
            if (status != null) {
                status.setBatchStatus(BatchStatusTyp.ABGEBROCHEN.getName());
                status.setDatumLetzterAbbruch(new Date());
            }
            this.transactionManager.commit(transactionStatus);
        } catch (Throwable t) {
            LOG.error(BatchRahmenEreignisSchluessel.EPLBAT00001, t.getMessage(), t);
            rollbackTransaction(transactionStatus, null);
        }
    }

    /**
     * Beendet den Batch mit dem vorigen Status. Diese Funktion wird bei einem Fehler während der
     * Initialisierungsphase genutzt.
     */
    private void beendeBatchMitVorigemStatus() {
        TransactionStatus transactionStatus = null;
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
            "Setze Batch-Status auf vorigen Status ({})...", this.vorigerBatchStatus);
        try {
            transactionStatus = starteTransaktion();
            BatchStatus status = this.statusHandler.leseBatchStatus();
            if (status != null) {
                status.setDatumLetzterAbbruch(new Date());
                status.setBatchStatus(this.vorigerBatchStatus);
            }
            this.transactionManager.commit(transactionStatus);
        } catch (Throwable t) {
            LOG.error(BatchRahmenEreignisSchluessel.EPLBAT00001, t.getMessage(), t);
            rollbackTransaction(transactionStatus, null);
        }
    }

    /**
     * Die Transaktion wird zurückgerollt. Dabei auftretende Fehler werden geloggt und ignoriert.
     *
     * @param transactionStatus
     *            die zurückzurollende Transaktion.
     * @param batchBean
     *            (optional) die Batch-Bean, welcher das Zurückrollen mitgeteilt werden soll.
     */
    private void rollbackTransaction(TransactionStatus transactionStatus, BatchAusfuehrungsBean batchBean) {
        if (transactionStatus != null && !transactionStatus.isCompleted()) {
            try {
                if (batchBean != null) {
                    batchBean.vorRollbackDurchgefuehrt();
                }
                this.transactionManager.rollback(transactionStatus);
            } catch (Exception rollbackEx) {
                LOG.error(BatchRahmenEreignisSchluessel.EPLBAT00001, rollbackEx.getMessage(), rollbackEx);
            } finally {
                if (batchBean != null) {
                    batchBean.rollbackDurchgefuehrt();
                }
            }
        }
    }

    /**
     * liest einen Beannamen aus Property {@link BatchKonfiguration.PROPERTY_AUSFUEHRUNGSBEAN} und liest die
     * Bean mit diesem Namen aus dem Kontext. Die Bean wird auf das {@link BatchAusfuehrungsBean} Interface
     * gecastet und zurueckgegeben.
     *
     * @param konfig
     *            die Konfiguration
     * @return die Bean.
     */
    private BatchAusfuehrungsBean getBatchAusfuehrer(BatchKonfiguration konfig) {
        String beanName = konfig.getAsString(KonfigurationSchluessel.PROPERTY_AUSFUEHRUNGSBEAN);
        if (beanName == null || beanName.length() == 0) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT,
                KonfigurationSchluessel.PROPERTY_AUSFUEHRUNGSBEAN);
        }
        BatchAusfuehrungsBean bean = (BatchAusfuehrungsBean) this.applicationContext.getBean(beanName);
        if (bean == null) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_BEAN_PFLICHT, beanName);
        }
        return bean;
    }

    /**
     * aufgerufen durch Spring zum Konfigurieren des Transaktions-Managers.
     * @param transactionManager
     *            der Transaktionsmanager.
     */
    public void setTransactionManager(JpaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * aufgerufen durch Spring zum Setzen des Applikationskontext.
     * @param applicationContext
     *            Der Applikations-Kontext.
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * aufgerufen durch Spring zum Konfigurieren der JMX-Bean.
     * @param jmxBean
     *            die JMX-Bean.
     */
    public void setJmxBean(BatchRahmenMBean jmxBean) {
        this.jmxBean = jmxBean;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        this.statusHandler = new StatusHandler(this.transactionManager.getEntityManagerFactory());
    }

    /**
     *
     * {@inheritDoc}
     */
    public void destroy() throws Exception {
        this.batchAbgebrochen = true;

        if (this.batchLaeuft) {
            LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Warte auf Batch Ende...");
        }
        while (this.batchLaeuft) {
            Thread.sleep(1000);
        }
    }

    /**
     * Setzt das Feld {@link #aufrufKontextVerwalter}.
     * @param aufrufKontextVerwalter
     *            Neuer Wert für aufrufKontextVerwalter
     */
    @Required
    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<T> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    /**
     * Setzt das Feld {@link #aufrufKontextFactory}.
     * @param aufrufKontextFactory
     *            Neuer Wert für aufrufKontextFactory
     */
    @Required
    public void setAufrufKontextFactory(AufrufKontextFactory<T> aufrufKontextFactory) {
        this.aufrufKontextFactory = aufrufKontextFactory;
    }

}
