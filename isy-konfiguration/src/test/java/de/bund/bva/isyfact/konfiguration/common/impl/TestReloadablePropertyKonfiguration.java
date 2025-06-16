package de.bund.bva.isyfact.konfiguration.common.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.KonfigurationChangeListener;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationParameterException;

public class TestReloadablePropertyKonfiguration {

    private static final String CONFIG_A = "/config/config_A.properties";

    private static final String CONFIG_B = "/config/config_B.properties";

    private static final String CONFIG_X_CLASSPATH = "target/test-classes/config/config_X.properties";

    private static final String CONFIG_X_RESOURCEPATH = "/config/config_X.properties";

    private static final String CONTENT = "parameter.string = Hello";

    private ReloadablePropertyKonfiguration konf;

    @Before
    public void setUp() {
        konf = new ReloadablePropertyKonfiguration(new String[] { CONFIG_A, CONFIG_B });
    }

    @Test
    public void testGetSchluessel() {
        Set<String> testSet = konf.getSchluessel();
        assertEquals(9, testSet.size());
        assertTrue(testSet.contains("parameter.string"));
        assertTrue(testSet.contains("parameter.rawstring"));
        assertTrue(testSet.contains("parameter.int.2"));
    }

    @Test(expected = KonfigurationDateiException.class)
    public void testInitializationOnException() {
        new ReloadablePropertyKonfiguration(new String[] { "foo bar" });
    }

    /**
     * Testen der getAs...() Methoden aus AbstractKonfiguration
     */

    @Test
    public void testAbstractKonfiguration() {
        assertFalse(konf.getAsBoolean("parameter.boolean", true));
        assertFalse(konf.getAsBoolean("parameter.boolean.2", true));
        assertTrue(konf.getAsBoolean("parameter.boolean.3", false));

        assertEquals(2000, konf.getAsInteger("parameter.int", 0));
        assertEquals(1000, konf.getAsLong("parameter.int.2", 0));
        assertEquals(100.5, konf.getAsDouble("parameter.double", 0.0), 0.0);

        assertEquals("", konf.getAsString("parameter.defined", null));
        //testet ob die AsString Methode trimmt
        assertEquals("Hans", konf.getAsString("parameter.rawstring", null));

        assertEquals("Hans ", konf.getAsRawString("parameter.rawstring", null));


        assertTrue(konf.getAsBoolean("parameter", true));
        assertEquals(0, konf.getAsInteger("parameter", 0));
        assertEquals(0l, konf.getAsLong("parameter", 0l));
        assertEquals(0.0, konf.getAsDouble("parameter", 0.0), 0.0);
        assertNull(konf.getAsString("parameter", null));
        assertNull(konf.getAsRawString("parameter", null));

        assertFalse(konf.getAsBoolean("parameter.boolean"));
        assertEquals(2000, konf.getAsInteger("parameter.int"));
        assertEquals(1000, konf.getAsLong("parameter.int.2"));
        assertEquals(100.5, konf.getAsDouble("parameter.double"), 0.0);
        assertEquals("Hans", konf.getAsString("parameter.rawstring"));
        assertEquals("Hans ", konf.getAsRawString("parameter.rawstring"));
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsStringOnException() {
        konf.getAsString("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsRawStringOnException() {
        konf.getAsRawString("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsBooleanOnException() {
        konf.getAsBoolean("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsBooleanWrongFormatOnException() {
        konf.getAsBoolean("parameter.int");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsLongOnException() {
        konf.getAsLong("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsLongWrongFormatOnException() {
        konf.getAsLong("parameter.string");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsDoubleOnException() {
        konf.getAsDouble("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsDoubleWrongFormatOnException() {
        konf.getAsDouble("parameter.string");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsIntegerOnException() {
        konf.getAsInteger("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsIntegernWrongFormatOnException() {
        konf.getAsInteger("parameter.string");
    }

    @Test
    public void testAddKonfigurationChangeListener() {
        KonfigurationChangeListener listener = changedKeys -> {};

        konf.addKonfigurationChangeListener(listener);
        assertTrue(konf.hasKonfigurationChangeListener(listener));
        assertFalse(konf.hasKonfigurationChangeListener(null));

        konf.addKonfigurationChangeListener(listener);
        konf.addKonfigurationChangeListener(null);

        konf.removeKonfigurationChangeListener(listener);
        assertFalse(konf.hasKonfigurationChangeListener(listener));
    }

    @Test
    public void testCheckAndUpdate() throws IOException, InterruptedException {

        File f = new File(CONFIG_X_CLASSPATH);
        assertTrue(f.createNewFile());
        Thread.sleep(1000);
        FileWriter fw = new FileWriter(f);
        fw.write(CONTENT);
        fw.write("\nparameter.world = hello");
        fw.close();
        Thread.sleep(1000);
        ReloadablePropertyKonfiguration other =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_X_RESOURCEPATH });
        assertFalse(other.checkAndUpdate());
        assertTrue(f.delete());
        assertTrue(f.createNewFile());
        Thread.sleep(1000);
        fw = new FileWriter(f);
        fw.write(CONTENT + "World");
        fw.write("\nparameter.hello = World");
        fw.close();
        other.addKonfigurationChangeListener(changedKeys -> {});
        Thread.sleep(1000);
        assertTrue(other.checkAndUpdate());
        assertTrue(f.delete());
    }

    @Test
    public void testCheckAndUpdateWithoutCorrelationId() {
        String correlationId = MdcHelper.liesKorrelationsId();
        konf.checkAndUpdate();
        assertEquals(correlationId, MdcHelper.liesKorrelationsId());
    }

    @Test
    public void testCheckAndUpdateWithCorrelationId() {
        MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

        String correlationId = MdcHelper.liesKorrelationsId();
        konf.checkAndUpdate();
        assertEquals(correlationId, MdcHelper.liesKorrelationsId());

        MdcHelper.entferneKorrelationsId();
    }
}
