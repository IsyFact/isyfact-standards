package de.bund.bva.isyfact.logging.config;

/**
 * Properties that configure logging at system boundaries.
 */
public class IsyLoggingBoundaryLoggerProperties extends AbstractBoundaryLoggerProperties {

    /**
     * Creates a new instance of the configuration parameters.
     */
    public IsyLoggingBoundaryLoggerProperties() {
        applyDefaults();
    }

    private void applyDefaults() {
        // Set default values
        super.setPointcut("@within(de.bund.bva.isyfact.logging.annotation.Systemgrenze)");
        super.setLoggeDauer(true);
        super.setLoggeAufruf(true);
        super.setLoggeErgebnis(true);
        super.setLoggeDaten(false);
        super.setLoggeDatenBeiException(true);
    }

}
