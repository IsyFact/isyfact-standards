package de.bund.bva.isyfact.logging;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;

/**
 * Spezifische Logger-Factory gemäß des Bausteins IsyFact-Logging. Diese Klasse wird zum Erstellen von
 * Logger-Instanzen verwendet.
 * 
 */
public final class IsyLoggerFactory {

    /**
     * Privater Konstruktor der Klasse. Verhindert, dass Instanzen der Klasse angelegt werden.
     * 
     */
    private IsyLoggerFactory() {
    }

    /**
     * Map mit allen erstellten loggern als simpler Cache. Verhindert, dass Logger zu einer Klasse mehrfach
     * erzeugt werden.
     */
    private static final Map<String, IsyLogger> LOGGER_CACHE = new HashMap<>();

    /**
     * Erstellt einen Logger für die übergebene Klasse. Als Name, wird der absolute Pfad der Klasse verwendet.
     * 
     * @param klasse
     *            Klasse, für die ein Logger erstellt werden soll.
     * @return der erstellte Logger.
     */
    public static IsyLogger getLogger(Class<?> klasse) {

        String klassenName = klasse.getName();

        IsyLogger isyLogger = LOGGER_CACHE.get(klassenName);
        if (isyLogger != null) {
            return isyLogger;
        } else {
            Logger logger = LoggerFactory.getLogger(klassenName);
            pruefeLoggerImplementierung(logger);
            LocationAwareLogger locationAwareLogger = (LocationAwareLogger) logger;
            isyLogger = new IsyLocationAwareLoggerImpl(locationAwareLogger);
            LOGGER_CACHE.put(klassenName, isyLogger);
            return isyLogger;
        }
    }

    /**
     * Hilfsmethode zum Prüfen, ob die Logger-Implementierung unterstützt wird.
     * 
     * @param logger der zu überprüfende Logger.
     * @throws LogKonfigurationFehler
     *             falls die Logger-Implementierung nicht unterstützt wird.
     */
    private static void pruefeLoggerImplementierung(Object logger) {
        if (!(logger instanceof LocationAwareLogger)) {
            throw new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK, logger.getClass()
                    .getName());
        }
    }

}
