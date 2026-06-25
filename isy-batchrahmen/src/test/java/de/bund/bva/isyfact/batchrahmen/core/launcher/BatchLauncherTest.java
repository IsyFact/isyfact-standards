package de.bund.bva.isyfact.batchrahmen.core.launcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.test.BatchProtokollTester;

public class BatchLauncherTest {

    /**
     * File for Batch protocol. Is set in {@link #init()}.
     */
    private static String ERGEBNIS_DATEI;

    @Before
    public void init() {
        try {
            final String ergebnisdateiPath = "/resources/batch/ausgabe/ergebnisdatei.xml";
            final URL ergebnisdateiUrl = BatchLauncherTest.class.getResource(ergebnisdateiPath);
            ERGEBNIS_DATEI = new File(Objects.requireNonNull(ergebnisdateiUrl, String.format(
                    "failed to find classpath-resource: %s", ergebnisdateiPath)).toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if a {@link BatchAusfuehrungsException} with error code "BAT420" (in case of a
     * {@link ClassNotFoundException}) is thrown when a configuration class is not found.
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

        System.setProperty("spring.main.web-application-type", "none");
        BatchProtokollTester bpt;
        BatchLauncher.run(new String[] {"-start", "-cfg", "/resources/batch/batch-launcher-test.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI});
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertThat(bpt.getAnzahlFehler()).isZero();
    }
}
