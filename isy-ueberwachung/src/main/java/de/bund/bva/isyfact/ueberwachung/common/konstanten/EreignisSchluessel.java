package de.bund.bva.isyfact.ueberwachung.common.konstanten;

/**
 * Diese Klasse enthält Ereignisschlüssel für das Logging.
 */
public final class EreignisSchluessel {

    private EreignisSchluessel() {
        // private constructor to hide the implicit public one.
    }

    /** Standard-Ereignis für Debug/Info-Ausgaben ohne eigene Ereignis-ID. */
    public static final String PLUEB00001 = "PLUEB00001";

    /** Pruefung der Pruefroutine nicht erfolgreich.
     *  @deprecated  nicht mehr verwendet. */
    @Deprecated
    public static final String PLUEB00002 = "PLUEB00002";

    /** Pruefung der Pruefroutine nicht erfolgreich (hat Exception geworfen).
     * @deprecated Wird nicht mehr verwendet. */
    @Deprecated
    public static final String PLUEB00003 = "PLUEB00003";

    /** Selbsttest fehlgeschlagen.
     * @deprecated Wird nicht mehr verwendet. */
    @Deprecated
    public static final String PLUEB00004 = "PLUEB00004";

    /** Loadbalancer wurde abgefragt, aber isAlive-Datei existiert, aber es konnte keine Antwort gesendet werden. */
    public static final String IS_ALIVE_EXISTIERT_IO_EXCEPTION = "PLUEB00006";

    /** Loadbalancer wurde abgefragt, aber isAlive-Datei existiert nicht. */
    public static final String IS_ALIVE_EXISTIERT_NICHT = "PLUEB00005";

    /** Essentielles Nachbarsystem {} nicht erreicht. Status: {} */
    public static final String NACHBARSYSTEM_ESSENTIELL_NICHT_ERREICHBAR = "PLUEB00002";

    /** Nicht-essentielles Nachbarsystem {} nicht erreicht. Status: {} */
    public static final String NACHBARSYSTEM_NICHT_ESSENTIELL_NICHT_ERREICHBAR = "PLUEB00003";

    /** Bei der Anfrage des Nachbarsystems {} ist ein Fehler aufgetreten: {}. */
    public static final String NACHBARSYSTEM_ANFRAGEFEHLER = "PLUEB00004";
}
