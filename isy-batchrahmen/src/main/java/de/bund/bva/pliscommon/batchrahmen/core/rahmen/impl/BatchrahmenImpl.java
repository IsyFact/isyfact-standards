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
package de.bund.bva.pliscommon.batchrahmen.core.rahmen.impl;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;

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

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.pliscommon.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.AuthenticationCredentials;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.pliscommon.batchrahmen.core.exception.BatchrahmenAbbruchException;
import de.bund.bva.pliscommon.batchrahmen.core.exception.BatchrahmenKonfigurationException;
import de.bund.bva.pliscommon.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.pliscommon.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.pliscommon.batchrahmen.core.rahmen.jmx.BatchRahmenMBean;
import de.bund.bva.pliscommon.batchrahmen.persistence.rahmen.BatchStatus;

/**
 * Implementation of the batchrahmen functionality.
 * <p>
 * T is the type of the {@link AufrufKontext} to be used.
 */
public class BatchrahmenImpl<T extends AufrufKontext> implements Batchrahmen, InitializingBean,
        ApplicationContextAware, DisposableBean {

    /**
     * The logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(BatchrahmenImpl.class);

    /**
     * Reference to the TransactionManager.
     */
    private JpaTransactionManager transactionManager;

    /**
     * The capsule around the BatchStatus table.
     */
    private StatusHandler statusHandler;

    /**
     * The {@link ApplicationContext}, needed to read the bean.
     */
    private ApplicationContext applicationContext;

    /**
     * The JMX bean.
     */
    private BatchRahmenMBean jmxBean;

    /**
     * Indicates whether the batch was terminated.
     */
    private volatile boolean batchAbgebrochen;

    /**
     * Indicator when the batch has ended.
     */
    private volatile boolean batchLaeuft;

    /* Flag whether the maximum runtime has been exceeded. */
    private boolean maximaleLaufzeitUeberschritten;

    /**
     * Saves the previous batch status.
     **/
    private String vorigerBatchStatus;

    /**
     * Reference to component {@link AufrufKontextVerwalter}
     */
    private AufrufKontextVerwalter<T> aufrufKontextVerwalter;

    /**
     * Reference to the {@link AufrufKontextFactory}.
     */
    private AufrufKontextFactory<T> aufrufKontextFactory;

    /**
     * @return TransactionStatus
     */
    public TransactionStatus starteTransaktion() {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return this.transactionManager.getTransaction(transDef);
    }

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

        // Initialize status record
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
            // Initialization phase
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
            // Start transaction
            verarbInfo.setTransactionStatus(starteTransaktion());
            VerarbeitungsErgebnis ergebnis = null;
            // Execute until Bean stops processing records.
            while ((ergebnis == null || !ergebnis.isAlleSaetzeVerarbeitet())
                    && !(verarbInfo.getLetzterDatensatzNummer() != 0 && verarbInfo.getLetzterDatensatzNummer() == verarbInfo
                    .getSatzNummer()) && !this.batchAbgebrochen
                    && !(this.maximaleLaufzeitUeberschritten = istMaximaleLaufzeitUeberschritten(verarbInfo))) {
                verarbInfo.incSatzNummer();

                MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());
                aufrufKontext.setKorrelationsId(MdcHelper.liesKorrelationsId());

                ergebnis = verarbInfo.getBean().verarbeiteSatz();

                MdcHelper.entferneKorrelationsId();

                this.jmxBean.satzVerarbeitet(ergebnis.getDatenbankSchluessel());
                if ((verarbInfo.getCommitIntervall() > 0)
                        && (verarbInfo.getSatzNummer() % verarbInfo.getCommitIntervall() == 0)) {
                    // Process checkpoint
                    verarbeiteCheckpunkt(verarbInfo, ergebnis.getDatenbankSchluessel());
                }
                if ((verarbInfo.getClearIntervall() > 0)
                        && (verarbInfo.getSatzNummer() % verarbInfo.getClearIntervall() == 0)) {
                    // Get the current EntityManager
                    EntityManager entityManager =
                            EntityManagerFactoryUtils.getTransactionalEntityManager(this.transactionManager
                                    .getEntityManagerFactory());
                    // Clear session cache
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
                // Check for errors during init
                if (!initErfolgreich) {
                    LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                            "Fehler während der Initialisierung erkannt, beende Batch...");
                    // Process and terminate batch execution
                    rollbackTransaction(verarbInfo.getTransactionStatus(), verarbInfo.getBean());
                    beendeBatchMitVorigemStatus();
                } else if (!erfolgreich) {
                    // Error during processing
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
     * Checks whether the runtime has been exceeded, if the runtime is configured.
     *
     * @return {@code true}, if the runtime is configured and exceeded; otherwise {@code false}
     */
    private boolean istMaximaleLaufzeitUeberschritten(VerarbeitungsInformationen verarbInfo) {
        if (!verarbInfo.istMaximaleLaufzeitKonfiguriert()) {
            return false;
        }
        return verarbInfo.getAktuelleLaufzeitInMinuten() >= verarbInfo.getMaximaleLaufzeitLimitInMinuten();
    }

    /**
     * Logic for processing a checkpoint: the state database record is updated,
     * the transaction is terminated, a new one is started,
     * the state record is reloaded and the execution bean is informed.
     *
     * @param verarbInfo   the processing information of the batch
     * @param dbSchluessel the database key of the last record
     * @throws BatchAusfuehrungsException If an error occurs when processing the checkpoint.
     */
    private void verarbeiteCheckpunkt(VerarbeitungsInformationen verarbInfo, String dbSchluessel)
            throws BatchAusfuehrungsException {
        // Update status table
        BatchStatus status = this.statusHandler.leseBatchStatus();
        status.setSatzNummerLetztesCommit(verarbInfo.getSatzNummer());
        status.setSchluesselLetztesCommit(dbSchluessel);

        // Start new transaction, clear cache.
        // Inform bean about upcoming checkpoint.
        verarbInfo.getBean().vorCheckpointGeschrieben(verarbInfo.getSatzNummer());
        this.transactionManager.commit(verarbInfo.getTransactionStatus());
        verarbInfo.setTransactionStatus(starteTransaktion());

        // Inform Bean about checkpoint.
        verarbInfo.getBean().checkpointGeschrieben(verarbInfo.getSatzNummer());
    }

    /**
     * Initializes the processing information of the batch, the status table and the execution bean.
     *
     * @param verarbInfo the processing information to be filled.
     * @param protokoll  the {@link BatchErgebnisProtokoll} to be filled
     * @throws BatchAusfuehrungsException in case of initialization errors of the execution bean.
     */
    private void initialisiereBatch(VerarbeitungsInformationen verarbInfo, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException {

        // Start transaction
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

        // Read and initialize bean
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
        // End transaction.
        this.transactionManager.commit(verarbInfo.getTransactionStatus());
    }

    /**
     * Updates the state database record and informs the bean about the end of processing. Closes the last transaction.
     *
     * @param verarbInfo the processing information.
     * @param dbschl     the database key that was processed last.
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
        // The last commit follows: Update the last processed record number and database key.
        status.setSatzNummerLetztesCommit(verarbInfo.getSatzNummer());
        status.setSchluesselLetztesCommit(dbschl);
        this.transactionManager.commit(verarbInfo.getTransactionStatus());
    }

    /**
     * Sets the status in table BatchStatus to aborted and the date of the last abort to the current date.
     * Any errors that occur are logged. In this case, the transaction is rolled back.
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
     * Ends the batch with the previous status. This function is used in case of an error during the initialization phase.
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
     * The transaction is rolled back. Any errors that occur are logged and ignored.
     *
     * @param transactionStatus the transaction to be rolled back.
     * @param batchBean         (optional) the batch bean that is to be notified of the rollback.
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
     * reads a bean name from property {@link BatchKonfiguration.PROPERTY_AUSFUEHRUNGSBEAN} and
     * reads the bean with this name from the context.
     * The bean is cast to the {@link BatchAusfuehrungsBean} interface and returned.
     *
     * @param konfig the configuration
     * @return the Bean.
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
     * called by Spring to configure the transaction manager.
     *
     * @param transactionManager the transaction manager.
     */
    public void setTransactionManager(JpaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * called by Spring to set the {@link ApplicationContext}.
     *
     * @param applicationContext the {@link ApplicationContext}.
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * called by Spring to configure the JMX bean.
     *
     * @param jmxBean the JMX-Bean.
     */
    public void setJmxBean(BatchRahmenMBean jmxBean) {
        this.jmxBean = jmxBean;
    }

    public void afterPropertiesSet() throws Exception {
        this.statusHandler = new StatusHandler(this.transactionManager.getEntityManagerFactory());
    }

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
     * Sets the field {@link #aufrufKontextVerwalter}.
     *
     * @param aufrufKontextVerwalter New value for {@link #aufrufKontextVerwalter}
     */
    @Required
    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<T> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    /**
     * Sets the field {@link #aufrufKontextFactory}.
     *
     * @param aufrufKontextFactory New value for {@link #aufrufKontextFactory}
     */
    @Required
    public void setAufrufKontextFactory(AufrufKontextFactory<T> aufrufKontextFactory) {
        this.aufrufKontextFactory = aufrufKontextFactory;
    }

}
