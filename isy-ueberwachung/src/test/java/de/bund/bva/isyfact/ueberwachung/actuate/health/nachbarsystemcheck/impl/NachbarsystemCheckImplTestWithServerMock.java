package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import reactor.netty.http.client.HttpClient;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { NachbarsystemCheckImplTestWithServerMock.TestConfiguration.class },
        properties = {
                "isy.logging.anwendung.name=NachbarsystemCheckImplTest",
                "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
                "isy.logging.anwendung.typ=Test",
                "isy.task.autostart=false"
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class NachbarsystemCheckImplTestWithServerMock {

    private final IsyLogger LOGGER =
            IsyLoggerFactory.getLogger(NachbarsystemCheckImplTestWithServerMock.class);

    private static MockWebServer mockBackEnd;

    @Autowired
    private WebClient webClient;

    @Autowired
    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    private NachbarsystemCheck nachbarsystemCheck;


    @BeforeClass

    public static void startServer() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(1234);
    }

    @Before
    public void setUp() {
        nachbarsystemCheck = Mockito.spy(new NachbarsystemCheckImpl(
                webClient, nachbarsystemConfigurationProperties));
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
    public void timeoutWirdGetriggertMitRetries() {
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


    @Configuration
    public static class TestConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public WebClient webClient(
                NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
            long timeoutInMillis =
                    nachbarsystemConfigurationProperties.getNachbarsystemCheck().getTimeout().toMillis();
            if (timeoutInMillis > (long) Integer.MAX_VALUE) { //damit beim Cast keine Probleme auftreten
                //fachlich gesehen sollte der Wert ohnehin deutlich kleiner sein
                throw new IllegalArgumentException("NachbarsystemConfigurationProperties: Timeout zu lang");
            }

            // create reactor netty HTTP client
            HttpClient httpClient = HttpClient.create()
                    .tcpConfiguration(tcpClient -> {
                        tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutInMillis);
                        tcpClient = tcpClient.doOnConnected(conn -> conn
                                .addHandlerLast(new ReadTimeoutHandler(timeoutInMillis, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeoutInMillis, TimeUnit.MILLISECONDS)));
                        return tcpClient;
                    });
            // create a client http connector using above http client
            ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
            return WebClient.builder().clientConnector(clientHttpConnector).build();
        }

        @Bean
        public NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties() {
            return new NachbarsystemConfigurationProperties();
        }

    }

}
