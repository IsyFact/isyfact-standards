package de.bund.bva.isyfact.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielParameterPerson;
import de.bund.bva.isyfact.logging.util.LoggingMethodInvoker;

/**
 * Test cases for the LogMethodInvoker.
 * 
 */
public class LogMethodInvokerTest extends AbstractLogTest {

    /**
     * Testing the creation of log entries for successful method calls.
     * 
     * @throws Throwable
     *             if an error occurs in the test.
     */
    @Test
    public void testAufrufErfolgreich() throws Throwable {

        boolean loggeDauer = true;
        boolean loggeAufruf = true;
        boolean loggeErgebnis = true;
        boolean loggeDatenBeiException = true;
        boolean loggeDaten = false;
        long loggeMaximaleParameterGroesse = 1000000;

        Method methode = TestZielKlasse.class.getMethod("setzeName", TestZielParameterPerson.class,
                String.class);

        LoggingMethodInvoker<TestZielParameterPerson> methodInvoker =
            new LoggingMethodInvoker<>(methode, IsyLoggerFactory.getLogger(TestZielKlasse.class), loggeAufruf,
                loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException, loggeMaximaleParameterGroesse);

        methodInvoker.fuehreMethodeAus(new TestZielKlasse(), new TestZielParameterPerson("Mustermann", "Max",
                "Peter", "Hans"), "TestParameter 2");

        pruefeLogdatei("testAufrufErfolgreich");

    }

    /**
     * Testing the creation of log entries for not successful method calls (Exception).
     * 
     * @throws Exception
     *             if an error occurs in the test.
     */
    @Test
    public void testAufrufMitException() throws Exception {

        boolean loggeDauer = true;
        boolean loggeAufruf = true;
        boolean loggeErgebnis = true;
        boolean loggeDatenBeiException = true;
        boolean loggeDaten = false;
        long loggeMaximaleParameterGroesse = 1000000;

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        LoggingMethodInvoker<TestZielParameterPerson> methodInvoker =
            new LoggingMethodInvoker<>(methode, IsyLoggerFactory.getLogger(TestZielKlasse.class), loggeAufruf,
                loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException, loggeMaximaleParameterGroesse);

        try {
            methodInvoker.fuehreMethodeAus(new TestZielKlasse(), new TestZielParameterPerson("Mustermann",
                    "Max", "Peter", "Hans"), "TestParameter 2");
            Assertions.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            pruefeLogdatei("testAufrufMitException", true);
        }

    }

    /**
     * Testing the creation of log entries for successful method calls in a neighbor system.
     * 
     * @throws Throwable
     *             if an error occurs in the test.
     */
    @Test
    public void testAufrufErfolgreichNachbarsystem() throws Throwable {

        boolean loggeDauer = true;
        boolean loggeAufruf = true;
        boolean loggeErgebnis = true;
        boolean loggeDaten = false;
        boolean loggeDatenBeiException = true;
        long loggeMaximaleParameterGroesse = 1000000;

        Method methode = TestZielKlasse.class.getMethod("setzeName", TestZielParameterPerson.class,
                String.class);

        LoggingMethodInvoker<TestZielParameterPerson> methodInvoker =
            new LoggingMethodInvoker<>(methode, IsyLoggerFactory.getLogger(TestZielKlasse.class), loggeAufruf,
                loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException, loggeMaximaleParameterGroesse,
                "Nachbarsystem 123", "http://test.test/test");

        methodInvoker.fuehreMethodeAus(new TestZielKlasse(), new TestZielParameterPerson("Mustermann", "Max",
                "Peter", "Hans"), "TestParameter 2");

        pruefeLogdatei("testAufrufErfolgreichNachbarsystem");

    }

    /**
     * Testing the creation of log entries for not successful method calls in a neighbor system (Exception).
     * 
     * @throws Exception
     *             if an error occurs in the test.
     */
    @Test
    public void testAufrufMitExceptionNachbarsystem() throws Exception {

        boolean loggeDauer = true;
        boolean loggeAufruf = true;
        boolean loggeErgebnis = true;
        boolean loggeDatenBeiException = true;
        boolean loggeDaten = false;
        long loggeMaximaleParameterGroesse = 1000000;

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        LoggingMethodInvoker<TestZielParameterPerson> methodInvoker =
            new LoggingMethodInvoker<>(methode, IsyLoggerFactory.getLogger(TestZielKlasse.class), loggeAufruf,
                loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException, loggeMaximaleParameterGroesse,
                "Nachbarsystem 123", "http://test.test/test");

        try {
            methodInvoker.fuehreMethodeAus(new TestZielKlasse(), new TestZielParameterPerson("Mustermann",
                    "Max", "Peter", "Hans"), "TestParameter 2");
            Assertions.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // expecting this exception.
            pruefeLogdatei("testAufrufMitExceptionNachbarsystem");
        }

    }

}
