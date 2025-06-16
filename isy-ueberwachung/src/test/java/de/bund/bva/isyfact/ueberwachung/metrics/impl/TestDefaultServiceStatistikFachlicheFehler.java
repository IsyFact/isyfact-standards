package de.bund.bva.isyfact.ueberwachung.metrics.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.ueberwachung.common.data.Fehler;
import de.bund.bva.isyfact.ueberwachung.common.data.ToMitFehlerCollection;
import de.bund.bva.isyfact.ueberwachung.common.data.ToMitFehlerFeld;
import de.bund.bva.isyfact.ueberwachung.common.data.ToObjektMitFehlernInOberklasse;
import de.bund.bva.isyfact.ueberwachung.common.data.ToObjekthierarchieMitFehlern;

/**
 * Tests für Erkennung fachlicher Fehler.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"isy.logging.anwendung.name=Test",
        "isy.logging.anwendung.typ=Test",
        "isy.logging.anwendung.version=0.1"})
public class TestDefaultServiceStatistikFachlicheFehler {

    /**
     * Tests the recognition of individual error objects as attributes
     */
    @Test
    public void testZaehleDirekteFehler() {

        ToMitFehlerFeld toMitFehlerFeld = new ToMitFehlerFeld();
        erwarteKeineFehler(toMitFehlerFeld);

        toMitFehlerFeld.setFehler(new Fehler());
        erwarteFehler(toMitFehlerFeld);
    }

    /**
     * Tests the recognition of collection errors
     */
    @Test
    public void testZaehleCollectionFehler() {
        ToMitFehlerCollection toCollection = new ToMitFehlerCollection();
        erwarteKeineFehler(toCollection);
        toCollection.setFehlerliste(null);
        erwarteKeineFehler(toCollection);

        List<Fehler> fehlerlisteMitFehler = new ArrayList<>();
        fehlerlisteMitFehler.add(new Fehler());
        toCollection.setFehlerliste(fehlerlisteMitFehler);
        erwarteFehler(toCollection);
    }

    /**
     * Tests the recognition of individual error objects and error collection in an object hierarchy
     */
    @Test
    public void testObjekthierarchie() {
        // Annotated attribute, but null
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
        // Annotated attribute, but null
        ToObjektMitFehlernInOberklasse toObject = new ToObjektMitFehlernInOberklasse();
        erwarteKeineFehler(toObject);

        toObject.setFehler(new Fehler());
        erwarteFehler(toObject);

        toObject.setFehler(null);
        erwarteKeineFehler(toObject);

        List<Fehler> fehlerliste = new ArrayList<>();
        fehlerliste.add(new Fehler());
        toObject.setFehlerliste(fehlerliste);
        erwarteFehler(toObject);
    }

    /**
     * Tests the recognition of individual error objects and error collections in an inheritance hierarchy
     * of an object hierarchy
     *
     * ULTIMATE!!
     */
    @Test
    public void testUltimateHierarchieUndObjektstruktur() {
        // Annotated attribute, but null
        ToObjektMitFehlernInOberklasse toObject = new ToObjektMitFehlernInOberklasse();
        erwarteKeineFehler(toObject);

        ToObjektMitFehlernInOberklasse subHierarchie = new ToObjektMitFehlernInOberklasse();
        toObject.setToObjektMitFehlernInOberklasse(subHierarchie);
        erwarteKeineFehler(toObject);

        // Now insert error in superclass of the subhierarchy
        subHierarchie.setFehler(new Fehler());
        erwarteFehler(toObject);
    }

    /**
     * Checks the object structure and expects a technical error
     *
     * @param object
     *            Objektstruktur
     */
    private void erwarteFehler(Object object) {
        assertTrue(DefaultServiceStatistik.pruefeObjektAufFehler(object, null, 0));
    }

    /**
     * Checks the object structure and expects no technical errors
     *
     * @param object
     *            Objektstruktur
     */
    private void erwarteKeineFehler(Object object) {
        assertFalse(DefaultServiceStatistik.pruefeObjektAufFehler(object, null, 0));
    }

}
