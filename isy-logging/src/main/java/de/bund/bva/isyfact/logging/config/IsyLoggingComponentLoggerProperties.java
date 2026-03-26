package de.bund.bva.isyfact.logging.config;

/**
 * Properties that configure logging at system boundaries.
 */
public class IsyLoggingComponentLoggerProperties extends AbstractBoundaryLoggerProperties {

    /**
     * Creates a new instance of the configuration parameters.
     */
    public IsyLoggingComponentLoggerProperties() {
        applyDefaults();
    }

    private void applyDefaults() {
        // Set default values
        super.setPointcut("@within(de.bund.bva.isyfact.logging.annotation.Komponentengrenze)");
        super.setLoggeDauer(false);
        super.setLoggeAufruf(false);
        super.setLoggeErgebnis(false);
        super.setLoggeDaten(false);
        super.setLoggeDatenBeiException(false);
    }

}