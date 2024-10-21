package de.bund.bva.isyfact.task.konstanten;

/**
 * The error keys of isy-task.
 */
public final class FehlerSchluessel {

    private FehlerSchluessel () {
        // hide constructor, this class contains static content, there is no need to instantiate this class.
    }

    public static final String HOSTNAME_STIMMT_NICHT_UEBEREIN = "ISYTA00001";

    public static final String TASK_KONNTE_NICHT_EINGEREIHT_WERDEN = "ISYTA00002";

    public static final String TASK_KONFIGURATION_UNGUELTIG = "ISYTA00003";

}
