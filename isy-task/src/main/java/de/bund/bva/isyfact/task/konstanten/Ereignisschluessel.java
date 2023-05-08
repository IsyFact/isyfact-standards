package de.bund.bva.isyfact.task.konstanten;

/**
 * The event keys of isy-task.
 */
public abstract class Ereignisschluessel {

    /** Info key if task was cancelled. **/
    public static final String TASK_WURDE_ABGEBROCHEN = "ISYTA10001";

    /** Info key if task was finished incorrectly. **/
    public static final String TASK_WURDE_FEHLERHAFT_BEENDET = "ISYTA10002";

    /** Info key for deactivated task. **/
    public static final String TASK_DEAKTIVIERT = "ISYTA10003";

    /** General warning key in case there are errors in metrics processing. **/
    public static final String METRIC_WARNUNG = "ISYTA10004";


}
