package de.bund.bva.isyfact.logging;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import de.bund.bva.isyfact.logging.util.BeanGroessePruefer;

/**
 * Test des Prüfers für die Größe von Log-Parametern.
 */
public class BeanGroessePrueferTest extends AbstractLogTest {

    /**
     * Testet die erfolgreiche Erkennung eines primitiven Typs.
     */
    @Test
    public void testePrimitivenTyp() {
        BeanGroessePruefer pruefer = new BeanGroessePruefer();

        long daten = 42L;

        assertFalse(pruefer.pruefeGroesse(daten, 6L));
    }

    /**
     * Testet die erfolgreiche Erkennung einer Klasse als Attribut.
     */
    @Test
    public void testeKlasse() {
        BeanGroessePruefer pruefer = new BeanGroessePruefer();

        Container container = new Container();
        container.klasse = Container.class;

        assertTrue(pruefer.pruefeGroesse(container, 100L));
    }

    /**
     * Testet die erfolgreiche Erkennung von Arrays.
     */
    @Test
    public void testeArrays() {
        BeanGroessePruefer pruefer = new BeanGroessePruefer();

        byte[] binaerDaten = new byte[50000000];
        new Random().nextBytes(binaerDaten);

        assertFalse(pruefer.pruefeGroesse(binaerDaten, 49999999L));
    }

    /**
     * Testet die erfolgreiche Erkennung von Collections.
     */
    @Test
    public void testeCollections() {
        BeanGroessePruefer pruefer = new BeanGroessePruefer();

        byte[] binaerDaten = new byte[50000000];
        new Random().nextBytes(binaerDaten);

        Map<Long, Object> testMap = new HashMap<>();
        testMap.put(4711L, binaerDaten);
        assertFalse(pruefer.pruefeGroesse(testMap, 10000L));

        Set<Object> testSet = new HashSet<>();
        testSet.add(binaerDaten);
        assertFalse(pruefer.pruefeGroesse(testSet, 10000L));

        List<Object> testList = new ArrayList<>();
        testList.add(binaerDaten);
        assertFalse(pruefer.pruefeGroesse(testList, 10000L));
    }

    /**
     * Testet die erfolgreiche Erkennung von Attributen in Objekten.
     */
    @Test
    public void testeAttributeInObjekten() {
        BeanGroessePruefer pruefer = new BeanGroessePruefer();

        Container container = new Container();
        container.daten = new byte[50000000];
        new Random().nextBytes(container.daten);

        assertFalse(pruefer.pruefeGroesse(container, 49999999L));
    }

    static class Container {
        public byte[] daten;

        public Class<?> klasse;
    }

}