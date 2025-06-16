package de.bund.bva.isyfact.konfiguration.common.konstanten;

/**
 * Diese Klasse enthaelt Konstanten fuer alle in Resource-Bundles verwendeten Schluessel.
 *
 */
public abstract class NachrichtenSchluessel {
    /** Der angegebene Konfigurationsparameter ist nicht gesetzt. */
    public static final String ERR_PARAMETER_LEER = "KONF100";

    /** Der angegebene Konfigurationsparameter hat ein ungültiges Format. */
    public static final String ERR_PARAMETERWERT_UNGUELTIG = "KONF101";

    /** Konfigurationsdatei konnte nicht geladen werden. */
    public static final String ERR_DATEI_LESEN = "KONF200";

    /** Konfigurationsdatei konnte nicht geladen werden. */
    public static final String ERR_DATEI_NICHT_GEFUNDEN = "KONF201";

    /** Konfigurationsdatei konnte nicht geladen werden. */
    public static final String ERR_DATEI_FORMAT = "KONF202";

    /** Der Pfad des Property-Ordners muss mit einem "/" enden. */
    public static final String ERR_PROPERTY_ORDNER_PFAD = "KONF203";

}
