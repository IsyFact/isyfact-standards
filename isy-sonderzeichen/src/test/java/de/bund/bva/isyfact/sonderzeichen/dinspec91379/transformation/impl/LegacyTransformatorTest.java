package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.impl;

import org.junit.Assert;
import org.junit.Test;

public class LegacyTransformatorTest {

    /**
     * String containing String.Latin and DIN SPEC 91379 characters.
     */
    public static final String STRING_DIN_SPEC = "a™‰R̥̄CC̨̆àĐ";

    /**
     * String containing String.Latin, DIN SPEC 91379 and non-DIN SPEC characters (to check for correct
     * trimming).
     */
    public static final String STRING_NON_DIN_SPEC = "ツツツaツツツR̥̄CツC̨̆ツツàツ甲ツツ";

    /**
     * String containing single diacritical marks.
     */
    public static final String STRING_DIAKRITISCHE_ZEICHEN = "eR̥̄\u0328m\u0328\u0306ö\u0304";

    @Test
    public void checkLegacyTransformationDinSpec() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Assert.assertEquals("a(TM)permilRCCàDJ", legacyTransformator.transformiereOhneTrim(STRING_DIN_SPEC));
        Assert.assertEquals("a(TM)permilRCCàDJ", legacyTransformator.transformiere(STRING_DIN_SPEC));
    }

    @Test
    public void checkLegacyTransformationNonDinSpec() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Assert.assertEquals("   a   RC C  à    ", legacyTransformator.transformiereOhneTrim(
            STRING_NON_DIN_SPEC));
        Assert.assertEquals("a RC C à", legacyTransformator.transformiere(STRING_NON_DIN_SPEC));
    }

    @Test
    public void checkLegacyTransformationDiacriticalMarks() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Assert.assertEquals("eR m  ö ", legacyTransformator.transformiereOhneTrim(STRING_DIAKRITISCHE_ZEICHEN));
        Assert.assertEquals("eR m ö", legacyTransformator.transformiere(STRING_DIAKRITISCHE_ZEICHEN));
    }

}
