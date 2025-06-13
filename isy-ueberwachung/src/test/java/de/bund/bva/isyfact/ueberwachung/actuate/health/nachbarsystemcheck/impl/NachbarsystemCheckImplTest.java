package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;

public class NachbarsystemCheckImplTest {

    private final IsyLogger LOGGER = IsyLoggerFactory.getLogger(NachbarsystemCheckImplTest.class);

    //Mocken der Anfrage nach außen
    private final RestTemplate restTemplate = mock(RestTemplate.class);

    private NachbarsystemCheck nachbarsystemCheck;


    @Before
    public void setup() {
        nachbarsystemCheck = new NachbarsystemCheckImpl(restTemplate);
    }

    //korrekter Durchlauf mit Antwort "UP".
    @Test
    public void checkNachbarUp() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        NachbarsystemHealth nachbarsystemHealth = new NachbarsystemHealth();
        nachbarsystemHealth.setStatus(Status.UP);
        when(restTemplate.getForEntity(any(), eq(NachbarsystemHealth.class)))
            .thenReturn(new ResponseEntity<>(nachbarsystemHealth, HttpStatus.OK));

        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);
        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
    }

    // korrekter Durchlauf mit Antwort "OUT_OF_SERVICE", ungleich "UP".
    @Test
    public void checkNachbarDown() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        NachbarsystemHealth nachbarsystemHealth = new NachbarsystemHealth();
        nachbarsystemHealth.setStatus(Status.OUT_OF_SERVICE);
        when(restTemplate.getForEntity(any(), eq(NachbarsystemHealth.class)))
            .thenReturn(new ResponseEntity<>(nachbarsystemHealth, HttpStatus.OK));

        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);
        assertNotNull(health);
        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
    }

    //Wenn bei der Anfrage eine Exception auftritt, gilt der Nachbar als nicht erreichbar
    //und es wird als Status für den Nachbarn "DOWN" zurückgegeben
    @Test
    public void exceptionBeiAnfrage() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();
        when(restTemplate.getForEntity(any(), eq(NachbarsystemHealth.class)))
            .thenThrow(new RuntimeException("Fehler bei Abfrage simuliert"));

        LOGGER.debug("Error-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    public void exceptionBeiAnfrageNichtEssentiell() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();
        nachbarsystem.setEssentiell(false);
        when(restTemplate.getForEntity(any(), eq(NachbarsystemHealth.class)))
            .thenThrow(new RuntimeException("Fehler bei Abfrage simuliert"));

        LOGGER.debug("Warn-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    private Nachbarsystem createNachbarsystemDummy() {
        Nachbarsystem nachbarsystem = new Nachbarsystem();
        nachbarsystem.setSystemname("system1");
        nachbarsystem.setEssentiell(true);
        nachbarsystem.setHealthEndpoint(URI.create("http://example.com"));
        return nachbarsystem;
    }

}
