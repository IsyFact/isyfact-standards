package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ZeichenValidatorTest {

    public static final String EMPTY_STRING = "";
    public static final String ZEICHEN_A = "\u0041\u0027R̥̄";
    public static final String ZEICHEN_B = "\u0041\u0027\u0033";
    public static final String ZEICHEN_C = "\u0041\u0027\u0033\u00BC\u00A0";
    public static final String ZEICHEN_D = "\u0041\u0027\u0033\u00BC\u2080\u03B9";
    public static final String ZEICHEN_E = "\u0041\u0027\u0033\u00BC\u00A0\u2080\u03B9\u0420";
    public static final String NON_DINNORM_ZEICHEN = "\u0007\uFFED";
    public static final String ZEICHEN_DIAKRIT = "\u035F";

    private ZeichenValidator validator;

    @Before
    public void setUp() {
        validator = new ZeichenValidator();
    }

    @Test
    public void testDatentypA() {
        Datentyp datentyp = Datentyp.A;

        assertTrue(validator.isGueltigerString(EMPTY_STRING, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_A, datentyp));

        assertFalse(validator.isGueltigerString(ZEICHEN_B, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_C, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_D, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_E, datentyp));
        assertFalse(validator.isGueltigerString(NON_DINNORM_ZEICHEN, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_DIAKRIT, datentyp));
    }

    @Test
    public void testDatentypB() {
        Datentyp datentyp = Datentyp.B;

        assertTrue(validator.isGueltigerString(EMPTY_STRING, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_A, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_B, datentyp));

        assertFalse(validator.isGueltigerString(ZEICHEN_C, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_D, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_E, datentyp));
        assertFalse(validator.isGueltigerString(NON_DINNORM_ZEICHEN, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_DIAKRIT, datentyp));
    }

    @Test
    public void testDatentypC() {
        Datentyp datentyp = Datentyp.C;

        assertTrue(validator.isGueltigerString(EMPTY_STRING, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_A, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_B, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_C, datentyp));

        assertFalse(validator.isGueltigerString(ZEICHEN_D, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_E, datentyp));
        assertFalse(validator.isGueltigerString(NON_DINNORM_ZEICHEN, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_DIAKRIT, datentyp));
    }

    @Test
    public void testDatentypD() {
        Datentyp datentyp = Datentyp.D;

        assertTrue(validator.isGueltigerString(EMPTY_STRING, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_A, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_B, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_D, datentyp));

        assertFalse(validator.isGueltigerString(ZEICHEN_C, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_E, datentyp));
        assertFalse(validator.isGueltigerString(NON_DINNORM_ZEICHEN, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_DIAKRIT, datentyp));
    }

    @Test
    public void testDatentypE() {
        Datentyp datentyp = Datentyp.E;

        assertTrue(validator.isGueltigerString(EMPTY_STRING, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_A, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_B, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_C, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_D, datentyp));
        assertTrue(validator.isGueltigerString(ZEICHEN_E, datentyp));

        assertFalse(validator.isGueltigerString(NON_DINNORM_ZEICHEN, datentyp));
        assertFalse(validator.isGueltigerString(ZEICHEN_DIAKRIT, datentyp));
    }

    @Test(expected = NullPointerException.class)
    public void testDatentypNull() {
        assertFalse(validator.isGueltigerString(ZEICHEN_E, null));
    }

    @Test(expected = NullPointerException.class)
    public void testZeichenketteNull() {
        assertFalse(validator.isGueltigerString(null, Datentyp.E));
    }

}
