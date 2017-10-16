package de.bund.bva.isyfact.task.konstanten;

/**
 * Die Konfigurationsschlüssel für isy-task
 */
public abstract class KonfigurationSchluessel {
    public static final String PRAEFIX = "isyfact.task.";

    public static final String INITIAL_NUMBER_OF_THREADS = PRAEFIX + "standard.amount_of_threads";
    public static final String DATETIME_PATTERN = PRAEFIX + ".standard.date_time_pattern";

    public static final String WATCHDOG_RESTART_INTERVAL = PRAEFIX + ".watchdog.restart_interval";

    public static final String BENUTZER = ".benutzer";
    public static final String PASSWORT = ".passwort";

    public static final String BEHOERDENKENNZEICHEN = ".bhkz";

    public static final String STANDARD_BENUTZER = PRAEFIX + "standard.benutzer";
    public static final String STANDARD_PASSWORT = PRAEFIX + "standard.passwort";
    public static final String STANDARD_BHKZ = PRAEFIX + "standard.bhkz";

    public static final String HOST = ".host";
    public static final String AUSFUEHRUNGSPLAN = ".ausfuehrung";
    public static final String ZEITPUNKT = ".zeitpunkt";

    public static final String STANDARD_HOST = PRAEFIX + ".standard.host";

    public static final String FIXED_RATE = ".fixed-rate";
    public static final String FIXED_DELAY = ".fixed-delay";
    public static final String INITIAL_DELAY = ".initial-delay";

    public static final String STANDARD_FIXED_DATE = "standard.fixed-rate";

    public static final String STANDARD_FIXED_DELAY = "standard.fixed-delay";

    public static final String STANDARD_INITIAL_DELAY = "standard.initial-delay";
}
