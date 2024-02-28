package de.bund.bva.isyfact.logging;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */

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
