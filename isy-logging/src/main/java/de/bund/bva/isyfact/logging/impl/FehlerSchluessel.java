package de.bund.bva.isyfact.logging.impl;

/**
 * Fehlerschlüssel von Isy-Logging.
 * 
 */
public final class FehlerSchluessel {

    /**
     * Privater Konstruktor der Klasse. Verhindert, dass Instanzen der Klasse angelegt werden.
     * 
     */
    private FehlerSchluessel() {
    }

    /**
     * Der Logger der bereitgestetllten SLF4J-Implementierung implementiert nicht das benötigte Inferface
     * LocationAwareLogger. Bereitgestellt wurde: {0}.
     */
    public static final String FALSCHES_LOGGING_FRAMEWORK = "ISYLO00000";

    /**
     * Beim Erstellen eines Logeintrags im Level {0} in Logger {1} wurde kein Schlüssel und keine Exception
     * übergeben.
     */
    public static final String FEHLERHAFTER_EINTRAG_KEIN_SCHLUESSEL = "ISYLO00001";

    /**
     * Beim Erstellen eines Logeintrags im Level {0} in Logger {1} wurde keine Kategorie übergeben.
     */
    public static final String FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE = "ISYLO00002";

    /**
     * Es wurde kein JsonFormatter für das JSON-Layout {0} konfiguriert.
     */
    public static final String FEHLENDE_KONFIGURATION_JSON_LAYOUT = "ISYLO00003";

    /**
     * Fehler beim Aufruf der Methode {0} durch den Log-Interceptor.
     */
    public static final String LOG_INTERCEPTOR_FEHLER_BEI_AUFRUF = "ISYLO01000";
    
    /**
     * Fehler bei der Serialisierung der Aufrufparameter.
     */
    public static final String FEHLER_SERIALISIERUNG_AUFRUFPARAMETER = "ISYLO01001";

}
