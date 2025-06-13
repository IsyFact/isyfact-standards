package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

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
        assertFalse(Transskriptionsregeln.isWortende(null, 1, 2, trennzeichen));

    }

    @Test
    public void testWortMitteNull(){
        assertFalse(Transskriptionsregeln.isWortmitte(null, 1, 2, trennzeichen));

    }

    @Test
    public void testWortAnfangNull(){
        assertFalse(Transskriptionsregeln.isWortanfang(null,0,trennzeichen));

    }
    @Test
    public void testVorZeichenNull() {
        assertFalse(Transskriptionsregeln.isVorZeichen(null, 4, 2, pruefzeichen));
    }

}
