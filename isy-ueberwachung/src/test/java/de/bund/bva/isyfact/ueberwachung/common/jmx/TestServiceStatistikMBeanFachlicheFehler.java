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
package de.bund.bva.isyfact.ueberwachung.common.jmx;

import java.util.ArrayList;
import java.util.List;

import de.bund.bva.isyfact.ueberwachung.common.jmx.data.Fehler;
import de.bund.bva.isyfact.ueberwachung.common.jmx.data.ToMitFehlerCollection;
import de.bund.bva.isyfact.ueberwachung.common.jmx.data.ToMitFehlerFeld;
import de.bund.bva.isyfact.ueberwachung.common.jmx.data.ToObjektMitFehlernInOberklasse;
import de.bund.bva.isyfact.ueberwachung.common.jmx.data.ToObjekthierarchieMitFehlern;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests für Erkennung fachlicher Fehler.
 *
 * @author Capgemini, Julian Meisel
 * @version $Id: TestServiceStatistikMBeanFachlicheFehler.java 129945 2015-02-09 10:03:33Z sdm_jmeisel $
 */
public class TestServiceStatistikMBeanFachlicheFehler {

    private static ServiceStatistikMBean mBean = new ServiceStatistikMBean();

    /**
     * Testet die Erkennung von einzelnen Fehlerobjekten als Attribute.
     */
    @Test
    public void testZaehleDirekteFehler() {

        ToMitFehlerFeld toMitFehlerFeld = new ToMitFehlerFeld();
        erwarteKeineFehler(toMitFehlerFeld);

        toMitFehlerFeld.setFehler(new Fehler());
        erwarteFehler(toMitFehlerFeld);
    }

    /**
     * Testet die Erkennung von Collection Fehlern.
     */
    @Test
    public void testZaehleCollectionFehler() {
        ToMitFehlerCollection toCollection = new ToMitFehlerCollection();
        erwarteKeineFehler(toCollection);
        toCollection.setFehlerliste(null);
        erwarteKeineFehler(toCollection);

        List<Fehler> fehlerlisteMitFehler = new ArrayList<Fehler>();
        fehlerlisteMitFehler.add(new Fehler());
        toCollection.setFehlerliste(fehlerlisteMitFehler);
        erwarteFehler(toCollection);
    }

    /**
     * Testet die Erkennung von einzelnen Fehlerobjekten und Fehlercollections in einer Objekthierarchie.
     */
    @Test
    public void testObjekthierarchie() {
        // Annotiertes Attribut, aber null
        ToObjekthierarchieMitFehlern toObject = new ToObjekthierarchieMitFehlern();
        erwarteKeineFehler(toObject);

        toObject.setTo(new ToMitFehlerFeld());
        erwarteKeineFehler(toObject);

        toObject.getTo().setFehler(new Fehler());
        erwarteFehler(toObject);

        toObject.setTo(null);
        erwarteKeineFehler(toObject);

        toObject.setToColl(new ToMitFehlerCollection());
        toObject.getToColl().getFehlerliste().add(new Fehler());
        erwarteFehler(toObject);
    }

    /**
     * Testet die Erkennung von einzelnen Fehlerobjekten und Fehlercollections in einer Vererbungshierarchie.
     */
    @Test
    public void testVererbunghierarchie() {
        // Annotiertes Attribut, aber null
        ToObjektMitFehlernInOberklasse toObject = new ToObjektMitFehlernInOberklasse();
        erwarteKeineFehler(toObject);

        toObject.setFehler(new Fehler());
        erwarteFehler(toObject);

        toObject.setFehler(null);
        erwarteKeineFehler(toObject);

        List<Fehler> fehlerliste = new ArrayList<Fehler>();
        fehlerliste.add(new Fehler());
        toObject.setFehlerliste(fehlerliste);
        erwarteFehler(toObject);
    }

    /**
     * Testet die Erkennung von einzelnen Fehlerobjekten und Fehlercollections in einer Vererbungshierarchie
     * einer Objekthierarchie.
     *
     * ULTIMATE!!
     */
    @Test
    public void testUltimateHierarchieUndObjektstruktur() {
        // Annotiertes Attribut, aber null
        ToObjektMitFehlernInOberklasse toObject = new ToObjektMitFehlernInOberklasse();
        erwarteKeineFehler(toObject);

        ToObjektMitFehlernInOberklasse subHierarchie = new ToObjektMitFehlernInOberklasse();
        toObject.setToObjektMitFehlernInOberklasse(subHierarchie);
        erwarteKeineFehler(toObject);

        // Jetzt Fehler in der Oberklasse der Subhierarchie einfügen
        subHierarchie.setFehler(new Fehler());
        erwarteFehler(toObject);
    }

    /**
     * Prüft die Objektstruktur und erwartet fachliche Fehler.
     *
     * @param object
     *            Objektstruktur
     */
    private void erwarteFehler(Object object) {
        assertTrue(mBean.pruefeObjektAufFehler(object, null, 0));
    }

    /**
     * Prüft die Objektstruktur und erwartet keine fachlichen Fehler.
     *
     * @param object
     *            Objektstruktur
     */
    private void erwarteKeineFehler(Object object) {
        assertFalse(mBean.pruefeObjektAufFehler(object, null, 0));
    }

}
