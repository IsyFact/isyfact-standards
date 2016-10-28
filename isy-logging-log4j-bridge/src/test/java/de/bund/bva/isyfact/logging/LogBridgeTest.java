package de.bund.bva.isyfact.logging;

/*
 * #%L
 * isy-logging-log4j-bridge
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.logging.common.layout.SingleLinePatternLayout;

/**
 * Testfälle der LogBridge.
 */
public class LogBridgeTest {

    /** Pfad zur Logdatei. */
    protected static final String LOG_DATEI = "/var/log/isy-logging/bridge.log";

    /** Trenner zum einzelne Abschnitte in der Ergebnisdatei unterscheiden zu können. */
    protected static final String TESTFALL_TRENNER = "===== ENDE " + Calendar.getInstance().getTimeInMillis();

    /**
     * Umfassender Testfall zur Prüfung, ob beim Aufruf der Bridge die gleichen Ergebnisse erzielt werden als
     * beim direkten Aufruf von log4j. Dazu werden zunächst logeinträge über die Bridge und dann direkt mit
     * log4j geschrieben. Dabei werden alle möglichen Logmethoden von slf4j aufgerufen. Es wird überprüft,
     * dass in beiden Situation die Logeinträge gleich aussehen.
     */
    @Test
    public void testLogBridge() throws IOException {

        // Setzen des NDC über den MdcHelper
        MdcHelper.pushKorrelationsId("1");
        MdcHelper.pushKorrelationsId("2");
        MdcHelper.pushKorrelationsId("3");
        MdcHelper.entferneKorrelationsId();

        // Vordefinierte Werte zur Ausgabe in den Tests
        String arg = "EINS";
        String arg1 = "EINS";
        String arg2 = "ZWEI";
        String arg3 = "DREI";

        // Lognachrichten mit Platzhaltern, die durch die Bridge ersetzt werden müssen.
        String nachrichtOhnePlatzmalter = "Dies ist ein Test.";
        String nachricht1Platzhalter = "Dies ist ein {} Test.";
        String nachricht2Platzhalter = "Dies ist ein {} und {} Test.";
        String nachricht3Platzhalter = "Dies ist ein {} und {} und {} Test.";

        // Lognachrichten mit bereits ersetzten Platzhaltern.
        String nachricht1PlatzhalterErsetzt = "Dies ist ein " + arg + " Test.";
        String nachricht2PlatzhalterErsetzt = "Dies ist ein " + arg1 + " und " + arg2 + " Test.";
        String nachricht3PlatzhalterErsetzt = "Dies ist ein " + arg1 + " und " + arg2 + " und " + arg3
                + " Test.";

        // Lognachrichten mit bereits ersetzten Platzhaltern (mit dem Wert 'null').
        String nachricht1PlatzhalterErsetztNull = "Dies ist ein null Test.";
        String nachricht2PlatzhalterErsetztNull = "Dies ist ein null und null Test.";
        String nachricht3PlatzhalterErsetztNull = "Dies ist ein null und null und null Test.";

        // Marker zur Übergabe an die Logs
        Marker marker = MarkerFactory.getMarker("ABC");

        // Exception zur Übergabe an die Logs
        Throwable t = new ArithmeticException();

        // Zu testender SLF4J-Bridge-Logger
        Logger logger = LoggerFactory.getLogger(LogBridgeTest.class);

        // Start der Tests
        logger.info(TESTFALL_TRENNER);

        loggeInBridge(logger, nachrichtOhnePlatzmalter, nachricht1Platzhalter, nachricht2Platzhalter,
                nachricht3Platzhalter, marker, arg, arg1, arg2, arg3, t);
        loggeInBridge(logger, nachrichtOhnePlatzmalter, nachricht1Platzhalter, nachricht2Platzhalter,
                nachricht3Platzhalter, null, null, null, null, null, null);

        // Zu testender SLF4J-Bridge-Root-Logger)
        Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        loggeInBridge(rootLogger, nachrichtOhnePlatzmalter, nachricht1Platzhalter, nachricht2Platzhalter,
                nachricht3Platzhalter, marker, arg, arg1, arg2, arg3, t);
        loggeInBridge(rootLogger, nachrichtOhnePlatzmalter, nachricht1Platzhalter, nachricht2Platzhalter,
                nachricht3Platzhalter, null, null, null, null, null, null);

        logger.info(TESTFALL_TRENNER);

        // Zu testender LOG4J-Logger
        org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(LogBridgeTest.class);

        loggeInLog4j(log4jLogger, nachrichtOhnePlatzmalter, nachricht1PlatzhalterErsetzt,
                nachricht2PlatzhalterErsetzt, nachricht3PlatzhalterErsetzt, marker, arg, arg1, arg2, arg3, t);
        loggeInLog4j(log4jLogger, nachrichtOhnePlatzmalter, nachricht1PlatzhalterErsetztNull,
                nachricht2PlatzhalterErsetztNull, nachricht3PlatzhalterErsetztNull, null, null, null, null,
                null, null);

        // Zu testender LOG4J-Root-Logger
        org.apache.log4j.Logger log4jRootLogger = org.apache.log4j.Logger.getRootLogger();

        loggeInLog4j(log4jRootLogger, nachrichtOhnePlatzmalter, nachricht1PlatzhalterErsetzt,
                nachricht2PlatzhalterErsetzt, nachricht3PlatzhalterErsetzt, marker, arg, arg1, arg2, arg3, t);
        loggeInLog4j(log4jRootLogger, nachrichtOhnePlatzmalter, nachricht1PlatzhalterErsetztNull,
                nachricht2PlatzhalterErsetztNull, nachricht3PlatzhalterErsetztNull, null, null, null, null,
                null, null);

        pruefeLogdatei();

        // Aufruf der restlichen Methoden des Logger-Interface
        Assert.assertEquals(logger.getName(), "de.bund.bva.isyfact.logging.LogBridgeTest");
        Assert.assertTrue(logger.isDebugEnabled());
        Assert.assertTrue(logger.isDebugEnabled(marker));
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertTrue(logger.isInfoEnabled(marker));
        Assert.assertTrue(logger.isTraceEnabled());
        Assert.assertTrue(logger.isTraceEnabled(marker));
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isWarnEnabled(marker));

    }

    /**
     * Liest die erstellte Logdatei und vergleicht die Einträge, die von der Bridge geschrieben wurden, mit
     * denen, die von log4j geschrieben wurden.
     * 
     * @throws IOException
     */
    private void pruefeLogdatei() throws IOException {

        // Lesen der Logzeilen der Ergebnisdatei
        List<String> zeilenBridge = new ArrayList<String>();
        List<String> zeilenLog4j = new ArrayList<String>();

        BufferedReader ergebnisReader = new BufferedReader(new FileReader(new File(LOG_DATEI)));
        String zeileErgebnis;
        List<String> aktuelleListe = null;
        while ((zeileErgebnis = ergebnisReader.readLine()) != null) {

            // Beim Auftreten des Trenners beginnen die Logzeilen der Bridge, beim zweiten Trenner die
            // Logzeilen von log4j.
            if (zeileErgebnis.contains(TESTFALL_TRENNER)) {
                if (aktuelleListe == null) {
                    aktuelleListe = zeilenBridge;
                } else {
                    aktuelleListe = zeilenLog4j;
                }
            } else if (aktuelleListe != null) {
                aktuelleListe.add(bereinigeLogzeile(zeileErgebnis));
            }
        }

        Assert.assertEquals("Anzahl der Logzeilen zwischen Bridge und Log4j stimmen nicht überein.",
                zeilenLog4j.size(), zeilenBridge.size());
        Assert.assertEquals("Unerwartete Anzahl an Logzeilen.", 200, zeilenLog4j.size());
        for (int i = 0; i < zeilenBridge.size(); i++) {
            Assert.assertEquals("Logzeilen " + i + " stimmen nicht überein.", zeilenLog4j.get(i),
                    zeilenBridge.get(i));
            System.out.println(zeilenLog4j.get(i));
        }

        ergebnisReader.close();
    }

    private String bereinigeLogzeile(String zeile) {
        String ergebnis = zeile.substring(0, 15) + "XX" + zeile.substring(17, 18) + "XX"
                + zeile.substring(20, 21) + "XX" + zeile.substring(23, 24) + "XXX" + zeile.substring(27);
        return ergebnis;
    }

    /**
     * Hilfsmethode zum Aufruf aller möglicher Methoden zum Schreiben Logeinträgen über die Bridge.
     * 
     * @param logger
     *            der zu verwendende Logger.
     * @param nachrichtOhnePlatzmalter
     *            Testnachricht ohne Platzhalter.
     * @param nachricht1Platzhalter
     *            Testnachricht mit 1 Platzhalter.
     * @param nachricht2Platzhalter
     *            Testnachricht mit 2 Platzhalter.
     * @param nachricht3Platzhalter
     *            Testnachricht mit 3 Platzhalter.
     * @param marker
     *            zu logender Marker
     * @param arg
     *            Wert des Platzhalters, wenn in der Nachricht nur ein Platzhalter vorhanden ist.
     * @param arg1
     *            Wert des ersten Platzhalters, wenn in der Nachricht nur mehrere Platzhalter vorhanden sind.
     * @param arg2
     *            Wert des zweiten Platzhalters, wenn in der Nachricht nur mehrere Platzhalter vorhanden sind.
     * @param arg3
     *            Wert des dritten Platzhalters, wenn in der Nachricht nur mehrere Platzhalter vorhanden sind.
     * @param t
     *            zu loggende Exception.
     */
    private void loggeInLog4j(org.apache.log4j.Logger logger, String nachrichtOhnePlatzmalter,
            String nachricht1Platzhalter, String nachricht2Platzhalter, String nachricht3Platzhalter,
            Marker marker, String arg, String arg1, String arg2, String arg3, Throwable t) {

        logger.trace(nachrichtOhnePlatzmalter);
        logger.trace(nachrichtOhnePlatzmalter);
        logger.trace(nachricht1Platzhalter);
        logger.trace(nachricht3Platzhalter);
        logger.trace(nachrichtOhnePlatzmalter, t);
        logger.trace(nachricht1Platzhalter);
        logger.trace(nachricht3Platzhalter);
        logger.trace(nachrichtOhnePlatzmalter, t);
        logger.trace(nachricht2Platzhalter);
        logger.trace(nachricht2Platzhalter);

        logger.debug(nachrichtOhnePlatzmalter);
        logger.debug(nachrichtOhnePlatzmalter);
        logger.debug(nachricht1Platzhalter);
        logger.debug(nachricht3Platzhalter);
        logger.debug(nachrichtOhnePlatzmalter, t);
        logger.debug(nachricht1Platzhalter);
        logger.debug(nachricht3Platzhalter);
        logger.debug(nachrichtOhnePlatzmalter, t);
        logger.debug(nachricht2Platzhalter);
        logger.debug(nachricht2Platzhalter);

        logger.info(nachrichtOhnePlatzmalter);
        logger.info(nachrichtOhnePlatzmalter);
        logger.info(nachricht1Platzhalter);
        logger.info(nachricht3Platzhalter);
        logger.info(nachrichtOhnePlatzmalter, t);
        logger.info(nachricht1Platzhalter);
        logger.info(nachricht3Platzhalter);
        logger.info(nachrichtOhnePlatzmalter, t);
        logger.info(nachricht2Platzhalter);
        logger.info(nachricht2Platzhalter);

        logger.warn(nachrichtOhnePlatzmalter);
        logger.warn(nachrichtOhnePlatzmalter);
        logger.warn(nachricht1Platzhalter);
        logger.warn(nachricht3Platzhalter);
        logger.warn(nachrichtOhnePlatzmalter, t);
        logger.warn(nachricht1Platzhalter);
        logger.warn(nachricht3Platzhalter);
        logger.warn(nachrichtOhnePlatzmalter, t);
        logger.warn(nachricht2Platzhalter);
        logger.warn(nachricht2Platzhalter);

        logger.error(nachrichtOhnePlatzmalter);
        logger.error(nachrichtOhnePlatzmalter);
        logger.error(nachricht1Platzhalter);
        logger.error(nachricht3Platzhalter);
        logger.error(nachrichtOhnePlatzmalter, t);
        logger.error(nachricht1Platzhalter);
        logger.error(nachricht3Platzhalter);
        logger.error(nachrichtOhnePlatzmalter, t);
        logger.error(nachricht2Platzhalter);
        logger.error(nachricht2Platzhalter);

    }

    /**
     * Hilfsmethode zum Aufruf aller möglicher Methoden zum Schreiben Logeinträgen über die Bridge.
     * 
     * @param logger
     *            der zu verwendende Logger.
     * @param nachrichtOhnePlatzmalter
     *            Testnachricht ohne Platzhalter.
     * @param nachricht1Platzhalter
     *            Testnachricht mit 1 Platzhalter.
     * @param nachricht2Platzhalter
     *            Testnachricht mit 2 Platzhalter.
     * @param nachricht3Platzhalter
     *            Testnachricht mit 3 Platzhalter.
     * @param marker
     *            zu logender Marker
     * @param arg
     *            Wert des Platzhalters, wenn in der Nachricht nur ein Platzhalter vorhanden ist.
     * @param arg1
     *            Wert des ersten Platzhalters, wenn in der Nachricht nur mehrere Platzhalter vorhanden sind.
     * @param arg2
     *            Wert des zweiten Platzhalters, wenn in der Nachricht nur mehrere Platzhalter vorhanden sind.
     * @param arg3
     *            Wert des dritten Platzhalters, wenn in der Nachricht nur mehrere Platzhalter vorhanden sind.
     * @param t
     *            zu loggende Exception.
     */
    private void loggeInBridge(Logger logger, String nachrichtOhnePlatzmalter, String nachricht1Platzhalter,
            String nachricht2Platzhalter, String nachricht3Platzhalter, Marker marker, String arg,
            String arg1, String arg2, String arg3, Throwable t) {

        logger.trace(nachrichtOhnePlatzmalter);
        logger.trace(marker, nachrichtOhnePlatzmalter);
        logger.trace(nachricht1Platzhalter, arg);
        logger.trace(nachricht3Platzhalter, arg1, arg2, arg3);
        logger.trace(nachrichtOhnePlatzmalter, t);
        logger.trace(marker, nachricht1Platzhalter, arg);
        logger.trace(marker, nachricht3Platzhalter, arg1, arg2, arg3);
        logger.trace(marker, nachrichtOhnePlatzmalter, t);
        logger.trace(nachricht2Platzhalter, arg1, arg2);
        logger.trace(marker, nachricht2Platzhalter, arg1, arg2);

        logger.debug(nachrichtOhnePlatzmalter);
        logger.debug(marker, nachrichtOhnePlatzmalter);
        logger.debug(nachricht1Platzhalter, arg);
        logger.debug(nachricht3Platzhalter, arg1, arg2, arg3);
        logger.debug(nachrichtOhnePlatzmalter, t);
        logger.debug(marker, nachricht1Platzhalter, arg);
        logger.debug(marker, nachricht3Platzhalter, arg1, arg2, arg3);
        logger.debug(marker, nachrichtOhnePlatzmalter, t);
        logger.debug(nachricht2Platzhalter, arg1, arg2);
        logger.debug(marker, nachricht2Platzhalter, arg1, arg2);

        logger.info(nachrichtOhnePlatzmalter);
        logger.info(marker, nachrichtOhnePlatzmalter);
        logger.info(nachricht1Platzhalter, arg);
        logger.info(nachricht3Platzhalter, arg1, arg2, arg3);
        logger.info(nachrichtOhnePlatzmalter, t);
        logger.info(marker, nachricht1Platzhalter, arg);
        logger.info(marker, nachricht3Platzhalter, arg1, arg2, arg3);
        logger.info(marker, nachrichtOhnePlatzmalter, t);
        logger.info(nachricht2Platzhalter, arg1, arg2);
        logger.info(marker, nachricht2Platzhalter, arg1, arg2);

        logger.warn(nachrichtOhnePlatzmalter);
        logger.warn(marker, nachrichtOhnePlatzmalter);
        logger.warn(nachricht1Platzhalter, arg);
        logger.warn(nachricht3Platzhalter, arg1, arg2, arg3);
        logger.warn(nachrichtOhnePlatzmalter, t);
        logger.warn(marker, nachricht1Platzhalter, arg);
        logger.warn(marker, nachricht3Platzhalter, arg1, arg2, arg3);
        logger.warn(marker, nachrichtOhnePlatzmalter, t);
        logger.warn(nachricht2Platzhalter, arg1, arg2);
        logger.warn(marker, nachricht2Platzhalter, arg1, arg2);

        logger.error(nachrichtOhnePlatzmalter);
        logger.error(marker, nachrichtOhnePlatzmalter);
        logger.error(nachricht1Platzhalter, arg);
        logger.error(nachricht3Platzhalter, arg1, arg2, arg3);
        logger.error(nachrichtOhnePlatzmalter, t);
        logger.error(marker, nachricht1Platzhalter, arg);
        logger.error(marker, nachricht3Platzhalter, arg1, arg2, arg3);
        logger.error(marker, nachrichtOhnePlatzmalter, t);
        logger.error(nachricht2Platzhalter, arg1, arg2);
        logger.error(marker, nachricht2Platzhalter, arg1, arg2);
    }
}
