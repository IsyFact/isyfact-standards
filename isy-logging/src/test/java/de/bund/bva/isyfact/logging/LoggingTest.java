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
import org.slf4j.spi.LocationAwareLogger;

import de.bund.bva.isyfact.logging.exceptions.FehlerhafterLogeintrag;
import de.bund.bva.isyfact.logging.exceptions.IsyLoggingFehlertextProvider;
import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;
import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;

/**
 * Testfällt der Erstellung von Logeinträgen.
 * 
 */
public class LoggingTest extends AbstractLogTest {

    /** Ergeinisschlüssel, der standardmäßig für die Tests verwendet wird. */
    private static final String EREIGNISSCHLUESSEL = "EISYLO12345";
    
    /**
     * Testfall zum Erstellen korrekter Logeinträge in allen Log-Leveln.
     */
    @Test
    public void testLoggingErfolgreich() {

        IsyLogger logger = IsyLoggerFactory.getLogger(this.getClass());

        Exception t = new NullPointerException();
        PlisTechnicalRuntimeException tre = new LogKonfigurationFehler(
                FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK, this.getClass().getName());

        logger.infoFachdaten(LogKategorie.JOURNAL, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super",
                "info", "Fachdaten", "Klasse");

        @SuppressWarnings("serial")
        PlisException te = new PlisTechnicalException(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK,
                new IsyLoggingFehlertextProvider(), this.getClass().getName()) {
        };

        // TRACE
        logger.trace("Dies ist ein {} {} Test. {}, oder?", "super", "trace", "Klasse");
        logger.traceFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", "super", "trace", "Fachdaten",
                "Klasse");

        // DEBUG
        logger.debug("Dies ist ein {} {} Test. {}, oder?", "super", "debug", "Klasse");
        logger.debugFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", "super", "debug", "Fachdaten",
                "Klasse");

        // INFO
        logger.info(LogKategorie.JOURNAL, "Dies ist ein {} {} Test. {}, oder?", tre, "super", "info",
                "Klasse");
        logger.info(LogKategorie.JOURNAL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "info", "Klasse");
        logger.info(LogKategorie.METRIK, "ETESTL10000", "Dies ist ein {} {} Test mit {}. {}, oder?", "super",
                "info", "Fachdaten", "Klasse");
        logger.infoFachdaten(LogKategorie.JOURNAL, "Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super",
                "info", "Fachdaten", "Klasse");
        logger.infoFachdaten(LogKategorie.JOURNAL, "Dies ist ein {} {} Test mit {}. {}, oder?", te, "super",
                "info", "Fachdaten", "Klasse");
        logger.infoFachdaten(LogKategorie.METRIK, "ETESTL10000", "Dies ist ein {} {} Test mit {}. {}, oder?",
                "super", "info", "Fachdaten", "Klasse");

        // WARN
        logger.warn("Dies ist ein {} {} Test. {}, oder?", tre, "super", "warn", "Klasse");
        logger.warn("Dies ist ein {} {} Test. {}, oder?", te, "super", "warn", "Klasse");
        logger.warn(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "warn", "Klasse");
        logger.warn(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "warn", "Klasse");

        logger.warnFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "warn", "Fachdaten",
                "Klasse");
        logger.warnFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "warn", "Fachdaten",
                "Klasse");
        logger.warnFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "warn",
                "Fachdaten");
        logger.warnFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "warn",
                "Fachdaten");

        // ERROR
        logger.error("Dies ist ein {} {} Test. {}, oder?", tre, "super", "error", "Klasse");
        logger.error("Dies ist ein {} {} Test. {}, oder?", te, "super", "error", "Klasse");
        logger.error(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "error", "Klasse");
        logger.error(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "error", "Klasse");
        logger.errorFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "error",
                "Fachdaten", "Klasse");
        logger.errorFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "error", "Fachdaten",
                "Schön");
        logger.errorFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "error",
                "Fachdaten");
        logger.errorFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "error",
                "Fachdaten");

        // FATAL
        logger.fatal("Dies ist ein {} {} Test. {}, oder?", tre, "super", "fatal", "Klasse");
        logger.fatal("Dies ist ein {} {} Test. {}, oder?", te, "super", "fatal", "Klasse");
        logger.fatal(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "fatal", "Klasse");
        logger.fatal(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "fatal", "Klasse");
        logger.fatalFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", tre, "super", "fatal",
                "Fachdaten", "Klasse");
        logger.fatalFachdaten("Dies ist ein {} {} Test mit {}. {}, oder?", te, "super", "fatal", "Fachdaten",
                "Klasse");
        logger.fatalFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", "super", "fatal",
                "Fachdaten");
        logger.fatalFachdaten(EREIGNISSCHLUESSEL, "Dies ist ein {} {} Test. {}, oder?", te, "super", "fatal",
                "Fachdaten");

        // Tests für Throwable
        String nachricht = "Dies ist ein {} {} Test. {}, oder?";

        logger.error(EREIGNISSCHLUESSEL, nachricht, t, "super", "error", "Klasse");
        logger.errorFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "error", "Fachdaten");
        logger.fatal(EREIGNISSCHLUESSEL, nachricht, t, "super", "fatal", "Klasse");
        logger.fatalFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "fatal", "Fachdaten");
        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachricht, t, "super", "info", "Klasse");
        logger.infoFachdaten(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL, nachricht, t, "super", "info",
                "Fachdaten");
        logger.warn(EREIGNISSCHLUESSEL, nachricht, t, "super", "warn", "Klasse");
        logger.warnFachdaten(EREIGNISSCHLUESSEL, nachricht, t, "super", "warn", "Fachdaten");

        logger.info(LogKategorie.JOURNAL, EREIGNISSCHLUESSEL_OHNE_NACHRICHT, (String) null);
        
        pruefeLogdatei("testLoggingErfolgreich");
    }

    /**
     * Testfall zum Erstellen korrekter Logeinträge in allen Log-Leveln, bei fehlerhaften Aufrufen.
     * 
     * @throws Exception
     *             Wenn bei der Testausführung eine Exception auftritt.
     */
    @Test
    public void testLoggingFehlerhaft() throws Exception {
        IsyLogger logger = IsyLoggerFactory.getLogger(LoggingTest.class);

        // Zu viele Parameter
        logger.debug(KENNZEICHNUNG_FEHLERTEST + " - Dies ist ein {} {} Test. {}, oder?", "super", "debug",
                "Klasse", "noch einer", "noch einer", "noch einer");

        // Zu wenige Parameter
        logger.debug(KENNZEICHNUNG_FEHLERTEST + " - Dies ist ein {} {} Test. {}");

        pruefeLogdatei("testLoggingFehlerhaft");

        try {
            // Logeintrag in Info ohne Schlüssel führt zu Exception.
            logger.info(LogKategorie.JOURNAL, null, "Eine Nachricht.");
            Assert.fail("Erstellen eines Logeintrags in Level INFO ohne Schlüssel führte nicht zu einem Fehler.");
        } catch (FehlerhafterLogeintrag fle) {
            Assert.assertEquals("ISYLO00001", fle.getAusnahmeId());
        }

        try {
            // Logeintrag in Info ohne Kategorie führt zu Exception. Dies ist wird durch das Interface bisher
            // verhindert. Daher wird die interne Log-Methode direkt per Reflection aufgerufen.
            // Test für INFO
            Method interneLogmethode = IsyLocationAwareLoggerImpl.class.getDeclaredMethod("log", int.class,
                    String.class, String.class, String.class, Object[].class, Throwable.class, boolean.class);
            interneLogmethode.setAccessible(true);
            
            interneLogmethode.invoke(logger, LocationAwareLogger.INFO_INT, null, EREIGNISSCHLUESSEL,
                    "Eine Nachricht.", null, null, false);
            Assert.fail("Erstellen eines Logeintrags in Level INFO ohne Kategorie führte nicht zu einem Fehler.");
        } catch (InvocationTargetException ite) {
            FehlerhafterLogeintrag fle = (FehlerhafterLogeintrag) ite.getCause();
            Assert.assertEquals("ISYLO00002", fle.getAusnahmeId());
        }
        
        try {
            // Logeintrag in Info ohne Kategorie führt zu Exception. Dies ist wird durch das Interface bisher
            // verhindert. Daher wird die interne Log-Methode direkt per Reflection aufgerufen.
            // Test für ERROR
            Method interneLogmethode = IsyLocationAwareLoggerImpl.class.getDeclaredMethod("log", int.class,
                    String.class, String.class, String.class, Object[].class, Throwable.class, boolean.class);
            interneLogmethode.setAccessible(true);
            
            interneLogmethode.invoke(logger, LocationAwareLogger.ERROR_INT, null, EREIGNISSCHLUESSEL,
                    "Eine Nachricht.", null, null, false);
            Assert.fail("Erstellen eines Logeintrags in Level INFO ohne Kategorie führte nicht zu einem Fehler.");
        } catch (InvocationTargetException ite) {
            FehlerhafterLogeintrag fle = (FehlerhafterLogeintrag) ite.getCause();
            Assert.assertEquals("ISYLO00002", fle.getAusnahmeId());
        }
    }

    /**
     * Testet die interne Methode "ermittleLevelString" die nur für die Aufbereitung von Meldungen eigener
     * Exceptions verwendet wird.
     * 
     * @throws Exception
     *             Wenn bei der Testausführung eine Exception auftritt.
     */
    @Test
    public void testErmittleLevelString() throws Exception {
        Method interneErmittleLevelMethode = IsyLocationAwareLoggerImpl.class.getDeclaredMethod(
                "ermittleLevelString", int.class);
        interneErmittleLevelMethode.setAccessible(true);

        IsyLogger logger = IsyLoggerFactory.getLogger(LoggingTest.class);

        Assert.assertEquals("DEBUG",
                interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.DEBUG_INT));
        Assert.assertEquals("ERROR",
                interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.ERROR_INT));
        Assert.assertEquals("INFO", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.INFO_INT));
        Assert.assertEquals("TRACE",
                interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.TRACE_INT));
        Assert.assertEquals("WARN", interneErmittleLevelMethode.invoke(logger, LocationAwareLogger.WARN_INT));
        Assert.assertEquals(null, interneErmittleLevelMethode.invoke(logger, -5));
    }

}
