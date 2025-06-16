package de.bund.bva.isyfact.serviceapi.common.konstanten;

/**
 * This class contains all event keys for isy-serviceapi-core.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */

@Deprecated
public class EreignisSchluessel {

    /**
     * There is no exception mapping defined for the {} service operation for exception class {}.
     * Use technical TO exception {} instead.
     */
    public static final String KEIN_EXCEPTION_MAPPING_DEFINIERT = "EPLSAC00002";

    /** Error in error handling. OR Original error was {}. */
    public static final String FEHLER_FEHLERBEHANDLUNG = "EPLSAC00003";

    /** Validate configuration for service implementation {}. */
    public static final String VALIDIERUNG_KONFIGURATION = "EPLSAC00004";

    /** A timeout occurred when calling the service [{}]. */
    public static final String TIMEOUT = "EPLSAC00005";

    /** Call aborted after timeout. */
    public static final String TIMEOUT_ABBRUCH = "EPLSAC00006";

    /** Wait {}ms until the call is repeated. */
    public static final String TIMEOUT_WARTEZEIT = "EPLSAC00007";

    /** Waiting for call repetition canceled. */
    public static final String TIMEOUT_WARTEZEIT_ABBRUCH = "EPLSAC00008";

    /** Repeat call... */
    public static final String TIMEOUT_WIEDERHOLUNG = "EPLSAC00009";

    /**
     * Corrected the correlation id {} in the call context as it does not match
     * the correlation id on the MDC {} is not the same.
     */
    public static final String AUFRUFKONTEXT_KORRID_KORRIGIERT = "EPLSAC00010";

    /** A call context without correlation ID was sent. Create new correlation ID. */
    public static final String KEINE_KORRELATIONSID_IM_AUFRUFKONTEXT_UEBERMITTELT = "EPLASC00011";

    /** No Authorization header with bearer token received. It is not set in the AufrufKontextVerwalter. */
    public static final String KEIN_BEARER_TOKEN_UEBERMITTELT = "EPLASC00012";

    /** No bearer token in the AufrufKontextVerwalter or the Security Context. The Authorization header is not set. */
    public static final String KEIN_BEARER_TOKEN_IM_AUFRUFKONTEXT = "EPLASC00013";

}
