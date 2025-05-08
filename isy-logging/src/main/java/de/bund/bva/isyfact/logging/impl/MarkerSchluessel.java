package de.bund.bva.isyfact.logging.impl;



/**
 * Auflistung aller Schlüssel (Namen) für Marker.
 * 
 */
public enum MarkerSchluessel {

    /** Rootmarker, zur Sammlung weitere Marker. */
    ROOTMARKER("rootmarker"),
    /** Kategorie des Logeintrags. */
    KATEGORIE("kategorie"),
    /** Schlüssel des Logeintrags. */
    SCHLUESSEL("schluessel"),
    /** Dauer des Logeintrags. */
    DAUER("dauer"),
    /** Methode eines geloggten Methodenaufruf. */
    METHODE("methode"),
    /** Parameter eines geloggten Methodenaufruf. */
    AUFRUFPARAMETER("aufrufparameter"),
    /** Ergebnis eines geloggten Methodenaufruf. */
    ERGEBNIS("ergebnis"),
    /** Datentyp des Logeintrags. */
    DATENTYP("datentyp");

    /**
     * Der Wert des Markers.
     */
    private final String wert;

    /**
     * Konstruktor der Klasse.
     * 
     * @param wert
     *            die zum Schlüssel gehörende Wert.
     */
    private MarkerSchluessel(String wert) {
        this.wert = wert;
    }

    /**
     * Liefert den Wert des Attributs 'wert'.
     * 
     * @return Wert des Attributs.
     */
    public String getWert() {
        return wert;
    }
}
