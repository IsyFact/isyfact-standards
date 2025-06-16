package de.bund.bva.isyfact.batchrahmen.batch.protokoll;

/**
 * Enum für Meldungstypen.
 * 
 *
 */
public enum MeldungTyp {
    /** Fehler. */
    FEHLER("F"),
    /** Warnungen. */
    WARNUNG("W"),
    /** Infos. */
    INFO("I");
    
    /** Kürzel für das Protokoll. */
    private String kuerzel;
    
    /**
     * Erzeugt MeldungTyp mit Kürzel.
     * @param kuerzel Das Kürzek fürs Protokoll.
     */
    private MeldungTyp(String kuerzel) {
        this.kuerzel = kuerzel;
    }
    
    /**
     * Das Kürzel für diesen Meldungstyp.
     * @return Das Kürzel für diesen Meldungstyp.
     */
    public String getKuerzel() {
        return kuerzel;
    }
}
