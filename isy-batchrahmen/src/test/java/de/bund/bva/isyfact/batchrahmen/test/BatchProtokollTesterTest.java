package de.bund.bva.isyfact.batchrahmen.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BatchProtokollTesterTest {

    private BatchProtokollTester batchProtokollTester;

    @BeforeEach
    public void setUp() throws Exception {
        String testProtokoll = getClass().getResource("/resources/batch/test_protokoll.xml").toURI().toString();
        batchProtokollTester = new BatchProtokollTester(testProtokoll);

    }

    @Test
    public void enthaeltMeldungsId() {
        assertTrue(batchProtokollTester.enthaeltMeldungsId("INFO2"));
        assertFalse(batchProtokollTester.enthaeltMeldungsId("NICHTVORHANDEN"));
    }

    @Test
    public void isFehlerfrei() {
        assertFalse(batchProtokollTester.isFehlerfrei());
    }

    @Test
    public void enthaeltFehler() {
        assertTrue(batchProtokollTester.enthaeltFehler("FEHLER1"));
        assertTrue(batchProtokollTester.enthaeltFehler("FEHLER2"));
        assertFalse(batchProtokollTester.enthaeltFehler("NICHTVORHANDEN"));
    }

    @Test
    public void enthaeltFehlerMitText() {
        assertTrue(batchProtokollTester.enthaeltFehler("FEHLER1", "Fehler1"));
        assertFalse(batchProtokollTester.enthaeltFehler("FEHLER1", "Fehler2"));
        assertFalse(batchProtokollTester.enthaeltFehler("FEHLER2", "Fehler1"));
    }

    @Test
    public void getFehlerIds() {
        Collection<String> fehlerIds = batchProtokollTester.getFehlerIds();
        assertEquals(3, fehlerIds.size());
        assertTrue(fehlerIds.contains("FEHLER1"));
        assertTrue(fehlerIds.contains("FEHLER2"));
    }

    @Test
    public void getFehlerIdsEindeutig() {
        Collection<String> fehlerIds = batchProtokollTester.getFehlerIdsEindeutig();
        assertEquals(2, fehlerIds.size());
        assertTrue(fehlerIds.contains("FEHLER1"));
        assertTrue(fehlerIds.contains("FEHLER2"));
    }

    @Test
    public void isStartmodusRestart() {
        assertTrue(batchProtokollTester.isStartmodusRestart());
    }

    @Test
    public void getAnzahlFehler() {
        assertEquals(3, batchProtokollTester.getAnzahlFehler());
    }

    @Test
    public void getAnzahlFehlerMitId() {
        assertEquals(2, batchProtokollTester.getAnzahlFehler("FEHLER1"));
        assertEquals(1, batchProtokollTester.getAnzahlFehler("FEHLER2"));
        assertEquals(0, batchProtokollTester.getAnzahlFehler("NICHTVORHANDEN"));
    }

    @Test
    public void getAnzahlFehlerEindeutig() {
        assertEquals(2, batchProtokollTester.getAnzahlFehlerEindeutig());
    }

    @Test
    public void getStatistikwert() {
        assertEquals(10, batchProtokollTester.getStatistikwert("STATISTIK1"));
        assertEquals(-1, batchProtokollTester.getStatistikwert("NICHTVORHANDEN"));
    }
}