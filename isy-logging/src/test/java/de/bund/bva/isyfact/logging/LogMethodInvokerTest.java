package de.bund.bva.isyfact.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielParameterPerson;
import de.bund.bva.isyfact.logging.util.LoggingMethodInvoker;

/**
 * Testfälle zur Klasse LogMethodInvoker.
 * 
 */
public class LogMethodInvokerTest extends AbstractLogTest {

    /**
     * Testet die Erstellung von Logeinträgen bei erfolgreichen Methodenaufrufen.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
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
     * Testet die Erstellung von Logeinträgen bei nicht erfolgreichen Methodenaufrufen (Exception).
     * 
     * @throws Exception
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
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
            Assert.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            pruefeLogdatei("testAufrufMitException", true);
        }

    }

    /**
     * Testet die Erstellung von Logeinträgen bei erfolgreichen Methodenaufrufen eines Nachbarsystems.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
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
     * Testet die Erstellung von Logeinträgen bei nicht erfolgreichen Methodenaufrufen eines Nachbarsystems
     * (Exception).
     * 
     * @throws Exception
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
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
            Assert.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // Diese Exception wird erwartet.
            pruefeLogdatei("testAufrufMitExceptionNachbarsystem");
        }

    }

}
