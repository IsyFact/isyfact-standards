package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.springframework.transaction.TransactionStatus;

import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;

/**
 * Informationen zur aktuellen Verarbeitunsstand des Batches. Dieses Objekt wird
 * zur Transportierung dier Informationen in verschiedene Methoden und
 * Operationen benoetigt.
 * <p>
 * Dieser Informations-Container kapselt alle für die Verarbeitung notwendige Parameter,
 * damit diese nicht einzeln übergeben werden müssen.
 * 
 * 
 */
public class VerarbeitungsInformationen {
    /** Konfigurations-Properties des Batches. */
    private final BatchKonfiguration konfiguration;

    /** Laeuft der Batch ueber START oder Restart? */
    private final BatchStartTyp startTyp;

    /** Aktuelle Transaktion des Batches. */
    private TransactionStatus transactionStatus;

    /** Ausfuehrungsbean des Batches. */
    private BatchAusfuehrungsBean bean;

    /** Commit-Intervall des Batches. */
    final private long commitIntervall;

    /** Clear-Intervall des Batches (Session-Cache clearen). */
    final private long clearIntervall;

    /** Nummer des letzten verarbeiteten Satzes. */
    private long satzNummer;

    /** Nummer des letzten Datensatzes. */
    private long letzterDatensatzNummer;
    
    /** Zeitstempel vom Begin des Batches als long ( System.currentTimeMillis() ) */
    final private long zeitstempelBegin;
    
    /** Das konfigurierte maximale Laufzeit-Limit in Minten. Oder -1, wenn keine Laufzeit konfiguriert wurde. */
    final private long maximaleLaufzeitLimitInMinuten;

    /**
     * Erzeugt neue Instanz. StartTyp, CommitInterval, ClearInterval werden aus
     * der Konfiguration ausgelesen.
     * @param konfiguration Die Batchkonfiguration.
     */
    public VerarbeitungsInformationen(BatchKonfiguration konfiguration) {
        this.konfiguration = konfiguration;
        startTyp = konfiguration.getStartTyp();
        commitIntervall =
                konfiguration.getAsLong(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_COMMIT_INTERVALL);
        clearIntervall =
                konfiguration.getAsLong(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_CLEAR_INTERVALL);
        zeitstempelBegin = System.currentTimeMillis();
        maximaleLaufzeitLimitInMinuten =
            konfiguration.getAsLong(KonfigurationSchluessel.KOMMANDO_PARAM_LAUFZEIT, -1);
    }
    
    /**
     * Prüft, ob die maximale Laufzeit konfiguriert ist.
     * @return true, wenn die maximale Laufzeit konfiguriert ist
     */
    public boolean istMaximaleLaufzeitKonfiguriert() {
        return maximaleLaufzeitLimitInMinuten != -1;
    }
    
    /**
     * Ermittelt die vergangene Laufzeit aufgrund der aktueller Zeit und dem in der Initialisierung
     * festgehaltenen Zeitpunkt. Angabe in Minuten.
     * 
     * @return vergangeneMinuten - die vergangene Laufzeit
     */
    public long getAktuelleLaufzeitInMinuten() {        
        long zeitstempelJetzt = Calendar.getInstance().getTimeInMillis();
        long vergangeneMillis = zeitstempelJetzt - zeitstempelBegin;
        return TimeUnit.MILLISECONDS.toMinutes(vergangeneMillis);
    }

    /**
     * @return the konfig
     */
    public BatchKonfiguration getKonfiguration() {
        return konfiguration;
    }

    /**
     * @return the startTyp
     */
    public BatchStartTyp getStartTyp() {
        return startTyp;
    }

    /**
     * @return the transactionStatus
     */
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * @param transactionStatus
     *            the transactionStatus to set
     */
    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    /**
     * @return the bean
     */
    public BatchAusfuehrungsBean getBean() {
        return bean;
    }

    /**
     * @param bean
     *            the bean to set
     */
    public void setBean(BatchAusfuehrungsBean bean) {
        this.bean = bean;
    }

    /**
     * @return the commitIntervall
     */
    public long getCommitIntervall() {
        return commitIntervall;
    }

    /**
     * @return the satzNummer
     */
    public long getSatzNummer() {
        return satzNummer;
    }

    /**
     * erhoeht die Satznummer um eins.
     */
    public void incSatzNummer() {
        satzNummer++;
    }

    /**
     * @param satzNummer
     *            the satzNummer to set
     */
    public void setSatzNummer(long satzNummer) {
        this.satzNummer = satzNummer;
    }

    /**
     * @return the clearIntervall
     */
    public long getClearIntervall() {
        return clearIntervall;
    }

    /** 
     * Liefert die Nummer des letzen Datensatzes.
     * @return Die Nummer des letzen Datensatzes.
     */
    public long getLetzterDatensatzNummer() {
        return letzterDatensatzNummer;
    }

    /**
     * Setzt die Nummer des letzten Datensatzes.
     * 
     * @param letzterDatensatzNummer
     *            die Nummer des letzten Datensatzes.
     */
    public void setLetzterDatensatzNummer(long letzterDatensatzNummer) {
        this.letzterDatensatzNummer = letzterDatensatzNummer;
    }

    /**
     * Liefert das Feld 'zeitstempelBegin' zurück.
     * @return Wert von zeitstempelBegin
     */
    public long getZeitstempelBegin() {
        return zeitstempelBegin;
    }

    /**
     * Liefert das Feld 'maximaleLaufzeitLimitInMinuten' zurück.
     * @return Wert von maximaleLaufzeitLimitInMinuten
     */
    public long getMaximaleLaufzeitLimitInMinuten() {
        return maximaleLaufzeitLimitInMinuten;
    }
    
}
