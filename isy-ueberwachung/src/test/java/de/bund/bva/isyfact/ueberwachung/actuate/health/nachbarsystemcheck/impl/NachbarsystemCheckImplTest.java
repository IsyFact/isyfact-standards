package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;

import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NachbarsystemCheckImplTest {

    private final IsyLogger LOGGER = IsyLoggerFactory.getLogger(NachbarsystemCheckImplTest.class);

    //Mocken der Anfrage nach außen
    private final WebClient webClient = mock(WebClient.class);
    private final WebClient.RequestHeadersUriSpec requestHeadersUriSpec =
            mock(WebClient.RequestHeadersUriSpec.class);
    private final WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
    private final ClientResponse clientResponse = mock(ClientResponse.class);

    private NachbarsystemCheck nachbarsystemCheck;

    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    @Before
    public void setup() {
        nachbarsystemConfigurationProperties = new NachbarsystemConfigurationProperties();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.exchange()).thenReturn(Mono.just(clientResponse));

        nachbarsystemCheck = new NachbarsystemCheckImpl(webClient, nachbarsystemConfigurationProperties);
    }

    //korrekter Durchlauf
    @Test
    public void checkNachbarUp() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        NachbarsystemHealth nachbarsystemHealth = new NachbarsystemHealth();
        nachbarsystemHealth.setStatus(Status.UP);
        when(clientResponse.bodyToMono(NachbarsystemHealth.class))
                .thenReturn(Mono.just(nachbarsystemHealth));

        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);
        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
    }

    //Wenn bei der Anfrage eine Exception auftritt, gilt der Nachbar als nicht erreichbar
    //und es wird als Status für den Nachbarn "DOWN" zurückgegeben
    @Test
    public void exceptionBeiAnfrage() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();
        when(clientResponse.bodyToMono(NachbarsystemHealth.class))
                .thenReturn(Mono.error(new Exception("someException")));

        LOGGER.debug("Error-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    public void exceptionBeiAnfrageNichtEssentiell() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();
        nachbarsystem.setEssentiell(false);
        when(clientResponse.bodyToMono(NachbarsystemHealth.class))
                .thenReturn(Mono.error(new Exception("someException")));

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
