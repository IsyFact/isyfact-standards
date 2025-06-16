package de.bund.bva.isyfact.batchrahmen.core.rahmen.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * Diese Bean gibt Informationen ueber den Ablauf des Batches.
 * <ul>
 * <li> Name des Batches.
 * <li> ID des Batches.
 * <li> Gesamt-Anzahl zu verarbeitende Saetze
 * <li> Anzahl bereits verarbeiteter Saetze
 * <li> DB-Schluessel des letzten verarbeiteten Satzes
 * <li> Zeitraum seit letztem verarbeiteten Satz
 * </ul>
 * 
 *
 */
@ManagedResource(
        description = "Diese MBean liefert Informationen ueber den Batch-Ablauf.")
public class BatchRahmenMBean {
    /** Gesamt-Anzahl Sätze. */
    private long saetzeGesamt;
    /** Anzahl verarbeiteter Saetze. */
    private long saetzeVerarbeitet;
    /** DB-Schluessel des letzten verarbeiteten Satzes. */
    private String schluesselLetzterSatz;
    /** Zeitpunkt der Verarbeitung des letzten Satzes. */
    private long zeitpunktLetzterSatz;
    /** Name des Batches. */
    private String batchName;
    /** ID des Batches. */
    private String batchId;

    
    /**
     * @return the anzahlAnfragen
     */
    @ManagedAttribute(
            description = "Liefert die Gesamtanzahl zu verarbeitender Saetze (-1 falls nicht bekannt).")
    public long getSaetzeGesamt() {
        return saetzeGesamt;
    }

    /**
     * @return the anzahlAnfragen
     */
    @ManagedAttribute(
            description = "Liefert die Anzahl bisher verarbeiteter Saetze.")
    public long getSaetzeVerarbeitet() {
        return saetzeVerarbeitet;
    }

    /**
     * @return the anzahlAnfragen
     */
    @ManagedAttribute(
            description = "Liefert den letzten Datenbankschluessel.")
    public String getSchluesselLetzterSatz() {
        return schluesselLetzterSatz;
    }
    
    /**
     * @return the anzahlAnfragen
     */
    @ManagedAttribute(
            description = "Liefert die verstrichene Zeit seit dem letzten Satz (in ms).")
    public long getZeitSeitLetztemSatz() {
        return System.currentTimeMillis() - zeitpunktLetzterSatz;
    }

    /**
     * setzt die Gesamtanzahl an Saetzen (oder -1 falls nicht bekannt).
     * 
     * @param saetzeGesamt  Anzahl der zu verarbeitenden Sätze.
     * @param saetzeVerarbeitet Anzahl der durch Restart uebersprungenen Saetze.
     * @param batchId           ID des Batches.
     * @param batchName         Name des Batches.
     */
    public void init(long saetzeGesamt, long saetzeVerarbeitet, String batchId, String batchName) {
        this.saetzeGesamt = saetzeGesamt;
        this.saetzeVerarbeitet = saetzeVerarbeitet;
        this.batchId = batchId;
        this.batchName = batchName;
    }
    
    /**
     * gibt an, dass gerade ein Satz verarbeitet wurde.
     * 
     * @param dbSchluessel der Datenbank-Schluessel des verarbeiteten Satzes.
     */
    public void satzVerarbeitet(String dbSchluessel) {
        this.schluesselLetzterSatz = dbSchluessel;
        saetzeVerarbeitet++;
        zeitpunktLetzterSatz = System.currentTimeMillis();
    }

    /**
     * @return the batchName
     */
    @ManagedAttribute(
            description = "Der Name des laufenden Batches.")
    public String getBatchName() {
        return batchName;
    }

    /**
     * @return the batchId
     */
    @ManagedAttribute(
            description = "Die ID des laufenden Batches.")
    public String getBatchId() {
        return batchId;
    }


}
