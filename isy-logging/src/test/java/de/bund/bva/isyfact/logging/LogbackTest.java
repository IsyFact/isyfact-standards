package de.bund.bva.isyfact.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;

/**
 * test cases for direct logging with logback
 */
public class LogbackTest extends AbstractLogTest {

    /**
     * test method for simple creation of log entries with logback
     */
    @Test
    public void testLogback() {

        MDC.clear();

        Marker marker = MarkerFactory.getMarker("WICHTIG");

        Logger logbackLogger = LoggerFactory.getLogger(LogbackTest.class);
        logbackLogger.debug("Dies ist eine Debug-Logausgabe mit Logback {} {}", 1, 2);
        logbackLogger.info("Dies ist eine Info-Logausgabe mit Logback {} {}", 1, 2,
                new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK, this.getClass()
                        .getName()));
        logbackLogger.warn("Dies ist eine Warn-Logausgabe mit Logback {} {}", 1, 2);

        logbackLogger.debug(marker, "Dies ist eine Debug-Logausgabe mit Logback {} {}", 1, 2);
        logbackLogger.info(marker, "Dies ist eine Info-Logausgabe mit Logback {} {}", 1, 2,
                new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK, this.getClass()
                        .getName()));
        logbackLogger.warn(marker, "Dies ist eine Warn-Logausgabe mit Logback {} {}", 1, 2);

        pruefeLogdatei("testLogback");

    }

    /**
     * This method tests logging of objects which are for JacksonJSON not sericlizable and are
     * used as values of placeholder in log messages. 'Non-sericlizable' means that this object
     * has no getter and setter and no special annotations. The object mapper would normally throw
     * an exception for the mapping of the object to the error text.
     * This is not the wanted result and therefore is changed by configuration of the
     * IsyJacksonJsonFormatter
     */
    @Test
    public void testLogbackNichtSerialisierbar() {

        MDC.clear();

        Logger logbackLogger = LoggerFactory.getLogger(LogbackTest.class);

        // test with a not serializable object
        logbackLogger.info("Type registration [{}] overrides previous : {}", "A", new LogbackTest());

        pruefeLogdatei("testLogbackNichtSerialisierbar");
    }

}
