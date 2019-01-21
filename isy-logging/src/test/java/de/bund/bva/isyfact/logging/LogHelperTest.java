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

import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach;
import de.bund.bva.isyfact.logging.util.LogHelper;
import org.junit.Test;

import java.util.Random;

/**
 * Test des Log-Helpers.
 */
public class LogHelperTest extends AbstractLogTest {

    /**
     * Testet mehrere Spezialfälle des Aufrufs des Loghelpers, die nicht durch die anderen Testfälle abgedeckt
     * sind.
     *
     * @throws Exception
     *             wenn beim Test eine Exception aufgetreten ist.
     */
    @Test
    public void testLogHelperSpezialfaelle() throws Exception {

        // LoggeErgebnis wird ohne Parameter aufgerufen, wobei im Helper nur "loggeErgebnis=true" und
        // "loggeDatenBeiException=true" ist. Es wird jeweils ein INFO-Logeintrag und einer in DEBUG mit den
        // übergebenen Parametern erstellt.
        LogHelper logHelper = new LogHelper(false, true, false, false, true, 0);
        IsyLogger logger = IsyLoggerFactory.getLogger(LogHelperTest.class);
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), false,
                null, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), true,
                null, "Ergebnis");
        // LoggeErgebnis wird mit einem Parameter aufgerufen
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), false,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), true,
                new String[] { "EinString" }, "Ergebnis");

        // LoggeErgebnis wird ohne Parameter aufgerufen, wobei im Helper nur "loggeErgebnis=true" ist. Es wird
        // jeweils nur der INFO.Eintrag erstellt.
        logHelper = new LogHelper(false, true, false, false, false, 0);
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), false,
                null, null);
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), true,
                null, null);
        // LoggeErgebnis wird mit einem Parameter aufgerufen
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), false,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), true,
                new String[] { "EinString" }, "Ergebnis");

        // Übergeben von NULL-Werten und deaktivierten Methoden - bei diesen wird keine Log-Eintrag erstellt:

        // Loggen des Aufrufs mit "loggeAufruf=false"
        logHelper = new LogHelper(false, true, true, false, true, 0);
        logHelper.loggeNachbarsystemAufruf(logger, null, null, null);

        // Loggen des Ergebnisses mit "loggeErgebnis=false"
        logHelper = new LogHelper(true, false, true, false, true, 0);
        logHelper.loggeNachbarsystemErgebnis(logger, null, null, null, false);

        // Loggen der Dauer mit "loggeDauer=false"
        logHelper = new LogHelper(true, true, false, false, true, 0);
        logHelper.loggeNachbarsystemDauer(logger, null, 1, null, null, true);

        pruefeLogdatei("testLogHelperSpezialfaelle");
    }

    /**
     * Testet mehrere Spezialfälle des Aufrufs des Loghelpers, die nicht durch die anderen Testfälle abgedeckt
     * sind. Es wird der deprecated-Konstruktor gestestet.
     *
     * @throws Exception
     *             wenn beim Test eine Exception aufgetreten ist.
     */
    @Test
    public void testLogHelperParameterGroesse() throws Exception {
        int parameterGroesse = 1000;

        LogHelper logHelper = new LogHelper(false, false, false, true, false, parameterGroesse - 1);
        IsyLogger logger = IsyLoggerFactory.getLogger(LogHelperTest.class);

        // Loggen eines zu großen Parameters
        byte[] binaerDaten = new byte[parameterGroesse];
        new Random().nextBytes(binaerDaten);
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), true,
            new Object[] { binaerDaten, "einString" }, "Ergebnis");

        pruefeLogdatei("testLogHelperParameterGroesse");
    }
}
