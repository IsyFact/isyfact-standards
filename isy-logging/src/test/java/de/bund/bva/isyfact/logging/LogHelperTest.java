package de.bund.bva.isyfact.logging;

import java.util.Random;

import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach;

import org.junit.jupiter.api.Test;
import de.bund.bva.isyfact.logging.hilfsklassen.TestSignaturKlasse;
import de.bund.bva.isyfact.logging.util.LogHelper;

/**
 * {@link LogHelper} tests.
 */
public class LogHelperTest extends AbstractLogTest {

    /**
     * Test special cases which are not covered in other test cases.
     * @throws Exception if an exception during the test occurs.
     */
    @Test
    public void testLogHelperSpezialfaelle() throws Exception {

        // The method "loggeErgebnis" is called without parameter and only "loggeErgebnis=true" and
        // "loggeDatenBeiException=true" are set in the helper. INFO and DEBUG log entries are created
        // with the given parameters.
        LogHelper logHelper = new LogHelper(false, true, false, false, true, 0);
        IsyLogger logger = IsyLoggerFactory.getLogger(LogHelperTest.class);
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), false,
                null, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), true,
                null, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), false,
                null, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), true,
                null, "Ergebnis");
        // calling LoggeErgebnis with one parameter
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), false,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), true,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), false,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), true,
                new String[] { "EinString" }, "Ergebnis");

        // The method "loggeErgebnis" is called without parameter and only "loggeErgebnis=true" and is set
        // in the helper. INFO log entry is created.
        logHelper = new LogHelper(false, true, false, false, false, 0);
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), false,
                null, null);
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), true,
                null, null);
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), false,
                null, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), true,
                null, "Ergebnis");
        // calling LoggeErgebnis with one parameter
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), false,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("method", String.class), true,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), false,
                new String[] { "EinString" }, "Ergebnis");
        logHelper.loggeErgebnis(logger, TestSignaturKlasse.class.getMethod("methodWithException", String.class), true,
                new String[] { "EinString" }, "Ergebnis");

        // Passing nulls and disabled methods - no log entries will be created:

        // Log a method call with "loggeAufruf=false"
        logHelper = new LogHelper(false, true, true, false, true, 0);
        logHelper.loggeNachbarsystemAufruf(logger, null, null, null);

        // Log result with "loggeErgebnis=false"
        logHelper = new LogHelper(true, false, true, false, true, 0);
        logHelper.loggeNachbarsystemErgebnis(logger, null, null, null, false);

        // Log duration with "loggeDauer=false"
        logHelper = new LogHelper(true, true, false, false, true, 0);
        logHelper.loggeNachbarsystemDauer(logger, null, 1, null, null, true);

        pruefeLogdatei("testLogHelperSpezialfaelle");
    }

    /**
     * Test special cases which are not covered in other test cases. Test deprecated constructor.
     * @throws Exception if an exception during the test occurs.
     */
   @Test
    public void testLogHelperParameterGroesse() throws Exception {
        int parameterGroesse = 1000;

        LogHelper logHelper = new LogHelper(false, false, false, true, false, parameterGroesse - 1);
        IsyLogger logger = IsyLoggerFactory.getLogger(LogHelperTest.class);

        // Log a parameter with a too big value
        byte[] binaerDaten = new byte[parameterGroesse];
        new Random().nextBytes(binaerDaten);
        logHelper.loggeErgebnis(logger, TestBeanEinfach.class.getMethod("setEinString", String.class), true,
            new Object[] { binaerDaten, "einString" }, "Ergebnis");

        pruefeLogdatei("testLogHelperParameterGroesse");
    }
}
