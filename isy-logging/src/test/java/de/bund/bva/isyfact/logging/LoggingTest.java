package de.bund.bva.isyfact.logging;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.logging.exceptions.FehlerhafterLogeintrag;
import de.bund.bva.isyfact.logging.exceptions.IsyLoggingFehlertextProvider;
import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex;
import de.bund.bva.isyfact.logging.impl.AbstractIsyDatentypMarker;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;
import de.bund.bva.isyfact.exception.TechnicalException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Testfall zur Erstellung von Logeinträgen.
 */
public class LoggingTest extends AbstractLogTest {

    /** Ereignisschlüssel, der standardmäßig für die Tests verwendet wird. */
    private static final String EREIGNISSCHLUESSEL = "EISYLO12345";

    /**
     * Testfall zum Erstellen korrekter Standard-Logeinträge in allen Log-Leveln.
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

        // Tests für Throwable
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
     * Testfall zum Erstellen korrekter Logeinträge mit Fachdaten in allen Log-Leveln.
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

        // Tests für Throwable
        String nachricht = "Dies ist ein {} {} Test. {}, oder?";

        logger.errorFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "error", "Fachdaten");
        logger.fatalFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "fatal", "Fachdaten");
        logger.infoFachdaten(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachricht, t, "super", "info",
            "Fachdaten");
        logger.warnFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "warn", "Fachdaten");

        pruefeLogdatei("testLoggingFachdatenErfolgreich");
    }

    /**
     * Testfall zum Erstellen korrekter Logeinträge mit Fachdaten in allen Log-Leveln.
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

        // Tests für Throwable
        String nachricht = "Dies ist ein {} {} Test. {}, oder?";

        logger.error(marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "error", "Fachdaten");
        logger.fatal(marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "fatal", "Fachdaten");
        logger.info(LogKategorie.JOURNAL, marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "info",
            "Fachdaten");
        logger.warn(marker, EREIGNISSCHLUESSEL, nachricht, t, "super", "warn", "Fachdaten");

        pruefeLogdatei("testLoggingTypisiertErfolgreich");
    }

    /**
     * Testfall zum Erstellen korrekter Logeinträge in allen Log-Leveln, bei fehlerhaften Aufrufen.
     *
     * @throws Exception Wenn bei der Testausführung eine Exception auftritt.
     */
    @Test
    public void testLoggingFehlerhaft() throws Exception {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(LoggingTest.class);

        // Zu viele Parameter
        logger.debug(KENNZEICHNUNG_FEHLERTEST + " - Dies ist ein {} {} Test. {}, oder?", "super", "debug",
            "Klasse", "noch einer", "noch einer", "noch einer");

        // Zu wenige Parameter
        logger.debug(KENNZEICHNUNG_FEHLERTEST + " - Dies ist ein {} {} Test. {}");

        pruefeLogdatei("testLoggingFehlerhaft");

        try {
            // Logeintrag in Info ohne Schlüssel führt zu Exception.
            logger.info(LogKategorie.JOURNAL, null, "Eine Nachricht.");
            Assert.fail(
                "Erstellen eines Logeintrags in Level INFO ohne Schlüssel führte nicht zu einem Fehler.");
        } catch (FehlerhafterLogeintrag fle) {
            Assert.assertEquals("ISYLO00001", fle.getAusnahmeId());
        }

        try {
            // Logeintrag in Info ohne Kategorie führt zu Exception. Dies ist wird durch das Interface bisher
            // verhindert. Daher wird die interne Log-Methode direkt per Reflection aufgerufen.
            // Test für INFO
            Method interneLogmethode = IsyLocationAwareLoggerImpl.class
                .getDeclaredMethod("log", int.class, String.class, IsyMarker[].class, String.class,
                    String.class, Object[].class, Throwable.class);
            interneLogmethode.setAccessible(true);

            interneLogmethode
                .invoke(logger, LocationAwareLogger.INFO_INT, null, new IsyMarker[0], EREIGNISSCHLUESSEL,
                    "Eine Nachricht.", null, null);
            Assert.fail(
                "Erstellen eines Logeintrags in Level INFO ohne Kategorie führte nicht zu einem Fehler.");
        } catch (InvocationTargetException ite) {
            FehlerhafterLogeintrag fle = (FehlerhafterLogeintrag) ite.getCause();
            Assert.assertEquals("ISYLO00002", fle.getAusnahmeId());
        }

        try {
            // Logeintrag in Info ohne Kategorie führt zu Exception. Dies ist wird durch das Interface bisher
            // verhindert. Daher wird die interne Log-Methode direkt per Reflection aufgerufen.
            // Test für ERROR
            Method interneLogmethode = IsyLocationAwareLoggerImpl.class
                .getDeclaredMethod("log", int.class, String.class, IsyMarker[].class, String.class,
                    String.class, Object[].class, Throwable.class);
            interneLogmethode.setAccessible(true);

            interneLogmethode
                .invoke(logger, LocationAwareLogger.ERROR_INT, null, new IsyMarker[0], EREIGNISSCHLUESSEL,
                    "Eine Nachricht.", null, null);
            Assert.fail(
                "Erstellen eines Logeintrags in Level INFO ohne Kategorie führte nicht zu einem Fehler.");
        } catch (InvocationTargetException ite) {
            FehlerhafterLogeintrag fle = (FehlerhafterLogeintrag) ite.getCause();
            Assert.assertEquals("ISYLO00002", fle.getAusnahmeId());
        }
    }

    /**
     * Testet die interne Methode "ermittleLevelString" die nur für die Aufbereitung von Meldungen eigener
     * Exceptions verwendet wird.
     *
     * @throws Exception Wenn bei der Testausführung eine Exception auftritt.
     */
    @Test
    public void testErmittleLevelString() throws Exception {
        Method interneErmittleLevelMethode =
            IsyLocationAwareLoggerImpl.class.getDeclaredMethod("ermittleLevelString", int.class);
        interneErmittleLevelMethode.setAccessible(true);

        IsyLogger logger = IsyLoggerFactory.getLogger(LoggingTest.class);

        Assert
            .assertEquals("DEBUG", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.DEBUG_INT));
        Assert
            .assertEquals("ERROR", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.ERROR_INT));
        Assert.assertEquals("INFO", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.INFO_INT));
        Assert
            .assertEquals("TRACE", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.TRACE_INT));
        Assert.assertEquals("WARN", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.WARN_INT));
        Assert.assertNull(interneErmittleLevelMethode.invoke(logger, -5));
    }

    /**
     * Testfall zum Erstellen verschiedener Loeinträge, die die Größe eines maximalen Logeintrags übertreffen
     */
    @Test
    public void testLogeintragZuGross() throws Exception {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());

        // String aus Sonderzeichen (2 Byte) für die Exception
        String exceptionText = String.join("", Collections.nCopies(2000, "ä"));

        FehlerhafterLogeintrag fl =
            new FehlerhafterLogeintrag(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE, exceptionText);

        // Strings für Testfälle erzeugen
        String nachrichtTestfall3 = String.join("", Collections.nCopies(8000, "a"));

        FehlerhafterLogeintrag flB =
            new FehlerhafterLogeintrag(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE,
                nachrichtTestfall3);

        // Strings für Testfälle erzeugen
        String nachrichtA = String.join("", Collections.nCopies(3000, "ab123üö%7/"));

        String nachrichtB = String.join("", Collections.nCopies(3000, "aaaaaaaaaa"));


        List<String> arrayListA = IntStream
            .range(0, 2000).mapToObj(i -> "" + nachrichtA.charAt(i) + nachrichtB.charAt(i)).collect(Collectors.toList());

        // Testfall 1: Test der Level-Bedignung - Testnachricht besitzt Level Debug
        logger.debug("Diese Nachricht ist zu lang: {}", nachrichtA);

        // Testfall 2: Test der maximalen Länge Bedingung: Testnachricht ist zu klein
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Diese Nachricht ist zu gross.");

        // Testfall 3: Test der maximalen Größe, Testnachricht ist zu klein aber größer als 16.000 Zeichen
        logger.
            info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Diese Nachricht ist zu gross. {}",
                nachrichtTestfall3);

        // Testfall 4: Test der Parameterentfernung, Testnachricht ist zu groß und enthält Parameter
        // Nach Entfernung der Parameter ist die Testnachricht nicht mehr zu lang
        logger.
            info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Diese Nachricht ist zu gross. {} und {} ",
                nachrichtTestfall3, nachrichtTestfall3, nachrichtTestfall3);

        // Testfall 5: Zu großer Logeintrag mit Exception,
        // Anzahl Zeichen der Exception ist kleiner als der Überhang, Länge in Bytes aber größer
        // Exception wird gekuerzt und anschließend wird festgestellt, dass die Kürzung ausreichend war
        logger.info(LogKategorie.JOURNAL, nachrichtB, fl);

        // Testfall 6: Zu großer Logeintrag mit Exception und zu langer Nachricht,
        // Anzahl Zeichen der Exception ist kleiner als der Überhang, Länge in Bytes aber größer
        // Exception wird gekuerzt und anschließend wird festgestellt, dass die Kürzung nicht ausreichend war
        // Das Feld Nachricht wird anschließend gekürzt
        logger.info(LogKategorie.JOURNAL, "Zu langer Logeintrag: {}", fl, nachrichtA);

        // Testfall 7: Zu großer Logeintrag aufgrund zu großer Paramter und zu großer Lognachricht
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, "Zu langer Logeintrag: {} {}", nachrichtA,
            arrayListA);

        // Testfall 8: Zu großer Logeintrag mit Exception
        // Anzahl Zeichen der Exception ist kleiner als der Überhang
        logger.info(LogKategorie.JOURNAL, nachrichtB, flB);

        // Testfall 9: Zu großer Logeintrag ohne Exception
        // Anzahl Zeichen der Nachricht ist größer als der Überhang
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachrichtB + nachrichtB);

        // Testfall 10: Zu großer Logeintrag mit Exception
        // Die Exception ist kleiner als der Überhang
        // Die Lognachricht ist größer als der Überhang
        logger.info(LogKategorie.JOURNAL, nachrichtB + nachrichtA + nachrichtB + nachrichtB, flB);

        // Testfall 11: Zu großer Logeintrag, der nicht ausreichend gekürzt werden kann
        // Um einen Überhang zu erzeugen, der nicht weiter gekürzt werden kann,
        // wird der Parameter maxLength auf 50 Bytes gesetzt.

        LogbackConfigTest logbackConfig = new LogbackConfigTest();

        logbackConfig.konfiguriereLogback("logback-maxLength-test.xml");

        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachrichtB, nachrichtA, nachrichtB);

        logbackConfig.konfiguriereLogback("logback-test.xml");
        // Testfall 12: Test eines zu großen Logeintrags mit dem Level ERROR
        logger.error(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE, "Zu große Error Nachricht {}",
            nachrichtA);

        // Testfall 13: Test eines zu großen Logeintrags mit dem Level WARN
        logger.warn(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE, "Zu große WARN Nachricht {}",
            nachrichtA);

        pruefeLogdatei("testLogEintragZuGross");
    }

}
