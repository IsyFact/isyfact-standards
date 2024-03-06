package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemRestTemplateConfigurer;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

public class NachbarsystemCheckImplWithServerMockTest {

    private final IsyLogger LOGGER =
            IsyLoggerFactory.getLogger(NachbarsystemCheckImplWithServerMockTest.class);

    private static MockWebServer mockBackEnd;

    private NachbarsystemCheck nachbarsystemCheck;


    @BeforeClass
    public static void startServer() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(1234);
    }

    @Before
    public void setUp() {
        RestTemplate restTemplate = NachbarsystemRestTemplateConfigurer
            .configureForNachbarSystemCheck(new RestTemplateBuilder(),
                new NachbarsystemConfigurationProperties() )
            .build();
        nachbarsystemCheck = Mockito.spy(new NachbarsystemCheckImpl(restTemplate));
    }

    @AfterClass
    public static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    //korrekter Durchlauf
    @Test
    public void checkNachbarUp() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"status\":\"UP\"}")
        );

        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
    }

    //korrekter Durchlauf
    @Test
    public void checkNachbarOutOfService() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"status\":\"" + Status.OUT_OF_SERVICE + "\"}")
        );

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
    public void responsecode400EmptyBody() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.BAD_REQUEST.value())
        );

        Nachbarsystem nachbarsystem = createNachbarsystemDummy();
        nachbarsystem.setEssentiell(true);
        LOGGER.debug("Error-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    public void exceptionBeiAnfrageNichtEssentiell() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(400)
        );
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        LOGGER.debug("Warn-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    public void nichtParsbareInfos() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody("abcd")
        );
        Nachbarsystem nachbarsystem = createNachbarsystemDummy();

        LOGGER.debug("Warn-Log erwartet: ");
        NachbarsystemHealth health = nachbarsystemCheck.checkNachbarsystem(nachbarsystem);

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    public void timeoutWirdGetriggert() {
        //Server gibt keine Response
        mockBackEnd.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

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
                .setHealthEndpoint(URI.create("http://localhost:" + mockBackEnd.getPort() + "/actuate/health"));
        return nachbarsystem;
    }

}
