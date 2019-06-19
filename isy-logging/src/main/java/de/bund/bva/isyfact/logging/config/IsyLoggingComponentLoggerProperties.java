package de.bund.bva.isyfact.logging.config;

/**
 * Properties, die das Loggen an Komponentengrenzen konfigurieren.
 */
public class IsyLoggingComponentLoggerProperties extends AbstractBoundaryLoggerProperties {

    /**
     * Erzeugt eine neue Instanz der Konfigurationsparameter.
     */
    public IsyLoggingComponentLoggerProperties() {
        // Default-Werte setzen
        setPointcut("@within(de.bund.bva.isyfact.logging.annotation.Komponentengrenze)");
        setLoggeDauer(false);
        setLoggeAufruf(false);
        setLoggeErgebnis(false);
        setLoggeDaten(false);
        setLoggeDatenBeiException(false);
    }

}