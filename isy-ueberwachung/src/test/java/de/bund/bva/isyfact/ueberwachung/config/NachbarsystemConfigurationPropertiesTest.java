package de.bund.bva.isyfact.ueberwachung.config;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyLoadbalancerAutoConfiguration;

/**
 * Test to verify that set properties are loaded correctly.
 */
@SpringBootTest(classes = { IsyLoadbalancerAutoConfiguration.class, IsyHealthAutoConfiguration.class },
    properties = {
        "isy.ueberwachung.nachbarsysteme.nachbar1.systemname=Nachbar",
        "isy.ueberwachung.nachbarsysteme.nachbar1.essentiell=true",
        "isy.ueberwachung.nachbarsysteme.nachbar1.healthendpoint=http://example.com",
        "isy.ueberwachung.nachbarsysteme.nachbar2.systemname=Nachbar2",
        "isy.ueberwachung.nachbarsysteme.nachbar2.essentiell=false",
        "isy.ueberwachung.nachbarsysteme.nachbar2.healthendpoint=http://example.com",
        "isy.ueberwachung.nachbarsystemcheck.timeout=20s",
        "isy.ueberwachung.nachbarsystemcheck.anzahlretries=2" },
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class NachbarsystemConfigurationPropertiesTest {

    /**
     * NachbarsystemConfigurationProperties.
     */
    @Autowired
    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    //test to verify that properties are loaded correctly in nachbarsystemConfigurationProperties
    @Test
    public void nachbarsystemConfigurationPropertiesTest() {
        Nachbarsystem nachbar1 = nachbarsystemConfigurationProperties.getNachbarsysteme().get("nachbar1");
        assertNotNull(nachbar1);
        assertEquals("Nachbar", nachbar1.getSystemname());
        assertTrue(nachbar1.isEssentiell());
        assertEquals("http://example.com", nachbar1.getHealthEndpoint().toString());


        Nachbarsystem nachbar2 = nachbarsystemConfigurationProperties.getNachbarsysteme().get("nachbar2");
        assertNotNull(nachbar2);
        assertEquals("Nachbar2", nachbar2.getSystemname());
        assertFalse(nachbar2.isEssentiell());
        assertEquals("http://example.com", nachbar2.getHealthEndpoint().toString());
    }

    @Test
    public void nachbarsystemcheckConfigurationPropertiesTest() {
        NachbarsystemConfigurationProperties.NachbarsystemCheckProperties nachbarsystemCheckProperties =
            nachbarsystemConfigurationProperties.getNachbarsystemCheck();
        assertNotNull(nachbarsystemCheckProperties);
        assertEquals(Duration.ofSeconds(20), nachbarsystemCheckProperties.getTimeout());
    }

}
