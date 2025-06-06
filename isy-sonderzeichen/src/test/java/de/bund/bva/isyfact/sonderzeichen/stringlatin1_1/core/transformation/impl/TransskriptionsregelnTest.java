package de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.impl;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Checks the function of the transcription rules.
 *
 */
public class TransskriptionsregelnTest {

    private String text;
    private Set<Character> trennzeichen;
    private Set<Character> pruefzeichen;

    @Before
    public void setUp(){
        trennzeichen = new HashSet<>();
        trennzeichen.add(' ');
        trennzeichen.add('-');
        trennzeichen.add('/');

        pruefzeichen = new HashSet<>();
        pruefzeichen.add('a');
        pruefzeichen.add('x');
        pruefzeichen.add('t');

        text = "Dies ist ein Testtext";

    }

    /**
     * Tests the exam for the beginning of a word.
     */
    @Test
    public void testWortanfang() {
        assertTrue(Transskriptionsregeln.isWortanfang(text, 0, trennzeichen));
        assertTrue(Transskriptionsregeln.isWortanfang(text, 5, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortanfang(text, 3, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortanfang(text, 22, trennzeichen));
    }

    /**
     * Tests the exam for the end of a word.
     */
    @Test
    public void testWortende() {
        assertTrue(Transskriptionsregeln.isWortende(text, 0, 4, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortende(text, 0, 3, trennzeichen));
        assertTrue(Transskriptionsregeln.isWortende(text, 16, 5, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortende(text, 22, 4, trennzeichen));
    }

    /**
     * Tests the middle of the word exam.
     */
    @Test
    public void testWortmitte() {
        assertTrue(Transskriptionsregeln.isWortmitte(text, 1, 2, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortmitte(text, 0, 3, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortmitte(text, 1, 3, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortmitte(text, 0, 4, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortmitte(text, 16, 5, trennzeichen));
    }

    /**
     * Tests the exam for follow-up characters.
     */
    @Test
    public void testVorZeichen() {
        assertTrue(Transskriptionsregeln.isVorZeichen(text, 4, 3, pruefzeichen));
        assertFalse(Transskriptionsregeln.isVorZeichen(text, 4, 2, pruefzeichen));
        assertFalse(Transskriptionsregeln.isVorZeichen(text, 16, 5, pruefzeichen));
    }

    @Test
    public void testWortEndeNull(){
        text = null;
        assertFalse(Transskriptionsregeln.isWortende(text, 1, 2, trennzeichen));

    }

    @Test
    public void testWortMitteNull(){
        text = null;
        assertFalse(Transskriptionsregeln.isWortmitte(text, 1, 2, trennzeichen));

    }

    @Test
    public void testWortAnfangNull(){
        text = null;
        assertFalse(Transskriptionsregeln.isWortanfang(text,0,trennzeichen));

    }
    @Test
    public void testVorZeichenNull() {
        text = null;
        assertFalse(Transskriptionsregeln.isVorZeichen(text, 4, 2, pruefzeichen));
    }

}
