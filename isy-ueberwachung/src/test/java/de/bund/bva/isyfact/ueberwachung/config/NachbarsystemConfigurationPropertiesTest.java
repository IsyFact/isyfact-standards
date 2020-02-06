package de.bund.bva.isyfact.ueberwachung.config;

import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyUeberwachungAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.Nachbarsystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/* Test zum überprüfen, ob die gesetzten properties korrekt geladen werden.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { IsyUeberwachungAutoConfiguration.class },
    properties = {
        "isy.ueberwachung.nachbarsysteme.nachbar1.systemname=Nachbar",
        "isy.ueberwachung.nachbarsysteme.nachbar1.essentiell=true",
        "isy.ueberwachung.nachbarsysteme.nachbar1.healthendpoint=http://example.com",
        "isy.ueberwachung.nachbarsysteme.nachbar2.systemname=Nachbar2",
        "isy.ueberwachung.nachbarsysteme.nachbar2.essentiell=false",
        "isy.ueberwachung.nachbarsysteme.nachbar2.healthendpoint=http://example.com" })
public class NachbarsystemConfigurationPropertiesTest {

    /** NachbarsystemConfigurationProperties. */
    @Autowired
    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    //testet, ob die Properties korrekt in die
    //nachbarsystemConfigurationProperties geladen werden
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
}