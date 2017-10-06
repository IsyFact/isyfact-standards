package de.bund.bva.isyfact.task.konstanten;

/**
 * Die Konfigurationsschlüssel für den isy-timer
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public abstract class KonfigurationSchluessel {
    public static final String PRAEFIX = "isyfact.task.";

    public static final String INITIAL_NUMBER_OF_THREADS = PRAEFIX + "standard.amount_of_threads";
    public static final String DATETIME_PATTERN = PRAEFIX + ".standard.date_time_pattern";

    public static final String BENUTZER = ".benutzer";
    public static final String PASSWORT = ".passwort";
    public static final String OPERATION = ".operation";
    public static final String HOST = ".host";
    public static final String AUSFUEHRUNGSPLAN = ".ausfuehrungsplan";
    public static final String EXECUTIONDATETIME = ".executionDateTime";

    public static final String FIXEDRATEDAYS = ".fixedRate.days";
    public static final String FIXEDRATEHOURS = ".fixedRate.hours";
    public static final String FIXEDRATEMINUTES = ".fixedRate.minutes";
    public static final String FIXEDRATESECONDS = ".fixedRate.seconds";

    public static final String FIXEDDELAYDAYS = ".fixedDelay.days";
    public static final String FIXEDDELAYHOURS = ".fixedDelay.hours";
    public static final String FIXEDDELAYMINUTES = ".fixedDelay.minutes";
    public static final String FIXEDDELAYSECONDS = ".fixedDelay.seconds";



}
