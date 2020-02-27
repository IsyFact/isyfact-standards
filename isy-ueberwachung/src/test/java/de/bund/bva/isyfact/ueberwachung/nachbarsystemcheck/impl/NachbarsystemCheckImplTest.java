package de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.impl;

import java.net.URI;

import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.Nachbarsystem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NachbarsystemCheckImplTest {

    //Mocken der Anfrage nach außen
    private final WebClient webClient = mock(WebClient.class);
    private final WebClient.RequestHeadersUriSpec requestHeadersUriSpec =
        mock(WebClient.RequestHeadersUriSpec.class);
    private final WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
    private final WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    private NachbarsystemCheck nachbarsystemCheck;

    @Before
    public void setup() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class)))
            .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        nachbarsystemCheck = new NachbarsystemCheckImpl(webClient);
    }

    //korrekter Durchlauf
    @Test
    public void checkNachbarUp() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        NachbarsystemHealth nachbarsystemHealth = new NachbarsystemHealth();
        nachbarsystemHealth.setStatus(Status.UP);
        when(responseSpec.bodyToMono(NachbarsystemHealth.class))
            .thenReturn(Mono.just(nachbarsystemHealth));

        Mono<NachbarsystemHealth> healthMono = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);
        NachbarsystemHealth health = healthMono.block();
        Assert.assertNotNull(health);
        Assert.assertEquals(Status.UP, health.getStatus());
    }

    //Wenn bei der Anfrage eine Exception auftritt, gilt der Nachbar als nicht erreichbar
    //und es wird als Status für den Nachbarn "DOWN" zurückgegeben
    @Test
    public void exceptionBeiAnfrage() {
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();
        when(responseSpec.bodyToMono(NachbarsystemHealth.class))
            .thenReturn(Mono.error(new Exception("someException")));

        Mono<NachbarsystemHealth> healthMono = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);
        NachbarsystemHealth health = healthMono.block();
        Assert.assertNotNull(health);
        Assert.assertEquals(Status.DOWN, health.getStatus());
    }


    private Nachbarsystem createNachbarsystemDummy() {
        Nachbarsystem nachbarsystem = new Nachbarsystem();
        nachbarsystem.setSystemname("system1");
        nachbarsystem.setEssentiell(true);
        nachbarsystem.setHealthEndpoint(URI.create("http://example.com"));
        return nachbarsystem;
    }
}