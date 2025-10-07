package de.bund.bva.isyfact.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.spi.LocationAwareLogger;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.logging.exceptions.FehlerhafterLogeintrag;
import de.bund.bva.isyfact.logging.exceptions.IsyLoggingFehlertextProvider;
import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex;
import de.bund.bva.isyfact.logging.impl.AbstractIsyDatentypMarker;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;

/**
 * test case to create log entries.
 * Testfall zur Erstellung von Logeinträgen.
 */
public class LoggingTest extends AbstractLogTest {

    /** 'Ereignisschlüssel', which will used as default in the tests. */
    private static final String EREIGNISSCHLUESSEL = "EISYLO12345";

    /**
     * test case to create correct log entries for all log levels.
     */
    @Test
    public void testLoggingErfolgreich() {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());

        Exception t = new NullPointerException();
        TechnicalRuntimeException tre =
            new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK,
                this.getClass().getName());

        @SuppressWarnings("serial")
        BaseException te = new TechnicalException(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK,
            new IsyLoggingFehlertextProvider(), this.getClass().getName()) {
        };

        // TRACE
        logger.trace("Dies ist ein {} {} Test. {}, oder?", "super", "trace", "Klasse");

        // DEBUG
        logger.debug("Dies ist ein {} {} Test. {}, oder?", "super", "debug", "Klasse");

        // INFO
        logger
            .info(LogKategorie.JOURNAL, "Dies ist ein {} {} Test. {}, oder?", tre, "super", "info", "Klasse");
        logger
            .info(LogKategorie.JOURNAL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "info", "Klasse");
        logger.info(LogKategorie.METRIK, "ETESTL10000", "Dies ist ein {} {} Test mit {}. {}, oder?", "super",
            "info", "Fachdaten", "Klasse");

        // WARN
        logger.warn("Dies ist ein {} {} Test. {}, oder?", tre, "super", "warn", "Klasse");
        logger.warn("Dies ist ein {} {} Test. {}, oder?", te, "super", "warn", "Klasse");
        logger.warn(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "warn", "Klasse");
        logger.warn(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "warn", "Klasse");

        // ERROR
        logger.error("Dies ist ein {} {} Test. {}, oder?", tre, "super", "error", "Klasse");
        logger.error("Dies ist ein {} {} Test. {}, oder?", te, "super", "error", "Klasse");
        logger.error(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "error", "Klasse");
        logger
            .error(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "error", "Klasse");

        // FATAL
        logger.fatal("Dies ist ein {} {} Test. {}, oder?", tre, "super", "fatal", "Klasse");
        logger.fatal("Dies ist ein {} {} Test. {}, oder?", te, "super", "fatal", "Klasse");
        logger.fatal(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "fatal", "Klasse");
        logger
            .fatal(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "fatal", "Klasse");

        // Tests for Throwable
        String nachricht = "Dies ist ein {} {} Test. {}, oder?";

        logger.error(EREIGNISSCHLUESSEL, nachricht, t, "super", "error", "Klasse");
        logger.fatal(EREIGNISSCHLUESSEL, nachricht, t, "super", "fatal", "Klasse");
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachricht, t, "super", "info", "Klasse");
        logger.warn(EREIGNISSCHLUESSEL, nachricht, t, "super", "warn", "Klasse");

        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL_OHNE_NACHRICHT, (String) null);

        TestBeanKomplex tbk = new TestBeanKomplex(true);

        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachricht, "super", "info", tbk);


        pruefeLogdatei("testLoggingErfolgreich");
    }

    /**
     * test case to create correct log entries for all log levels.
     */
    @Test
    public void testLoggingFachdatenErfolgreich() {
        IsyLoggerFachdaten logger = IsyLoggerFactory.getLogger(this.getClass());

        Exception t = new NullPointerException();
        TechnicalRuntimeException tre =
            new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK,
                this.getClass().getName());

        logger.infoFachdaten(LogKategorie.JOURNAL, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super",
            "info", "Fachdaten", "Klasse");

        @SuppressWarnings("serial")
        BaseException te = new TechnicalException(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK,
            new IsyLoggingFehlertextProvider(), this.getClass().getName()) {
        };

        // TRACE
        logger.traceFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", "super", "trace", "Fachdaten",
            "Klasse");

        // DEBUG
        logger.debugFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", "super", "debug", "Fachdaten",
            "Klasse");

        // INFO
        logger.infoFachdaten(LogKategorie.JOURNAL, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super",
            "info", "Fachdaten", "Klasse");
        logger.infoFachdaten(LogKategorie.JOURNAL, "Dies ist ein {} {} Test mit {}. {}, oder?", te, "super",
            "info", "Fachdaten", "Klasse");
        logger.infoFachdaten(LogKategorie.METRIK, "ETESTL10000", "Dies ist ein {} {} Test mit {}. {}, oder?",
            "super", "info", "Fachdaten", "Klasse");

        // WARN
        logger.warnFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "warn", "Fachdaten",
            "Klasse");
        logger.warnFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "warn", "Fachdaten",
            "Klasse");
        logger.warnFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "warn",
            "Fachdaten");
        logger.warnFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "warn",
            "Fachdaten");

        // ERROR
        logger.errorFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "error", "Fachdaten",
            "Klasse");
        logger.errorFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "error", "Fachdaten",
            "Schön");
        logger.errorFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "error",
            "Fachdaten");
        logger.errorFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "error",
            "Fachdaten");

        // FATAL
        logger.fatalFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "fatal", "Fachdaten",
            "Klasse");
        logger.fatalFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "fatal", "Fachdaten",
            "Klasse");
        logger.fatalFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "fatal",
            "Fachdaten");
        logger.fatalFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "fatal",
            "Fachdaten");

        // Tests for Throwable
        String nachricht = "Dies ist ein {} {} Test. {}, oder?";

        logger.errorFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "error", "Fachdaten");
        logger.fatalFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "fatal", "Fachdaten");
        logger.infoFachdaten(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachricht, t, "super", "info",
            "Fachdaten");
        logger.warnFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "warn", "Fachdaten");

        pruefeLogdatei("testLoggingFachdatenErfolgreich");
    }

    /**
     * test case to create correct log entries with business data for all log levels.
     */
    @Test
    public void testLoggingTypisiertErfolgreich() {
        IsyLoggerTypisiert logger = IsyLoggerFactory.getLogger(this.getClass());

        IsyDatentypMarker marker = new AbstractIsyDatentypMarker("Testdaten") {
        };

        Exception t = new NullPointerException();
        TechnicalRuntimeException tre =
            new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK,
                this.getClass().getName());

        logger.info(LogKategorie.JOURNAL, marker, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super",
            "info", "Fachdaten", "Klasse");

        @SuppressWarnings("serial")
        BaseException te = new TechnicalException(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK,
            new IsyLoggingFehlertextProvider(), this.getClass().getName()) {
        };

        // TRACE
        logger.trace(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", "super", "trace", "Fachdaten",
            "Klasse");

        // DEBUG
        logger.debug(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", "super", "debug", "Fachdaten",
            "Klasse");

        // INFO
        logger.info(LogKategorie.JOURNAL, marker, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super",
            "info", "Fachdaten", "Klasse");
        logger.info(LogKategorie.JOURNAL, marker, "Dies ist ein {} {} Test mit {}. {}, oder?", te, "super",
            "info", "Fachdaten", "Klasse");
        logger.info(LogKategorie.METRIK, marker, "ETESTL10000", "Dies ist ein {} {} Test mit {}. {}, oder?",
            "super", "info", "Fachdaten", "Klasse");

        // WARN
        logger.warn(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "warn", "Fachdaten",
            "Klasse");
        logger.warn(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "warn", "Fachdaten",
            "Klasse");
        logger.warn(marker, EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "warn",
            "Fachdaten");
        logger.warn(marker, EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "warn",
            "Fachdaten");

        // ERROR
        logger.error(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "error", "Fachdaten",
            "Klasse");
        logger.error(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "error", "Fachdaten",
            "Schön");
        logger.error(marker, EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "error",
            "Fachdaten");
        logger.error(marker, EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "error",
            "Fachdaten");

        // FATAL
        logger.fatal(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "fatal", "Fachdaten",
            "Klasse");
        logger.fatal(marker, "Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "fatal", "Fachdaten",
            "Klasse");
        logger.fatal(marker, EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "fatal",
            "Fachdaten");
        logger.fatal(marker, EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "fatal",
            "Fachdaten");

        // Tests for Throwable
        String nachricht = "Dies ist ein {} {} Test. {}, oder?";

        logger.error(marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "error", "Fachdaten");
        logger.fatal(marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "fatal", "Fachdaten");
        logger.info(LogKategorie.JOURNAL, marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "info",
            "Fachdaten");
        logger.warn(marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "warn", "Fachdaten");

        pruefeLogdatei("testLoggingTypisiertErfolgreich");
    }

    /**
     * test case to create correct log entries for all log levels in case of wrong calls.
     *
     * @throws Exception if an exception is thrown during the test.
     */
    @Test
    public void testLoggingFehlerhaft() throws Exception {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(LoggingTest.class);

        // too much parameters
        logger.debug(KENNZEICHNUNG_FEHLERTEST + " - Dies ist ein {} {} Test. {}, oder?", "super", "debug",
            "Klasse", "noch einer", "noch einer", "noch einer");

        // not enough parameters
        logger.debug(KENNZEICHNUNG_FEHLERTEST + " - Dies ist ein {} {} Test. {}");

        pruefeLogdatei("testLoggingFehlerhaft");

        try {
            // info log entry without key causes an exception.
            logger.info(LogKategorie.JOURNAL, null, "Eine Nachricht.");
            Assertions.fail(
                "Erstellen eines Logeintrags in Level INFO ohne Schlüssel führte nicht zu einem Fehler.");
        } catch (FehlerhafterLogeintrag fle) {
            Assertions.assertEquals("ISYLO00001", fle.getAusnahmeId());
        }

        try {
            // info log entry without category causes an exception. The inferface prohibts this until now.
            // so we are calling the internal log method without reflection.
            // Test for INFO
            Method interneLogmethode = IsyLocationAwareLoggerImpl.class
                .getDeclaredMethod("log", int.class, String.class, IsyMarker[].class, String.class,
                    String.class, Object[].class, Throwable.class);
            interneLogmethode.setAccessible(true);

            interneLogmethode
                .invoke(logger, LocationAwareLogger.INFO_INT, null, new IsyMarker[0], EREIGNISSCHLUESSEL,
                    "Eine Nachricht.", null, null);
            Assertions.fail(
                "Erstellen eines Logeintrags in Level INFO ohne Kategorie führte nicht zu einem Fehler.");
        } catch (InvocationTargetException ite) {
            FehlerhafterLogeintrag fle = (FehlerhafterLogeintrag) ite.getCause();
            Assertions.assertEquals("ISYLO00002", fle.getAusnahmeId());
        }

        try {
            // info log entry without category causes an exception. The inferface prohibts this until now.
            // so we are calling the internal log method without reflection.
            // Test for ERROR
            Method interneLogmethode = IsyLocationAwareLoggerImpl.class
                .getDeclaredMethod("log", int.class, String.class, IsyMarker[].class, String.class,
                    String.class, Object[].class, Throwable.class);
            interneLogmethode.setAccessible(true);

            interneLogmethode
                .invoke(logger, LocationAwareLogger.ERROR_INT, null, new IsyMarker[0], EREIGNISSCHLUESSEL,
                    "Eine Nachricht.", null, null);
            Assertions.fail(
                "Erstellen eines Logeintrags in Level INFO ohne Kategorie führte nicht zu einem Fehler.");
        } catch (InvocationTargetException ite) {
            FehlerhafterLogeintrag fle = (FehlerhafterLogeintrag) ite.getCause();
            Assertions.assertEquals("ISYLO00002", fle.getAusnahmeId());
        }
    }

    /**
     * test of the internal method 'ermittleLevelString' which is used only for processing messages of
     * own exceptions.
     *
     * @throws Exception if an exception is thrown during the test.
     */
    @Test
    public void testErmittleLevelString() throws Exception {
        Method interneErmittleLevelMethode =
            IsyLocationAwareLoggerImpl.class.getDeclaredMethod("ermittleLevelString", int.class);
        interneErmittleLevelMethode.setAccessible(true);

        IsyLogger logger = IsyLoggerFactory.getLogger(LoggingTest.class);

        Assertions
            .assertEquals("DEBUG", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.DEBUG_INT));
        Assertions
            .assertEquals("ERROR", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.ERROR_INT));
        Assertions.assertEquals("INFO", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.INFO_INT));
        Assertions
            .assertEquals("TRACE", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.TRACE_INT));
        Assertions.assertEquals("WARN", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.WARN_INT));
        Assertions.assertNull(interneErmittleLevelMethode.invoke(logger, -5));
    }

    /**
     * test case to create log entries which exceed the maximum size limits.
     */
    @Test
    public void testLogeintragZuGross() throws Exception {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());

        // String of special characters (2 Byte) for the exception
        String exceptionText = String.join("", Collections.nCopies(2000, "ä"));

        FehlerhafterLogeintrag fl =
            new FehlerhafterLogeintrag(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE, exceptionText);

        // creating strings for test cases
        String nachrichtTestfall3 = String.join("", Collections.nCopies(8000, "a"));

        FehlerhafterLogeintrag flB =
            new FehlerhafterLogeintrag(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE,
                nachrichtTestfall3);

        // creating strings for test cases
        String nachrichtA = String.join("", Collections.nCopies(3000, "ab123üö%7/"));

        String nachrichtB = String.join("", Collections.nCopies(3000, "aaaaaaaaaa"));


        List<String> arrayListA = IntStream
            .range(0, 2000).mapToObj(i -> "" + nachrichtA.charAt(i) + nachrichtB.charAt(i)).collect(Collectors.toList());

        // test case 1: test the level requirement - test message is level debug
        logger.debug("Diese Nachricht ist zu lang: {}", nachrichtA);

        // test case 2: test of the maximum length: test message is too small
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Diese Nachricht ist zu gross.");

        // test case 3: test of the maximum length, test message ist too small but bigger than 16.000 chars
        logger.
            info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Diese Nachricht ist zu gross. {}",
                nachrichtTestfall3);

        // test case 4: test parameter removement, test message is too big and contains parameter
        // after parameter removal the test message is no longer too big
        logger.
            info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Diese Nachricht ist zu gross. {} und {} ",
                nachrichtTestfall3, nachrichtTestfall3, nachrichtTestfall3);

        // test case 5: too large log entry with exceptions
        // number of characters in the exception is smaller than overhang, but length of bytes is bigger
        // exception is shortened and than noted that the shortening was sufficient
        logger.info(LogKategorie.JOURNAL, nachrichtB, fl);

        // test case 6: too large log entry with exception and too long message.
        // number of characters in the exception is smaller than overhang, but length of bytes is bigger
        // exception is shortened and than noted that the shortening was not sufficient
        // the message is then shortened.
        logger.info(LogKategorie.JOURNAL, "Zu langer Logeintrag: {}", fl, nachrichtA);

        // test case 7: too large log entry because of too large parameter and to large log message
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Zu langer Logeintrag: {} {}", nachrichtA,
            arrayListA);

        // test case 8: too large log entry with exception
        // number of characters of the exception is smaller than overhang
        logger.info(LogKategorie.JOURNAL, nachrichtB, flB);

        // test case 9: too large log entry without exception
        // number of characters of the exception is bigger than overhang
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachrichtB + nachrichtB);

        // test case 10: too large log entry with exception
        // the exception is smaller than overhang, log message is bigger than overhang
        logger.info(LogKategorie.JOURNAL, nachrichtB + nachrichtA + nachrichtB + nachrichtB, flB);

        // test case 11: too big log entry, which can not be shortened
        // parameter maxLength is set to 50 Bytes to create an overhang which can not be shortened.

        LogbackConfigTest logbackConfig = new LogbackConfigTest();

        logbackConfig.konfiguriereLogback("logback-maxLength-test.xml");

        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachrichtB, nachrichtA, nachrichtB);

        logbackConfig.konfiguriereLogback("logback-test.xml");
        // test case 12: test of too big log entry on level ERROR
        logger.error(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE, "Zu große Error Nachricht {}",
            nachrichtA);

        // test case 13: test of too big log entry on level WARN
        logger.warn(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE, "Zu große WARN Nachricht {}",
            nachrichtA);

        pruefeLogdatei("testLogEintragZuGross");
    }

}
