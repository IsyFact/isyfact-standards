package de.bund.bva.isyfact.batchrahmen.core.launcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.batchrahmen.test.BatchProtokollTester;


public class BatchLauncherTest {

    /** File for Batch protocol. Is set in {@link #init()}. */
    private static String ERGEBNIS_DATEI;

    @BeforeEach
    public void init() {
        try {
            ERGEBNIS_DATEI = new File(
                BatchLauncherTest.class.getResource("/resources/batch/ausgabe/ergebnisdatei.xml").toURI())
                .getAbsolutePath();
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if a BatchAusfuehrungsException with error code "BAT420" (in case of a ClassNotFoundException)
     * is thrown when a configuration class is not found.
     */

    @Test
    public void testKlasseNichtGefunden() {
        BatchProtokollTester bpt;
        BatchLauncher.run(new String[] {"-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI});
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.enthaeltFehler("BAT420"));
    }

    @Test
    public void testConditionalBeanOverride() {
        BatchProtokollTester bpt;
        BatchLauncher.run(new String[] {"-start", "-cfg", "/resources/batch/batch-launcher-test.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI});
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertThat(bpt.getAnzahlFehler()).isZero();
    }

}