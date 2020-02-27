package de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.Nachbarsystem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import reactor.core.publisher.Mono;

public class NachbarsystemIndicatorTest {

    private NachbarsystemCheck nachbarsystemCheck;

    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    private NachbarsystemIndicator nachbarsystemIndicator;

    @Before
    public void setup() {
        //Mocks der Nachbarkomponenten
        nachbarsystemCheck = Mockito.mock(NachbarsystemCheck.class);
        nachbarsystemConfigurationProperties = Mockito.mock(NachbarsystemConfigurationProperties.class);
        //Zu testende Klasse
        nachbarsystemIndicator =
            new NachbarsystemIndicator(nachbarsystemCheck, nachbarsystemConfigurationProperties);
    }

    // wenn keine Nachbarsysteme konfiguriert sind, gibt der Check immer "UP" zurück
    @Test
    public void keineNachbarsystemeKonfiguriert() {
        Mockito.when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(new HashMap<>());
        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: UP
        Assert.assertEquals(Status.UP, health.getStatus());
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

        Mockito.when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(nachbarn);

        NachbarsystemHealth mockHealth = new NachbarsystemHealth();
        mockHealth.setNachbarsystem(nachbar);
        mockHealth.setStatus(Status.DOWN);
        //Mock: Check liefert Down zurück
        Mockito.when(nachbarsystemCheck.checkNachbarsystem(Mockito.any()))
            .thenReturn(Mono.just(mockHealth));

        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: UP
        Assert.assertEquals(Status.UP, health.getStatus());
    }

    // Wenn ein System "essentiell" ist und der Check nicht erfolgreich ist,
    // liefert der Indikator trotzdem "OUT_OF_SERVICE" zurück
    @Test
    public void essentiellesSystemDown() {
        //Mock: Nachbar ist essentiell
        Map<String, Nachbarsystem> nachbarn = new HashMap<>();
        Nachbarsystem nachbar = new Nachbarsystem();
        nachbar.setSystemname("system");
        nachbar.setEssentiell(true);
        nachbar.setHealthEndpoint(URI.create("http://example.com"));
        nachbarn.put("nachbar1", nachbar);

        Mockito.when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(nachbarn);

        NachbarsystemHealth mockHealth = new NachbarsystemHealth();
        mockHealth.setNachbarsystem(nachbar);
        mockHealth.setStatus(Status.DOWN);
        //Mock: Check liefert Down zurück
        Mockito.when(nachbarsystemCheck.checkNachbarsystem(Mockito.any()))
            .thenReturn(Mono.just(mockHealth));

        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: OUT OF SERVICE
        Assert.assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
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

        Mockito.when(nachbarsystemConfigurationProperties.getNachbarsysteme()).thenReturn(nachbarn);

        NachbarsystemHealth mockHealth = new NachbarsystemHealth();
        mockHealth.setNachbarsystem(nachbar);
        mockHealth.setStatus(Status.UP);
        //Mock: Check liefert UP zurück
        Mockito.when(nachbarsystemCheck.checkNachbarsystem(Mockito.any()))
            .thenReturn(Mono.just(mockHealth));

        //Führe Check durch
        Health health = nachbarsystemIndicator.health();
        //Erwartetes ergebnis: UP
        Assert.assertEquals(Status.UP, health.getStatus());
    }

}