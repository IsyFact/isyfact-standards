package de.bund.bva.isyfact.sicherheit.common.konstanten;

/**
 * Diese Klasse enthält alle die Fehler für alle Schlüssel der Komponente Sicherheit.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public abstract class SicherheitFehlerSchluessel {

    /** Der übergebene Parameter "{0}" ist null oder leer. */
    public static final String MSG_PARAMETER_FEHLT = "SIC1000";

    /** Ein Wert für die Initialisierung ist null oder leer (String). */
    public static final String MSG_INITIALISIERUNGS_ELEMENT_FEHLT = "SIC1001";

    // Konfigurationsfehler

    /** Innerhalb eines Berechtigungsmanager wird das Rollenrechtemapping benötigt, fehlt aber. */
    public static final String MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLT = "SIC2000";

    /** Fehler beim Parsen der Datei zum RollenRechteMapping: (String). */
    public static final String MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT = "SIC2001";

    /** Tag wurde fehlerhaft konfiguriert. */
    public static final String MSG_TAG_KONFIGURATION_FEHLERHAFT = "SIC2002";

    /** Ein durch die Anwendung überprüftes Recht war im Rollenrechtemapping nicht definiert. */
    public static final String MSG_AUTORISIERUNG_RECHT_UNDEFINIERT = "SIC2003";

    /** Die Annotaton {0} fehlt an der Methode {1}. */
    public static final String MSG_ANNOTATION_FEHLT_AN_METHODE = "SIC2004";

    // Fehler bei der Autorisierung

    /** Der Berechtigungsmanager konnte nicht erzeugt werden. */
    public static final String MSG_BERECHTIGUNGSMANAGER_NICHT_VERFUEGBAR = "SIC2050";

    /** Das erforderliche Recht fehlt. */
    public static final String MSG_AUTORISIERUNG_FEHLGESCHLAGEN = "SIC2051";

    /** Der Berechtigungsmanager konnte den AufrufKontext nicht lesen. */
    public static final String MSG_AUFRUFKONTEXT_NICHT_VERFUEGBAR = "SIC2052";

    /** Die Autorisierung ist mit einem technischen Fehler gescheitert. */
    public static final String MSG_AUTORISIERUNG_TECHNISCH_FEHLGESCHLAGEN = "SIC2053";

    // Fehler bei Authentifizierung über den Access-Manager.
    /** Authentifizierung des Benutzers über Access Manager fehlgeschlagen. */
    public static final String MSG_AUTHENTIFIZIERUNG_FEHLGESCHLAGEN = "SIC4000";

    /** Zugriff auf den Access-Manager fehlgeschlagen. */
    public static final String MSG_ZUGRIFF_ACCESSMANAGER_FEHLGESCHLAGEN = "SIC4001";

    // Fehler bei Abruf und Auswertung der Sessioninformationen, die zu einer Session-Id vom Access-Manager
    // abgerufen wurden

}
