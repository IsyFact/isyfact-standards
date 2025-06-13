package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;

public class NachbarsystemIndicatorTest {

    private NachbarsystemCheck nachbarsystemCheck;

    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    private NachbarsystemIndicator nachbarsystemIndicator;

    @Before
    public void setup() {
        //Mocks der Nachbarkomponenten
        nachbarsystemCheck = mock(NachbarsystemCheck.class);
        nachbarsystemConfigurationProperties = mock(NachbarsystemConfigurationProperties.class);
        //Zu testende Klasse
        nachbarsystemIndicator =
                new NachbarsystemIndicator(nachbarsystemCheck, nachbarsystemConfigurationProperties);
    }

    // wenn keine Nachbarsysteme konfiguriert sind, gibt der Check immer "UP" zurück
    @Test
    public void keineNachbarsystemeKonfiguriert() {
        when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(new HashMap<>());
        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: UP
        assertEquals(Status.UP, health.getStatus());
    }

    // Wenn ein System "nicht essentiell" ist und der Check nicht erfolgreich ist,
    // liefert der Indikator trotzdem "UP" zurück
    @Test
    public void nichtEssentiellesSystemDown() {
        //Mock: Nachbar ist nicht essentiell
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
        //Mock: Check liefert Down zurück
        when(nachbarsystemCheck.checkNachbarsystem(any()))
                .thenReturn(mockHealth);

        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: UP
        assertEquals(Status.UP, health.getStatus());
    }

    // Wenn ein System "essentiell" ist und der Check nicht erfolgreich ist,
    // liefert der Indikator "DOWN" zurück
    @Test
    public void essentiellesSystemDown() {
        //Mock: Nachbar ist essentiell
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
        //Mock: Check liefert Down zurück
        when(nachbarsystemCheck.checkNachbarsystem(any()))
                .thenReturn(mockHealth);

        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: OUT OF SERVICE
        assertEquals(Status.DOWN, health.getStatus());
    }

    // Wenn alle Systeme erfolgreich überprüft werden (Health ist "UP"),
    // liefert der Indicator "UP" zurück
    @Test
    public void alleSystemeUp() {
        //Mock: Mehrere Nachbarn essentiell+nicht essentiell
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
        //Mock: Check liefert UP zurück
        when(nachbarsystemCheck.checkNachbarsystem(any()))
                .thenReturn(mockHealth);

        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: UP
        assertEquals(Status.UP, health.getStatus());
    }

}
