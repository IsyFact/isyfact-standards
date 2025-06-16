package de.bund.bva.isyfact.batchrahmen.batch.konstanten;

/**
 * Contains all keys for configuration parameters.
 */
public final class KonfigurationSchluessel {

    private KonfigurationSchluessel() {
        // class with config keys should not be initialized
    }

    /***************************************************************************
     * PROPERTY - PARAMETER *
     **************************************************************************/

    /**
     * Property name for the Batchrahmen-bean.
     */
    public static final String PROPERTY_BATCHRAHMEN_BEAN_NAME = "Batchrahmen.BeanName";

    /**
     * Property name for the logback configuration.
     */
    public static final String PROPERTY_BATCHRAHMEN_LOGBACK_CONF = "Batchrahmen.LogbackConfigFile";

    /**
     * Property name for the commit interval.
     */
    public static final String PROPERTY_BATCHRAHMEN_COMMIT_INTERVALL = "Batchrahmen.CommitIntervall";

    /**
     * Property name for the number of records to process.
     */
    public static final String PROPERTY_BATCHRAHMEN_ZU_VERARBEITENDE_DATENSAETZE_ANZAHL =
            "Batchrahmen.AnzahlZuVerarbeitendeDatensaetze";

    /**
     * Property name for the result file.
     */
    public static final String PROPERTY_BATCHRAHMEN_ERGEBNIS_DATEI = "Batchrahmen.Ergebnisdatei";

    /**
     * Property name for the clear interval (clearing the Hibernate session cache).
     */
    public static final String PROPERTY_BATCHRAHMEN_CLEAR_INTERVALL = "Batchrahmen.ClearIntervall";

    /**
     * Property name for the name of the Ausfuehrungs-bean.
     */
    public static final String PROPERTY_AUSFUEHRUNGSBEAN = "AusfuehrungsBean";

    /**
     * Property name for the ID of the batch.
     */
    public static final String PROPERTY_BATCH_ID = "BatchId";

    /**
     * Property name for the name of the batch.
     */
    public static final String PROPERTY_BATCH_NAME = "BatchName";

    /**
     * Property name for the oauth2ClientRegistrationId of the batch.
     */
    public static final String PROPERTY_BATCH_OAUTH2_CLIENT_REGISTRATION_ID = "batch.oauth2ClientRegistrationId";

    /**
     * Property name for the time frame before expiry in seconds during which a token is considered as already expired
     * before the actual token expiration.
     */
    public static final String PROPERTY_BATCH_OAUTH2_MINIMUM_TOKEN_VALIDITY = "batch.oauth2MinimumTokenValidity";

    /***************************************************************************
     * COMMAND LINE PARAMETERS *
     **************************************************************************/

    /**
     * Command line parameters for the property file.
     */
    public static final String KOMMANDO_PARAM_PROP_DATEI = "cfg";

    /**
     * Command line parameter for the start type of the batch: Start.
     */
    public static final String KOMMANDO_PARAM_START = "start";

    /**
     * Command line parameter for the start type of the batch: Restart.
     */
    public static final String KOMMANDO_PARAM_RESTART = "restart";

    /**
     * Command line parameter for restarting even if aborted.
     */
    public static final String KOMMANDO_PARAM_IGNORIERE_RESTART = "ignoriereRestart";

    /**
     * Command line parameter that states that restart is also done on run.
     */
    public static final String KOMMANDO_PARAM_IGNORIERE_LAUF = "ignoriereLauf";

    /**
     * Command line parameter for the maximum runtime of the batch (optional).
     */
    public static final String KOMMANDO_PARAM_LAUFZEIT = "laufzeit";

}
