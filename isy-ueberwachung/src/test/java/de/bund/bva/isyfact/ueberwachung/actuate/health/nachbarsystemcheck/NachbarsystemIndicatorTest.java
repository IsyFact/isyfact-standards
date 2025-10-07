package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;

public class NachbarsystemIndicatorTest {

    private NachbarsystemCheck nachbarsystemCheck;

    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    private NachbarsystemIndicator nachbarsystemIndicator;

    @BeforeEach
    public void setup() {
        //Mocks of neighbor systems
        nachbarsystemCheck = mock(NachbarsystemCheck.class);
        nachbarsystemConfigurationProperties = mock(NachbarsystemConfigurationProperties.class);
        //class to be tested
        nachbarsystemIndicator =
                new NachbarsystemIndicator(nachbarsystemCheck, nachbarsystemConfigurationProperties);
    }

    // if no neighbor systems are configured, the check always return 'UP'
    @Test
    public void keineNachbarsystemeKonfiguriert() {
        when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(new HashMap<>());
        //perform Check
        Health health = nachbarsystemIndicator.health();
        //Expected result: UP
        assertEquals(Status.UP, health.getStatus());
    }

    // if a system is not essential and the check is not successfully, the indicator returns 'UP' nevertheless
    @Test
    public void nichtEssentiellesSystemDown() {
        //Mock: neighbor is not essentiell
        Map<String, Nachbarsystem> nachbarn = new HashMap<>();
        Nachbarsystem nachbar = new Nachbarsystem();
        nachbar.setSystemname("system");
        nachbar.setEssentiell(false);
        nachbar.setHealthEndpoint(URI.create("http://example.com"));
        nachbarn.put("nachbar1", nachbar);

        when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(nachbarn);

        NachbarsystemHealth mockHealth = new NachbarsystemHealth();
        mockHealth.setNachbarsystem(nachbar);
        mockHealth.setStatus(Status.DOWN);
        //Mock: Check responds Down
        when(nachbarsystemCheck.checkNachbarsystem(any()))
                .thenReturn(mockHealth);

        //perform Check
        Health health = nachbarsystemIndicator.health();
        //Expected result: UP
        assertEquals(Status.UP, health.getStatus());
    }

    // if a system is essential and the check is not successfully, the indicator returns 'DOWN'
    @Test
    public void essentiellesSystemDown() {
        //Mock: neighbor is essentiell
        Map<String, Nachbarsystem> nachbarn = new HashMap<>();
        Nachbarsystem nachbar = new Nachbarsystem();
        nachbar.setSystemname("system");
        nachbar.setEssentiell(true);
        nachbar.setHealthEndpoint(URI.create("http://example.com"));
        nachbarn.put("nachbar1", nachbar);

        when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(nachbarn);

        NachbarsystemHealth mockHealth = new NachbarsystemHealth();
        mockHealth.setNachbarsystem(nachbar);
        mockHealth.setStatus(Status.DOWN);
        //Mock: Check responds Down
        when(nachbarsystemCheck.checkNachbarsystem(any()))
                .thenReturn(mockHealth);

        //perform Check
        Health health = nachbarsystemIndicator.health();
        //Expected result: OUT OF SERVICE
        assertEquals(Status.DOWN, health.getStatus());
    }

    // if all system are checked successfully (Health is 'UP'), the indicator returns 'UP'
    @Test
    public void alleSystemeUp() {
        //Mock: multiple neighbors, essentially +not essentially
        Map<String, Nachbarsystem> nachbarn = new HashMap<>();
        Nachbarsystem nachbar = new Nachbarsystem();
        nachbar.setSystemname("system");
        nachbar.setEssentiell(false);
        nachbar.setHealthEndpoint(URI.create("http://example.com"));
        nachbarn.put("nachbar1", nachbar);
        Nachbarsystem nachbar2 = new Nachbarsystem();
        nachbar2.setSystemname("system2");
        nachbar2.setEssentiell(true);
        nachbar2.setHealthEndpoint(URI.create("http://example.com"));
        nachbarn.put("nachbar2", nachbar2);

        when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(nachbarn);

        NachbarsystemHealth mockHealth = new NachbarsystemHealth();
        mockHealth.setNachbarsystem(nachbar);
        mockHealth.setStatus(Status.UP);
        //Mock: Check responds UP
        when(nachbarsystemCheck.checkNachbarsystem(any()))
                .thenReturn(mockHealth);

        //perform Check
        Health health = nachbarsystemIndicator.health();
        //Expected resul: UP
        assertEquals(Status.UP, health.getStatus());
    }

}
