package de.bund.bva.isyfact.konfiguration.common.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationParameterException;

public class TestPropertyKonfiguration {

    final static String DEFAULTNAMENSSCHEMA = ".*[.]properties";

    private static final String CONFIG_A = "/config/config_A.properties";

    private static final String CONFIG_B = "/config/config_B.properties";

    private PropertyKonfiguration konf;

    @Before
    public void setUp() throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(CONFIG_A);
        list.add(CONFIG_B);
        this.konf = new PropertyKonfiguration(list, DEFAULTNAMENSSCHEMA);
    }

    @Test
    public void testGetSchluessel() {
        Set<String> testSet = this.konf.getSchluessel();
        assertEquals(9, testSet.size());
        assertTrue(testSet.contains("parameter.string"));
        assertTrue(testSet.contains("parameter.rawstring"));
        assertTrue(testSet.contains("parameter.int.2"));
    }

    @Test
    public void testContainsKey() {
        assertTrue(this.konf.containsKey("parameter.string"));
    }

    @Test
    public void testGetValue() {
        PropertyKonfiguration konfCopy = new PropertyKonfiguration(this.konf.getProperties(), "");
        assertEquals("Hans ", konfCopy.getValue("parameter.rawstring"));
    }

    @Test(expected = KonfigurationDateiException.class)
    public void testInitializationOnException() {
        List<String> list = new ArrayList<String>();
        list.add("foo bar");
        new PropertyKonfiguration(list, DEFAULTNAMENSSCHEMA);
    }

    /**
     * Testen der getAs...() Methoden aus AbstractKonfiguration
     */

    @Test
    public void testAbstractKonfiguration() {
        assertEquals(false, this.konf.getAsBoolean("parameter.boolean", true));
        assertEquals(false, this.konf.getAsBoolean("parameter.boolean.2", true));
        assertEquals(true, this.konf.getAsBoolean("parameter.boolean.3", false));

        assertEquals(2000, this.konf.getAsInteger("parameter.int", 0));
        assertEquals(1000, this.konf.getAsLong("parameter.int.2", 0));
        assertEquals(100.5, this.konf.getAsDouble("parameter.double", 0.0), 0.0);

        assertEquals("", this.konf.getAsString("parameter.defined", null));
        // testet ob die AsString Methode trimmt
        assertEquals("Hans", this.konf.getAsString("parameter.rawstring", null));

        assertEquals("Hans ", this.konf.getAsRawString("parameter.rawstring", null));

        assertEquals(true, this.konf.getAsBoolean("parameter", true));
        assertEquals(0, this.konf.getAsInteger("parameter", 0));
        assertEquals(0l, this.konf.getAsLong("parameter", 0l));
        assertEquals(0.0, this.konf.getAsDouble("parameter", 0.0), 0.0);
        assertEquals(null, this.konf.getAsString("parameter", null));
        assertEquals(null, this.konf.getAsRawString("parameter", null));

        assertEquals(false, this.konf.getAsBoolean("parameter.boolean"));
        assertEquals(2000, this.konf.getAsInteger("parameter.int"));
        assertEquals(1000, this.konf.getAsLong("parameter.int.2"));
        assertEquals(100.5, this.konf.getAsDouble("parameter.double"), 0.0);
        assertEquals("Hans", this.konf.getAsString("parameter.rawstring"));
        assertEquals("Hans ", this.konf.getAsRawString("parameter.rawstring"));

    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsStringOnException() {
        this.konf.getAsString("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsRawStringOnException() {
        this.konf.getAsRawString("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsBooleanOnException() {
        this.konf.getAsBoolean("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsBooleanWrongFormatOnException() {
        this.konf.getAsBoolean("parameter.int");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsLongOnException() {
        this.konf.getAsLong("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsLongWrongFormatOnException() {
        this.konf.getAsLong("parameter.string");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsDoubleOnException() {
        this.konf.getAsDouble("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsDoubleWrongFormatOnException() {
        this.konf.getAsDouble("parameter.string");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsIntegerOnException() {
        this.konf.getAsInteger("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsIntegernWrongFormatOnException() {
        this.konf.getAsInteger("parameter.string");
    }

}
