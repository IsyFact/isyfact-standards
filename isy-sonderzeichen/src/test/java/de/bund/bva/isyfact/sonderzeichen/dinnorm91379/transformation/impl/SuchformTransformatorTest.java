package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SuchformTransformatorTest {

    /**
     * String containing LETTERS (Datatype C) in upper and lower case.
     */
    public static final String STRING_NORMATIVE_LETTERS = "ØÞø  c̆";

    /**
     * String containing LETTERS (Datatype C) in upper and lower case and other characters.
     */
    public static final String STRING_NORMATIVE_LETTERS_PLUS = "ØЬÞø-  c̆";

    /**
     * String containing LETTERS and SYMBOLS introduced with DIN Norm 91375.
     */
    public static final String STRING_NEW_DIN_NORM_LETTERS = "ē̍ō̍ḗ′″";

    @Test
    public void checkSuchformTransformationLettersOnly() {
        SuchformTransformator suchformtransformator = new SuchformTransformator();
        suchformtransformator.initialisiere(null);
        assertEquals("OETHOE  C", suchformtransformator.transformiereOhneTrim(STRING_NORMATIVE_LETTERS));
        assertEquals("OETHOE C", suchformtransformator.transformiere(STRING_NORMATIVE_LETTERS));
    }

    @Test
    public void checkSuchformTransformationLettersPlus() {
        SuchformTransformator suchformtransformator = new SuchformTransformator();
        suchformtransformator.initialisiere(null);
        assertEquals("OE THOE   C", suchformtransformator.transformiereOhneTrim(STRING_NORMATIVE_LETTERS_PLUS));
        assertEquals("OE THOE C", suchformtransformator.transformiere(STRING_NORMATIVE_LETTERS_PLUS));
    }

    @Test
    public void checkSuchformTransformationNewLetters() {
        SuchformTransformator suchformtransformator = new SuchformTransformator();
        suchformtransformator.initialisiere(null);
        assertEquals("EOE  ", suchformtransformator.transformiereOhneTrim(STRING_NEW_DIN_NORM_LETTERS));
        assertEquals("EOE", suchformtransformator.transformiere(STRING_NEW_DIN_NORM_LETTERS));
    }
}
