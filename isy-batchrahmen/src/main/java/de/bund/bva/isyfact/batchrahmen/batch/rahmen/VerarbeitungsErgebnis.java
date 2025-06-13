package de.bund.bva.isyfact.batchrahmen.batch.rahmen;

/**
 * Das Ergebnis der Verarbeitung eines Satzes durch eine
 * {@link BatchAusfuehrungsBean} Bean.
 * 
 *
 */
public class VerarbeitungsErgebnis {
    
    /** Der verarbeitete Datenbankschluessel fuer das Wiederaufsetzen. */
    private String datenbankSchluessel;

    /** Fachliche ID des verarbeiteten Satzes. */
    private String fachlicheId;
    
    /** Falls wahr, wurden alle Sätze bearbeitet und der Batch beendet. */    
    private boolean alleSaetzeVerarbeitet;
    
    /**
     * setzt alle benoetigten Daten.
     * @param schluessel              der Datenbankschluessel
     * @param alleSaetzeVerarbeitet   wahr, falls alle Saetze verarbeitet werden.
     */
    public VerarbeitungsErgebnis(String schluessel, boolean alleSaetzeVerarbeitet) {
        datenbankSchluessel = schluessel;
        this.alleSaetzeVerarbeitet = alleSaetzeVerarbeitet;
    }
    /**
     * @return the datenbankSchluessel
     */
    public String getDatenbankSchluessel() {
        return datenbankSchluessel;
    }
    /**
     * @param datenbankSchluessel the datenbankSchluessel to set
     */
    public void setDatenbankSchluessel(String datenbankSchluessel) {
        this.datenbankSchluessel = datenbankSchluessel;
    }
    /**
     * Liefert das Feld 'fachlicheId' zurück.
     * @return Wert von fachlicheId
     */
    public String getFachlicheId() {
        return fachlicheId;
    }
    /**
     * Setzt das Feld 'fachlicheId'.
     * @param fachlicheId Neuer Wert für fachlicheId
     */
    public void setFachlicheId(String fachlicheId) {
        this.fachlicheId = fachlicheId;
    }
    /**
     * @return the alleSaetzeVerarbeitet
     */
    public boolean isAlleSaetzeVerarbeitet() {
        return alleSaetzeVerarbeitet;
    }
    /**
     * @param alleSaetzeVerarbeitet the alleSaetzeVerarbeitet to set
     */
    public void setAlleSaetzeVerarbeitet(boolean alleSaetzeVerarbeitet) {
        this.alleSaetzeVerarbeitet = alleSaetzeVerarbeitet;
    }
}
