package de.bund.bva.isyfact.batchrahmen.batch.konfiguration;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenKonfigurationException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncherTest;

public class BatchKonfigurationTest {
    private BatchKonfiguration batchKonfiguration;
    private Properties properties;
    private static String ERGEBNIS_DATEI;
    @BeforeEach
    public void setUp() {
        try {
            ERGEBNIS_DATEI = new File(
                    BatchLauncherTest.class.getResource("/resources/batch/ausgabe/ergebnisdatei.xml").toURI())
                    .getAbsolutePath();
        } catch (URISyntaxException e) {
            Assertions.fail(e.getMessage());
        }
        properties = Mockito.mock(Properties.class);
    }

    @Test
    public void BatchKonfigurationCfgEmpty() {
        assertThrows(BatchrahmenParameterException.class, () -> {
            batchKonfiguration = new BatchKonfiguration(new String[]{"-start", "-cfg", "", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI});
        });
    }

    @Test
    public void BatchKonfiguration() {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals(1, batchKonfiguration.getBatchRahmenSpringKonfigFiles().size());
        Assertions.assertEquals("de.bund.bva.isyfact.batchrahmen.BatchrahmenTestConfig123", batchKonfiguration.getBatchRahmenSpringKonfigFiles().get(0));
        Assertions.assertEquals(1, batchKonfiguration.getAnwendungSpringKonfigFiles().size());
        Assertions.assertEquals("de.bund.bva.isyfact.batchrahmen.AnwendungTestConfig123", batchKonfiguration.getAnwendungSpringKonfigFiles().get(0));
    }

    @Test
    public  void BatchKonfigurationGetSpringProfiles()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals(1, batchKonfiguration.getSpringProfiles().length);
    }

    @Test
    public  void BatchKonfigurationGetSpringProfilesNull()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals(0, batchKonfiguration.getSpringProfiles().length);
    }

    @Test
    public void BatchKonfigurationgGetAsLong()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals(1000, batchKonfiguration.getAsLong("LongProperty"));
    }

    @Test
    public void BatchKonfigurationgGetAsLongKeyNotAvailable()
    {
        assertThrows(BatchrahmenKonfigurationException.class, () -> {
            batchKonfiguration = new BatchKonfiguration(new String[]{
                    "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                    "-PROPERTY_SPRINGPROFILES_PROPERTIES"
            });
            batchKonfiguration.getAsLong("LongProperty");
            batchKonfiguration.getAsLong("BatchName");
        });
    }

    @Test
    public void BatchKonfigurationgGetAsString()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals("testBatchLauncher", batchKonfiguration.getAsString("BatchName"));
    }

    @Test
    public void BatchKonfigurationgGetAsStringException()
    {
        assertThrows(BatchrahmenKonfigurationException.class, () -> {
            batchKonfiguration = new BatchKonfiguration(new String[]{
                    "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                    "-PROPERTY_SPRINGPROFILES_PROPERTIES"
            });
            batchKonfiguration.getAsString("NotSet");
        });
    }

    @Test
    public void BatchKonfigurationgGetAsBooleanException()
    {
        assertThrows(BatchrahmenKonfigurationException.class, () -> {
            batchKonfiguration = new BatchKonfiguration(new String[]{
                    "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                    "-PROPERTY_SPRINGPROFILES_PROPERTIES"
            });
            batchKonfiguration.getAsBoolean("NotSet");
        });
    }

    @Test
    public void BatchKonfigurationgGetAsStringStandard()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals("testBatchLauncher", batchKonfiguration.getAsString("BatchName", "Standard"));
    }

    @Test
    public void BatchKonfigurationgGetAsStringStandardKeyNotAvailable()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals("Standard", batchKonfiguration.getAsString("NotSet", "Standard"));
    }

    @Test
    public void BatchKonfigurationgGetAsLongStandardValueNotSet()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assertions.assertEquals(1000, batchKonfiguration.getAsLong("LongProperty", 1000));
    }

    @Test
    public void BatchKonfigurationgGtAsLongKeyNotLong()
    {
        assertThrows(BatchrahmenKonfigurationException.class, () -> {
            batchKonfiguration = new BatchKonfiguration(new String[]{
                    "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                    "-PROPERTY_SPRINGPROFILES_PROPERTIES"
            });
            batchKonfiguration.getAsLong("BatchName");
        });
    }

    @Test
    public void ladePropertyDateiInvalidFile() {
        assertThrows(BatchrahmenKonfigurationException.class, () -> {
            batchKonfiguration = new BatchKonfiguration(new String[]{
                    "-start", "-cfg", "resourceNotAvailable", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                    "-PROPERTY_SPRINGPROFILES_PROPERTIES"
            });
        });
    }

    @Test
    public void getStartTypStart() {
        assertThrows(BatchrahmenParameterException.class, () -> {
            batchKonfiguration = new BatchKonfiguration(new String[]{
                    "-start", "-cfg", "/resources/batch/test-batch-launcher-start-stop.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                    "-PROPERTY_SPRINGPROFILES_PROPERTIES"
            });
        });
    }
}

