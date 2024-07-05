package de.bund.bva.isyfact.batchrahmen.batch.konfiguration;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenKonfigurationException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncherTest;

public class BatchKonfigurationTest {
    private BatchKonfiguration batchKonfiguration;
    private Properties properties;
    private static String ERGEBNIS_DATEI;
    @Before
    public void setUp() {
        try {
            ERGEBNIS_DATEI = new File(
                    BatchLauncherTest.class.getResource("/resources/batch/ausgabe/ergebnisdatei.xml").toURI())
                    .getAbsolutePath();
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        properties = Mockito.mock(Properties.class);
    }

    @Test(expected = BatchrahmenParameterException.class)
    public void BatchKonfigurationCfgEmpty() {
        batchKonfiguration = new BatchKonfiguration(new String[] {"-start", "-cfg", "", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI});
    }

    @Test()
    public void BatchKonfiguration() {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals(1, batchKonfiguration.getBatchRahmenSpringKonfigFiles().size());
        Assert.assertEquals("de.bund.bva.isyfact.batchrahmen.BatchrahmenTestConfig123", batchKonfiguration.getBatchRahmenSpringKonfigFiles().get(0));
        Assert.assertEquals(1, batchKonfiguration.getAnwendungSpringKonfigFiles().size());
        Assert.assertEquals("de.bund.bva.isyfact.batchrahmen.AnwendungTestConfig123", batchKonfiguration.getAnwendungSpringKonfigFiles().get(0));
    }

    @Test()
    public  void BatchKonfigurationGetSpringProfiles()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals(1, batchKonfiguration.getSpringProfiles().length);
    }

    @Test()
    public  void BatchKonfigurationGetSpringProfilesNull()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals(0, batchKonfiguration.getSpringProfiles().length);
    }

    @Test()
    public void BatchKonfigurationgGetAsLong()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals(1000, batchKonfiguration.getAsLong("LongProperty"));
    }

    @Test(expected = BatchrahmenKonfigurationException.class)
    public void BatchKonfigurationgGetAsLongKeyNotAvailable()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        batchKonfiguration.getAsLong("LongProperty");
        batchKonfiguration.getAsLong("BatchName");
    }

    @Test()
    public void BatchKonfigurationgGetAsString()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals("testBatchLauncher", batchKonfiguration.getAsString("BatchName"));
    }

    @Test(expected = BatchrahmenKonfigurationException.class)
    public void BatchKonfigurationgGetAsStringException()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        batchKonfiguration.getAsString("NotSet");
    }

    @Test(expected = BatchrahmenKonfigurationException.class)
    public void BatchKonfigurationgGetAsBooleanException()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        batchKonfiguration.getAsBoolean("NotSet");
    }

    @Test()
    public void BatchKonfigurationgGetAsStringStandard()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals("testBatchLauncher", batchKonfiguration.getAsString("BatchName", "Standard"));
    }

    @Test()
    public void BatchKonfigurationgGetAsStringStandardKeyNotAvailable()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals("Standard", batchKonfiguration.getAsString("NotSet", "Standard"));
    }

    @Test()
    public void BatchKonfigurationgGetAsLongStandardValueNotSet()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        Assert.assertEquals(1000, batchKonfiguration.getAsLong("LongProperty", 1000));
    }

    @Test(expected = BatchrahmenKonfigurationException.class)
    public void BatchKonfigurationgGtAsLongKeyNotLong()
    {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher-spring-profile.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
        batchKonfiguration.getAsLong("BatchName");
    }

    @Test(expected = BatchrahmenKonfigurationException.class)
    public void ladePropertyDateiInvalidFile() {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "resourceNotAvailable", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
    }

    @Test(expected = BatchrahmenParameterException.class)
    public void getStartTypStart() {
        batchKonfiguration = new BatchKonfiguration(new String[] {
                "-start", "-cfg", "/resources/batch/test-batch-launcher-start-stop.properties", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI,
                "-PROPERTY_SPRINGPROFILES_PROPERTIES"
        });
    }
}

