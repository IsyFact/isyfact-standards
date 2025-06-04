package de.bund.bva.isyfact.logging;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;

/**
 * Testfälle zum Testen des direkten Loggens mit Logback.
 */
public class LogbackTest extends AbstractLogTest {

    /**
     * Methode zum Testen des einfachen Erstellens von Logeinträgen direkt mit Logback.
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
     * Diese Methode testet das loggen von für JacksonJSON nicht-serialisierbaren Objekten als Werte für
     * Platzhalter in Lognachrichten. "Nicht-serialisierbar" bedeutet in diesem fall, dass das Objekt keine
     * getter und setter und auch keine speziellen Annotationen besitzt. Normalerweise wirft der ObjectMapper
     * beim Abbilden des Objects auf den Text einen Fehler. Dies ist jedoch nicht das gewünschte Verhalten und
     * wird durch IsyJacksonJsonFormatter konfigurativ geändert.
     */
    @Test
    public void testLogbackNichtSerialisierbar() {

        MDC.clear();

        Logger logbackLogger = LoggerFactory.getLogger(LogbackTest.class);

        // Test, bei dem ein nicht-serialisierbares Objekt (ohne getter und
        logbackLogger.info("Type registration [{}] overrides previous : {}", "A", new LogbackTest());

        pruefeLogdatei("testLogbackNichtSerialisierbar");
    }

}
