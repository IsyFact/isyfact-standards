package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.ZeichenKategorie;
import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.TestData;

/**
 * Tests the IdentischerTransformator.
 *
 */
public class IdentischerTransformatorTest {

    /** Transformator. */
    public AbstractTransformator transformator;

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IdentischerTransformatorTest.class);

    @Before
    public void setUp() {
        this.transformator = new IdentischerTransformator();
    }

    @Test
    public void testInitialisierung() {
        this.transformator.initialisiere(null);
    }

    @Test
    public void testTransformiere() {

        this.transformator.initialisiere(null);

        String toTransform = "AaBZzöTä?/!$Ô+*#-";
        String expected = "AaBZzöTä?/!$Ô+*#-";

        LOG.debug("Eingabe: " + toTransform);
        LOG.debug("Erwartete Ausgabe: " + expected);

        String transformed = this.transformator.transformiere(toTransform);
        LOG.debug("Ausgabe: " + transformed);

        assertEquals(expected, transformed);
    }

    @Test
    public void testTransformiereDinspec91379() {

        this.transformator.initialisiere(null);

        for (int i = 0; i < TestData.RANDOM_TESTDATA.length; i++) {
            LOG.debug("Eingabe: " + TestData.RANDOM_TESTDATA[i]);
            LOG.debug("Erwartete Ausgabe: " + TestData.RANDOM_TESTDATA[i]);
            String transformed = this.transformator.transformiere(TestData.RANDOM_TESTDATA[i]);
            LOG.debug("Ausgabe: " + transformed);

            assertEquals(TestData.RANDOM_TESTDATA[i], transformed);
        }
    }

    @Test
    public void testGueltigeZeichen() {

        this.transformator.initialisiere(null);

    }

    @Test
    public void testRegulaererAusdruckSpeziell() {

        this.transformator.initialisiere(null);

        String regex = this.transformator.getRegulaererAusdruck(new String[] { ZeichenKategorie.ALLE });

        Pattern pattern = Pattern.compile(regex);

        String testpatternGueltig_1 = "Î";
        String testpatternGueltig_2 = "\u004A\u030C";
        String testpatternGueltig_3 = "\u006C\u0302";
        String testpatternUngueltig_1 = "B\u0302uivir%$";
        String testpatternUngueltig_2 = "\u0302";
        Matcher matcherGueltig_1 = pattern.matcher(testpatternGueltig_1);
        Matcher matcherGueltig_2 = pattern.matcher(testpatternGueltig_2);
        Matcher matcherGueltig_3 = pattern.matcher(testpatternGueltig_3);
        Matcher matcherUngueltig_1 = pattern.matcher(testpatternUngueltig_1);
        Matcher matcherUngueltig_2 = pattern.matcher(testpatternUngueltig_2);

        assertTrue(matcherGueltig_1.matches());
        assertTrue(matcherGueltig_2.matches());
        assertTrue(matcherGueltig_3.matches());
        assertFalse(matcherUngueltig_1.matches());
        assertFalse(matcherUngueltig_2.matches());
    }

    @Test
    public void testIsGueltigesString() {

        this.transformator.initialisiere(null);

        String testpatternGueltig_1 = "Î";
        String testpatternGueltig_2 = "\u004A\u030C";
        String testpatternGueltig_3 = "\u006C\u0302";
        String testpatternUngueltig_1 = "B\u0302uivir%$";
        String testpatternUngueltig_2 = "\u0302";
        String testpatternUngueltig_3 = "\u006C\u0302uivir%$";

        assertTrue(this.transformator.isGueltigerString(testpatternGueltig_1,
            new String[] { ZeichenKategorie.ALLE }));
        assertTrue(this.transformator.isGueltigerString(testpatternGueltig_2,
            new String[] { ZeichenKategorie.ALLE }));
        assertTrue(this.transformator.isGueltigerString(testpatternGueltig_3,
            new String[] { ZeichenKategorie.ALLE }));
        assertFalse(this.transformator.isGueltigerString(testpatternUngueltig_1,
            new String[] { ZeichenKategorie.ALLE }));
        assertFalse(this.transformator.isGueltigerString(testpatternUngueltig_2,
            new String[] { ZeichenKategorie.ALLE }));
        assertFalse(this.transformator.isGueltigerString(testpatternUngueltig_3,
            new String[] { ZeichenKategorie.LETTER }));
    }

    @Test
    public void testTransformiereNull() {
        transformator.transformiere(null);
    }

    @Test
    public void testTransformiereOhneTrim() {
        assertNull(transformator.transformiereOhneTrim(null));
        String zeichenkette = "Dies ist ein Text zum trimmen    ";

        transformator.initialisiere(null);
        assertNotEquals(transformator.transformiere(zeichenkette),transformator.transformiereOhneTrim(zeichenkette));
    }

    @Test
    public void testTransformiereZusaetzlich() {
        String zeichenkette = "\u2022";
        assertNotEquals("K", transformator.transformiere(zeichenkette));
        transformator.initialisiere("/tabellen/zusaetzliche.transform");
        assertEquals("K", transformator.transformiere(zeichenkette));
    }
}
