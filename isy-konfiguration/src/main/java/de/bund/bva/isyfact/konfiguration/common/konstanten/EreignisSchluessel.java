package de.bund.bva.isyfact.konfiguration.common.konstanten;

/**
 * Diese Klasse enthält Ereignisschlüssel für das Logging.
 * <p>
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
public class EreignisSchluessel {

    /** Die Konfigurationsdatei {} wird neu geladen. */
    public static final String KONFIGURATION_DATEI_NEU_GELADEN = "EPLKON00001";

    /** Mindestens eine Konfigurationsdatei wurde geändert. */
    public static final String KONFIGURATION_DATEI_GEAENDERT = "EPLKON00002";

    /** Listener wurde nicht hinzugefügt, da die gleiche Instanz bereits registriert ist. */
    public static final String KONFIGURATION_LISTENER_NICHT_HINZUGEFUEGT = "EPLKON00003";

}
