package de.bund.bva.isyfact.persistence.exception;

/**
 * Fehlerschlüssel für isy-persistence.
 *
 */
public final class FehlerSchluessel {

    /**
     * Fehlerhafte Konfiguration der Enum-Persistierung: {0}.
     */
    public static final String FALSCHE_ENUM_KONFIGURATION = "PERSI00001";

    /**
     * Unbekannter Stringwert {0} für den Enumtyp {1}.
     */
    public static final String UNBEKANNTER_STRING = "PERSI00002";

    /**
     * Unbekannte Ausprägung {0} für den Enumtyp {1}.
     */
    public static final String UNBEKANNTE_AUSPRAEGUNG = "PERSI00003";

    /**
     * Keine Datenbank-Connection verfügbar.
     */
    public static final String KEINE_DB_CONNECTION_VERFUEGBAR = "PERSI00004";

    /**
     * Keine valide Datenbank-Connection verfügbar.
     */
    public static final String KEINE_VALIDE_DB_CONNECTION_VERFUEGBAR = "PERSI00007";

    /**
     * Die Version des Datenbankschemas entspricht nicht der erwarteten Version ({0}).
     */
    public static final String FALSCHE_DB_SCHEMAVERSION = "PERSI00005";

    /**
     * Beim Prüfen der Version des Datenbankschemas ist ein Fehler aufgetreten.
     */
    public static final String PRUEFEN_DER_SCHEMAVERSION_FEHLGESCHLAGEN = "PERSI00006";

    /**
     * Die Version des Datenbankschemas konnte nicht geprüft werden. Verbindungen zu diesem Schema sind erst
     * nach einem Neustart wieder verfügbar.
     */
    public static final String DB_BEIM_HOCHFAHREN_NICHT_VERFUEGBAR = "PERSI00008";

    /**
     * Es konnte keine Verbindung aufgebaut werden, da beim Hochfahren ein Fehler aufgetreten war. Ist die
     * Datenbank wieder erreichbar, muss das System neu gestartet werden.
     */
    public static final String KEINE_CONNECTION_WEGEN_FEHLERHAFTER_INITIALISIERUNG = "PERSI00009";

}
