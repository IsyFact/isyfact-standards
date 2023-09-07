package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

import org.junit.Assert;
import org.junit.Test;

import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl.LegacyTransformator;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl.TranskriptionTransformator;

public class TransformationMetadatenTest {

    /** String that is not changed by the TranskriptionTransformator. */
    public static final String STRING_KEINE_AENDERUNG = "STRING";

    /** Expected result after use of the TranskriptionTransformator on the prior string. */
    public static final String STRING_KEINE_AENDERUNG_EXPECTED = "STRING";

    /** String with characters that are changed to empty strings by the TranskriptionTransformator. */
    public static final String STRING_TRANSFORMIERT_AUF_LEER = "™A™r";

    /** Expected result after use of the TranskriptionTransformator on the prior string. */
    public static final String STRING_TRANSFORMIERT_AUF_LEER_EXPECTED = "AR";

    /** String with leading characters that are trimmed after a transformation. */
    public static final String STRING_ENTFERNE_PRAEFIX = "ツKÄÖ";

    /** Expected result after use of the TranskriptionTransformator on the prior string. */
    public static final String STRING_ENTFERNE_PRAEFIX_EXPECTED = "KAEOE";

    /** String with trailing characters that are trimmed after a transformation. */
    public static final String STRING_ENTFERNE_SUFFIX = "Iツ";

    /** Expected result after use of the TranskriptionTransformator on the prior string. */
    public static final String STRING_ENTFERNE_SUFFIX_EXPECTED = "I";

    /** String with characters that are transformed to consecutive spaces that are replaced by a single space. */
    public static final String STRING_ENTFERNE_MEHRERE_LEERZEICHEN = "Zツツツrツr";

    /** Expected result after use of the TranskriptionTransformator on the prior string. */
    public static final String STRING_ENTFERNE_MEHRERE_LEERZEICHEN_EXPECTED = "Z R R";

    /** String that will be shortened by the LegacyTransformator. */
    public static final String STRING_LEGACY_KUERZEN ="R̥̄C̨̆";

    /** Expected result after use of the LegacyTransformator on the prior string. */
    public static final String STRING_LEGACY_KUERZEN_EXPECTED ="RC";

    /** String that will be extended by the LegacyTransformator. */
    public static final String STRING_LEGACY_ERWEITERN ="™‰";

    /** Expected result after use of the LegacyTransformator on the prior string. */
    public static final String STRING_LEGACY_ERWEITERN_EXPECTED ="(TM)permil";


    @Test
    public void testKeineAenderung() {
        TranskriptionTransformator transkriptionTransformator = new TranskriptionTransformator();
        transkriptionTransformator.initialisiere(null);

        Transformation transformation = transkriptionTransformator.transformiereMitMetadaten(STRING_KEINE_AENDERUNG);

        Assert.assertEquals(STRING_KEINE_AENDERUNG_EXPECTED, transformation.getTransformierterText());
        Assert.assertTrue(transformation.getMetadatenList().isEmpty());
    }

    @Test
    public void testMapAufLeerstring() {
        TranskriptionTransformator transkriptionTransformator = new TranskriptionTransformator();
        transkriptionTransformator.initialisiere(null);

        Transformation transformation = transkriptionTransformator.transformiereOhneTrimMitMetadaten(STRING_TRANSFORMIERT_AUF_LEER);

        Assert.assertEquals(STRING_TRANSFORMIERT_AUF_LEER_EXPECTED, transformation.getTransformierterText());
        Assert.assertEquals(3, transformation.getMetadatenList().size());

        Assert.assertEquals("™", transformation.getMetadatenList().get(0).getAltesZeichen());
        Assert.assertEquals("2122", transformation.getMetadatenList().get(0).getAlteCodepoints());
        Assert.assertEquals("", transformation.getMetadatenList().get(0).getNeuesZeichen());
        Assert.assertEquals("", transformation.getMetadatenList().get(0).getNeueCodepoints());
        Assert.assertEquals(0, transformation.getMetadatenList().get(0).getAltePosition());
        Assert.assertEquals(0, transformation.getMetadatenList().get(0).getNeuePosition());

        Assert.assertEquals("™", transformation.getMetadatenList().get(1).getAltesZeichen());
        Assert.assertEquals("2122", transformation.getMetadatenList().get(1).getAlteCodepoints());
        Assert.assertEquals("", transformation.getMetadatenList().get(1).getNeuesZeichen());
        Assert.assertEquals("", transformation.getMetadatenList().get(1).getNeueCodepoints());
        Assert.assertEquals(2, transformation.getMetadatenList().get(1).getAltePosition());
        Assert.assertEquals(1, transformation.getMetadatenList().get(1).getNeuePosition());

        Assert.assertEquals("r", transformation.getMetadatenList().get(2).getAltesZeichen());
        Assert.assertEquals("0072", transformation.getMetadatenList().get(2).getAlteCodepoints());
        Assert.assertEquals("R", transformation.getMetadatenList().get(2).getNeuesZeichen());
        Assert.assertEquals("0052", transformation.getMetadatenList().get(2).getNeueCodepoints());
        Assert.assertEquals(3, transformation.getMetadatenList().get(2).getAltePosition());
        Assert.assertEquals(1, transformation.getMetadatenList().get(2).getNeuePosition());
    }

    @Test
    public void testEntfernePraefix() {
        TranskriptionTransformator transkriptionTransformator = new TranskriptionTransformator();
        transkriptionTransformator.initialisiere(null);
        Transformation transformation = transkriptionTransformator.transformiereMitMetadaten(STRING_ENTFERNE_PRAEFIX);
        Assert.assertFalse(transformation.getMetadatenList().isEmpty());

        Assert.assertEquals(STRING_ENTFERNE_PRAEFIX_EXPECTED, transformation.getTransformierterText());
        Assert.assertEquals(3, transformation.getMetadatenList().size());

        Assert.assertEquals("ツ", transformation.getMetadatenList().get(0).getAltesZeichen());
        Assert.assertEquals("30C4", transformation.getMetadatenList().get(0).getAlteCodepoints());
        Assert.assertEquals(" ", transformation.getMetadatenList().get(0).getNeuesZeichen());
        Assert.assertEquals("0020", transformation.getMetadatenList().get(0).getNeueCodepoints());
        Assert.assertEquals(0, transformation.getMetadatenList().get(0).getAltePosition());
        Assert.assertEquals(-1, transformation.getMetadatenList().get(0).getNeuePosition());

        Assert.assertEquals("Ä", transformation.getMetadatenList().get(1).getAltesZeichen());
        Assert.assertEquals("00C4", transformation.getMetadatenList().get(1).getAlteCodepoints());
        Assert.assertEquals("AE", transformation.getMetadatenList().get(1).getNeuesZeichen());
        Assert.assertEquals("0041 + 0045", transformation.getMetadatenList().get(1).getNeueCodepoints());
        Assert.assertEquals(2, transformation.getMetadatenList().get(1).getAltePosition());
        Assert.assertEquals(1, transformation.getMetadatenList().get(1).getNeuePosition());

        Assert.assertEquals("Ö", transformation.getMetadatenList().get(2).getAltesZeichen());
        Assert.assertEquals("00D6", transformation.getMetadatenList().get(2).getAlteCodepoints());
        Assert.assertEquals("OE", transformation.getMetadatenList().get(2).getNeuesZeichen());
        Assert.assertEquals("004F + 0045", transformation.getMetadatenList().get(2).getNeueCodepoints());
        Assert.assertEquals(3, transformation.getMetadatenList().get(2).getAltePosition());
        Assert.assertEquals(3, transformation.getMetadatenList().get(2).getNeuePosition());
    }

    @Test
    public void testEntferneSuffix() {
        TranskriptionTransformator transkriptionTransformator = new TranskriptionTransformator();
        transkriptionTransformator.initialisiere(null);
        Transformation transformation = transkriptionTransformator.transformiereMitMetadaten(STRING_ENTFERNE_SUFFIX);
        Assert.assertFalse(transformation.getMetadatenList().isEmpty());

        Assert.assertEquals(STRING_ENTFERNE_SUFFIX_EXPECTED, transformation.getTransformierterText());
        Assert.assertEquals(1, transformation.getMetadatenList().size());

        Assert.assertEquals("ツ", transformation.getMetadatenList().get(0).getAltesZeichen());
        Assert.assertEquals("30C4", transformation.getMetadatenList().get(0).getAlteCodepoints());
        Assert.assertEquals(" ", transformation.getMetadatenList().get(0).getNeuesZeichen());
        Assert.assertEquals("0020", transformation.getMetadatenList().get(0).getNeueCodepoints());
        Assert.assertEquals(1, transformation.getMetadatenList().get(0).getAltePosition());
        Assert.assertEquals(-2, transformation.getMetadatenList().get(0).getNeuePosition());
    }

    @Test
    public void testEntferneMehrereLeerzeichen() {
        TranskriptionTransformator transkriptionTransformator = new TranskriptionTransformator();
        transkriptionTransformator.initialisiere(null);
        Transformation transformation = transkriptionTransformator.transformiereMitMetadaten(STRING_ENTFERNE_MEHRERE_LEERZEICHEN);
        Assert.assertFalse(transformation.getMetadatenList().isEmpty());

        Assert.assertEquals(STRING_ENTFERNE_MEHRERE_LEERZEICHEN_EXPECTED, transformation.getTransformierterText());
        Assert.assertEquals(6, transformation.getMetadatenList().size());

        Assert.assertEquals("ツ", transformation.getMetadatenList().get(0).getAltesZeichen());
        Assert.assertEquals("30C4", transformation.getMetadatenList().get(0).getAlteCodepoints());
        Assert.assertEquals(" ", transformation.getMetadatenList().get(0).getNeuesZeichen());
        Assert.assertEquals("0020", transformation.getMetadatenList().get(0).getNeueCodepoints());
        Assert.assertEquals(1, transformation.getMetadatenList().get(0).getAltePosition());
        Assert.assertEquals(1, transformation.getMetadatenList().get(0).getNeuePosition());

        Assert.assertEquals("ツ", transformation.getMetadatenList().get(1).getAltesZeichen());
        Assert.assertEquals("30C4", transformation.getMetadatenList().get(1).getAlteCodepoints());
        Assert.assertEquals(" ", transformation.getMetadatenList().get(1).getNeuesZeichen());
        Assert.assertEquals("0020", transformation.getMetadatenList().get(1).getNeueCodepoints());
        Assert.assertEquals(2, transformation.getMetadatenList().get(1).getAltePosition());
        Assert.assertEquals(1, transformation.getMetadatenList().get(1).getNeuePosition());

        Assert.assertEquals("ツ", transformation.getMetadatenList().get(2).getAltesZeichen());
        Assert.assertEquals("30C4", transformation.getMetadatenList().get(2).getAlteCodepoints());
        Assert.assertEquals(" ", transformation.getMetadatenList().get(2).getNeuesZeichen());
        Assert.assertEquals("0020", transformation.getMetadatenList().get(2).getNeueCodepoints());
        Assert.assertEquals(3, transformation.getMetadatenList().get(2).getAltePosition());
        Assert.assertEquals(1, transformation.getMetadatenList().get(2).getNeuePosition());

        Assert.assertEquals("r", transformation.getMetadatenList().get(3).getAltesZeichen());
        Assert.assertEquals("0072", transformation.getMetadatenList().get(3).getAlteCodepoints());
        Assert.assertEquals("R", transformation.getMetadatenList().get(3).getNeuesZeichen());
        Assert.assertEquals("0052", transformation.getMetadatenList().get(3).getNeueCodepoints());
        Assert.assertEquals(4, transformation.getMetadatenList().get(3).getAltePosition());
        Assert.assertEquals(2, transformation.getMetadatenList().get(3).getNeuePosition());

        Assert.assertEquals("ツ", transformation.getMetadatenList().get(4).getAltesZeichen());
        Assert.assertEquals("30C4", transformation.getMetadatenList().get(4).getAlteCodepoints());
        Assert.assertEquals(" ", transformation.getMetadatenList().get(4).getNeuesZeichen());
        Assert.assertEquals("0020", transformation.getMetadatenList().get(4).getNeueCodepoints());
        Assert.assertEquals(5, transformation.getMetadatenList().get(4).getAltePosition());
        Assert.assertEquals(3, transformation.getMetadatenList().get(4).getNeuePosition());

        Assert.assertEquals("r", transformation.getMetadatenList().get(5).getAltesZeichen());
        Assert.assertEquals("0072", transformation.getMetadatenList().get(5).getAlteCodepoints());
        Assert.assertEquals("R", transformation.getMetadatenList().get(5).getNeuesZeichen());
        Assert.assertEquals("0052", transformation.getMetadatenList().get(5).getNeueCodepoints());
        Assert.assertEquals(6, transformation.getMetadatenList().get(5).getAltePosition());
        Assert.assertEquals(4, transformation.getMetadatenList().get(5).getNeuePosition());
    }

    @Test
    public void testDinNormKuerzen() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Transformation transformation = legacyTransformator.transformiereMitMetadaten(STRING_LEGACY_KUERZEN);
        Assert.assertFalse(transformation.getMetadatenList().isEmpty());

        Assert.assertEquals(STRING_LEGACY_KUERZEN_EXPECTED, transformation.getTransformierterText());
        Assert.assertEquals(2, transformation.getMetadatenList().size());

        Assert.assertEquals("R̥̄", transformation.getMetadatenList().get(0).getAltesZeichen());
        Assert.assertEquals("0052 + 0325 + 0304", transformation.getMetadatenList().get(0).getAlteCodepoints());
        Assert.assertEquals("R", transformation.getMetadatenList().get(0).getNeuesZeichen());
        Assert.assertEquals("0052", transformation.getMetadatenList().get(0).getNeueCodepoints());
        Assert.assertEquals(0, transformation.getMetadatenList().get(0).getAltePosition());
        Assert.assertEquals(0, transformation.getMetadatenList().get(0).getNeuePosition());

        Assert.assertEquals("C̨̆", transformation.getMetadatenList().get(1).getAltesZeichen());
        Assert.assertEquals("0043 + 0328 + 0306", transformation.getMetadatenList().get(1).getAlteCodepoints());
        Assert.assertEquals("C", transformation.getMetadatenList().get(1).getNeuesZeichen());
        Assert.assertEquals("0043", transformation.getMetadatenList().get(1).getNeueCodepoints());
        Assert.assertEquals(3, transformation.getMetadatenList().get(1).getAltePosition());
        Assert.assertEquals(1, transformation.getMetadatenList().get(1).getNeuePosition());
    }

    @Test
    public void testDinNormErweitern() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Transformation transformation = legacyTransformator.transformiereMitMetadaten(STRING_LEGACY_ERWEITERN);
        Assert.assertFalse(transformation.getMetadatenList().isEmpty());

        Assert.assertEquals(STRING_LEGACY_ERWEITERN_EXPECTED, transformation.getTransformierterText());
        Assert.assertEquals(2, transformation.getMetadatenList().size());

        Assert.assertEquals("™", transformation.getMetadatenList().get(0).getAltesZeichen());
        Assert.assertEquals("2122", transformation.getMetadatenList().get(0).getAlteCodepoints());
        Assert.assertEquals("(TM)", transformation.getMetadatenList().get(0).getNeuesZeichen());
        Assert.assertEquals("0028 + 0054 + 004D + 0029", transformation.getMetadatenList().get(0).getNeueCodepoints());
        Assert.assertEquals(0, transformation.getMetadatenList().get(0).getAltePosition());
        Assert.assertEquals(0, transformation.getMetadatenList().get(0).getNeuePosition());

        Assert.assertEquals("‰", transformation.getMetadatenList().get(1).getAltesZeichen());
        Assert.assertEquals("2030", transformation.getMetadatenList().get(1).getAlteCodepoints());
        Assert.assertEquals("permil", transformation.getMetadatenList().get(1).getNeuesZeichen());
        Assert.assertEquals("0070 + 0065 + 0072 + 006D + 0069 + 006C", transformation.getMetadatenList().get(1).getNeueCodepoints());
        Assert.assertEquals(1, transformation.getMetadatenList().get(1).getAltePosition());
        Assert.assertEquals(4, transformation.getMetadatenList().get(1).getNeuePosition());
    }

}
