package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.*;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemRestTemplateConfigurer;

@WireMockTest(httpPort = 1234)
class NachbarsystemCheckImplWithServerMockTest {

    public static final String ACTUATOR_PATH = "/actuate/health";

    private final IsyLogger LOGGER =
        IsyLoggerFactory.getLogger(NachbarsystemCheckImplWithServerMockTest.class);

    private NachbarsystemCheck nachbarsystemCheck;

    @BeforeEach
    public void setUp() {
        RestTemplate restTemplate = NachbarsystemRestTemplateConfigurer
            .configureForNachbarSystemCheck(new RestTemplateBuilder(),
                new NachbarsystemConfigurationProperties())
            .build();
        nachbarsystemCheck = Mockito.spy(new NachbarsystemCheckImpl(restTemplate));
    }


    //korrekter Durchlauf
    @Test
    void checkNachbarUp() {

        stubFor(get(ACTUATOR_PATH).willReturn(ok().withBody("{\"status\":\"UP\"}").withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
    }

    //korrekter Durchlauf
    @Test
    void checkNachbarOutOfService() {
        stubFor(get(ACTUATOR_PATH).willReturn(serviceUnavailable().withBody("{\"status\":\"" + Status.OUT_OF_SERVICE + "\"}").withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
    }

    //Wenn bei der Anfrage eine Exception auftritt
    // z.B. weil der Server "BAD REQUEST" zurückgibt (400)
    // gilt der Nachbar als nicht erreichbar
    // und es wird als Status für den Nachbarn "DOWN" zurückgegeben
    @Test
    void responsecode400EmptyBody() {
        stubFor(get(ACTUATOR_PATH).willReturn(badRequest()));

        Nachbarsystem nachbarsystem = createNachbarsystemDummy();
        nachbarsystem.setEssentiell(true);
        LOGGER.debug("Error-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void exceptionBeiAnfrageNichtEssentiell() {
        stubFor(get(ACTUATOR_PATH).willReturn(badRequest()));
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        LOGGER.debug("Warn-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void nichtParsbareInfos() {

        stubFor(get("/*").willReturn(ok().withBody("abcd")));

        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        LOGGER.debug("Warn-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void timeoutWirdGetriggert() {
        //Server gibt keine Response

        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        LOGGER.debug("Warn-Log erwartet: "); //da nicht essentielles System down
        //Logs werden nicht über Mocks überprüft, da dies allgemein als eher instabil angesehen wird
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    private Nachbarsystem createNachbarsystemDummy() {
        Nachbarsystem nachbarsystem = new Nachbarsystem();
        nachbarsystem.setSystemname("system1");
        nachbarsystem.setEssentiell(false);
        nachbarsystem
            .setHealthEndpoint(URI.create("http://localhost:1234/actuate/health"));
        return nachbarsystem;
    }

}
