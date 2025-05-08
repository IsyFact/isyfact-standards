package de.bund.bva.isyfact.batchrahmen.core.konstanten;

/**
 * Contains constants for messages of the batch frame.
 * 
 * 
 */
public abstract class NachrichtenSchluessel {

    // **** Error in the configuration ****
    /** Parameter missing. */
    public static final String ERR_KONF_PARAMETER_FEHLT = "BAT100";

    /** Parameter invalid. */
    public static final String ERR_KONF_PARAMETER_UNGUELTIG = "BAT110";

    /** Configuration file could not be read. */
    public static final String ERR_KONF_DATEI_LESEN = "BAT120";

    /** Batch bean must be configured. */
    public static final String ERR_KONF_BEAN_PFLICHT = "BAT130";

    // **** Error in the command line parameters ****
    /** Command line parameters invalid. */
    public static final String ERR_KOMMANDO_PARAMETER_UNGUELTIG = "BAT200";

    /** Configuration file not specified. */
    public static final String ERR_KOMMANDO_PARAMETER_KEINE_CONFIG = "BAT210";

    /** Two exclusionary parameters were specified. */
    public static final String ERR_KOMMANDO_PARAMETER_KONFLIKT = "BAT220";

    /** One of two parameters must be set. */
    public static final String ERR_KOMMANDO_PARAMETER_NOETIG = "BAT230";

    /** Parameter must start with -. */
    public static final String ERR_KOMMANDO_PARAMETER_PRAEFIX = "BAT240";
    
    /** No value is specified for the command line parameter {0}. */
    public static final String ERR_KOMMANDO_PARAMETER_WERT_NOETIG = "BAT250";

    // **** Error during initialization ****
    /** Status entry already in DB. */
    public static final String ERR_BATCH_IN_DB = "BAT300";

    /** Last run is canceled. */
    public static final String ERR_IGNORIERE_RESTART = "BAT320";

    /** Batch is already running. */
    public static final String ERR_BATCH_AKTIV = "BAT310";

    /** Restart not possible. */
    public static final String ERR_BATCH_INIT_ABGEBR = "BAT330";

    // **** Error during execution ****
    /** Not all records were processed. */
    public static final String ERR_BATCH_UNVOLLSTAENDIG = "BAT400";

    /** Error while processing the result log. */
    public static final String ERR_BATCH_PROTOKOLL = "BAT410";

    /** Class could not be found */
    public static final String ERR_KLASSE_NICHT_GEFUNDEN = "BAT420";

    // **** Messages for ReturnCodes ****
    /** OK. */
    public static final String MSG_RC_OK = "RC_OK";

    /** Executed with errors. */
    public static final String MSG_RC_FEHLER_AUSGEFUEHRT = "RC_FEHLER_AUSGEFUEHRT";

    /** Abort. */
    public static final String MSG_RC_FEHLER_ABBRUCH = "RC_FEHLER_ABBRUCH";

    /** Error in the call parameters. */
    public static final String MSG_RC_FEHLER_PARAMETER = "RC_FEHLER_PARAMETER";

    /** Error in the configuration. */
    public static final String MSG_RC_FEHLER_KONFIGURATION = "RC_FEHLER_KONFIGURATION";

    /** Manual abort. */
    public static final String MSG_RC_FEHLER_MANUELLER_ABBRUCH = "RC_FEHLER_MANUELLER_ABBRUCH";

    /** Abort due to runtime overrun. */
    public static final String MSG_RC_FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN =
        "RC_FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN";
    
}
