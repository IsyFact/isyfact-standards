package de.bund.bva.isyfact.logging.config;

/**
 * Properties, die das Loggen an Systemgrenzen konfigurieren.
 */
public class IsyLoggingBoundaryLoggerProperties extends AbstractBoundaryLoggerProperties {

    /**
     * Erzeugt eine neue Instanz der Konfigurationsparameter.
     */
    public IsyLoggingBoundaryLoggerProperties() {
        // Default-Werte setzen
        setPointcut("@within(de.bund.bva.isyfact.logging.annotation.Systemgrenze)");
        setLoggeDauer(true);
        setLoggeAufruf(true);
        setLoggeErgebnis(true);
        setLoggeDaten(false);
        setLoggeDatenBeiException(true);
    }

}
