package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl;

import org.junit.Assert;
import org.junit.Test;

public class LegacyTransformatorTest {

    /**
     * String containing String.Latin and DIN Norm 91379 characters.
     */
    public static final String STRING_DIN_NORM = "a™‰R̥̄CC̨̆àĐē̍ō̍ḗ \t′″";

    /**
     * String containing String.Latin, DIN Norm 91379 and non-DIN Norm characters (to check for correct
     * trimming).
     */
    public static final String STRING_NON_DIN_NORM = "ツツツaツツツR̥̄CツC̨̆ツツàツ甲ツツ";

    /**
     * String containing single diacritical marks.
     */
    public static final String STRING_DIAKRITISCHE_ZEICHEN = "eR̥̄\u0328m\u0328\u0306ö\u0304";

    @Test
    public void checkLegacyTransformationDinNorm() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Assert.assertEquals("a(TM)permilRCCàĐeoe \t'\"", legacyTransformator.transformiereOhneTrim(STRING_DIN_NORM));
        Assert.assertEquals("a(TM)permilRCCàĐeoe \t'\"", legacyTransformator.transformiere(STRING_DIN_NORM));
    }

    @Test
    public void checkLegacyTransformationNonDinNorm() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Assert.assertEquals("   a   RC C  à    ", legacyTransformator.transformiereOhneTrim(
            STRING_NON_DIN_NORM));
        Assert.assertEquals("a RC C à", legacyTransformator.transformiere(STRING_NON_DIN_NORM));
    }

    @Test
    public void checkLegacyTransformationDiacriticalMarks() {
        LegacyTransformator legacyTransformator = new LegacyTransformator();
        legacyTransformator.initialisiere(null);
        Assert.assertEquals("eR m  ö ", legacyTransformator.transformiereOhneTrim(STRING_DIAKRITISCHE_ZEICHEN));
        Assert.assertEquals("eR m ö", legacyTransformator.transformiere(STRING_DIAKRITISCHE_ZEICHEN));
    }

}
