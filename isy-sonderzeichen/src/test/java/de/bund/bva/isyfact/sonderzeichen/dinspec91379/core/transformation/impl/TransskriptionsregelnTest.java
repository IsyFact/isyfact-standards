/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.sonderzeichen.dinspec91379.core.transformation.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

/**
 * Überprüft die Funktion der Transskriptionsregeln.
 *
 */
public class TransskriptionsregelnTest {

    /** Logger. */
    private static IsyLogger LOG = IsyLoggerFactory.getLogger(TransskriptionsregelnTest.class);

    private String text;
    private Set<Character> trennzeichen;
    private Set<Character> pruefzeichen;

    @Before
    public void setUp(){
        trennzeichen = new HashSet<Character>();
        trennzeichen.add(new Character(' '));
        trennzeichen.add(new Character('-'));
        trennzeichen.add(new Character('/'));

        pruefzeichen = new HashSet<Character>();
        pruefzeichen.add(new Character('a'));
        pruefzeichen.add(new Character('x'));
        pruefzeichen.add(new Character('t'));

        text = "Dies ist ein Testtext";

    }

    /**
     * Testet die Prüfung auf einen Wortanfang.
     */
    @Test
    public void testWortanfang() {
        assertTrue(Transskriptionsregeln.isWortanfang(text, 0, trennzeichen));
        assertTrue(Transskriptionsregeln.isWortanfang(text, 5, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortanfang(text, 3, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortanfang(text, 22, trennzeichen));
    }

    /**
     * Testet die Prüfung auf ein Wortende.
     */
    @Test
    public void testWortende() {
        assertTrue(Transskriptionsregeln.isWortende(text, 0, 4, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortende(text, 0, 3, trennzeichen));
        assertTrue(Transskriptionsregeln.isWortende(text, 16, 5, trennzeichen));
        assertFalse(Transskriptionsregeln.isWortende(text, 22, 4, trennzeichen));
    }

    /**
     * Testet die Prüfung auf die Wortmitte.
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
     * Testet die Prüfung auf Folgezeichen.
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
