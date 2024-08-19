package de.bund.bva.isyfact.task.konstanten;

/**
 * The hint keys of isy-task.
 */
public final class HinweisSchluessel {

    private HinweisSchluessel () {
        // hide constructor, this class contains static content, there is no need to instantiate this class.
    }

    /** Info code if standard configuration is used. **/
    public static final String VERWENDE_STANDARD_KONFIGURATION = "ISYTA20001";

    /** Info code if no authentication is used. **/
    public static final String VERWENDE_KEINE_AUTHENTIFIZIERUNG = "ISYTA20002";

    /** Info code if standard configuration is used. **/
    public static final String FALSCHER_HOSTNAME = "ISYTA20003";
}
