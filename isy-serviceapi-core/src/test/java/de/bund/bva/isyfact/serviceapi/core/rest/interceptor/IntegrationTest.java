package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mockserver.MockServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpStatus;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest;

import ch.qos.logback.classic.Level;

/**
 * In dieser Testklasse wird ein {@link MockServer} konfiguriert und instanziiert, an den ein Request gesendet
 * wird und welcher eine Response zurücksendet, um die IsyLoggingRest-Interceptoren gesamtheitlich zu testen.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegrationTest extends AbstractInterceptorTest {

    private static final int PORT = 1080;

    private static final String MOCK_SERVICE_URL = "http://localhost:" + String.valueOf(PORT) + "/";

    private static final String MOCK_URL_200 = "/okay";

    private static final String MOCK_URL_500 = "/internalServerError";

    private static final String ZERTIFIKAT = "--BEGIN CERTIFICATE----- MIIU=-----END CERTIFICATE--";

    private static final String AUTHORIZATION = "Basic dXNlcjpwYXNzd29yZA==";

    private static ClientAndServer mockServer;

    private static JAXRSClientFactoryBean clientFactory;

    @Mock
    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    @BeforeClass
    public static void startServer() {
        setLoggingLevel(Level.INFO);
        disableMockServerLogging();

        mockServer = ClientAndServer.startClientAndServer(PORT);

        prepareOkayResponse();
        prepareInternalServerErrorResponse();

        clientFactory = new JAXRSClientFactoryBean();
        clientFactory.setAddress(MOCK_SERVICE_URL);
    }

    @Override
    @Before
    public void setUp() {
        configureInterceptors(clientFactory);
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }

    @Test
    public void testSendeRequestAnPing() throws Exception {
        WebClient client = clientFactory.createWebClient();
        client.accept(MediaType.APPLICATION_XML);
        client.header("x-client-cert", ZERTIFIKAT);
        client.header("Authorization", AUTHORIZATION);
        client.header(KonstantenRest.KEY_SYSTEMNAME_REMOTE, "TestCaseServer-Mock");
        Response response = client.replacePath(MOCK_URL_200).post("");

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));

        verify(this.logHelper).logHeaderOut(any(), any(), any());
        verify(this.logHelper).logBodyOut(any(), any(), any());
        verify(this.logHelper).logBodyOutForce(any(), any(), any());
        verify(this.logHelper).logHeaderIn(any(), any(), any());
        verify(this.logHelper).logBodyIn(any(), any(), any());
        verify(this.logHelper).logBodyInForce(any(), any(), any());
        verify(this.logHelper).logDauerAufruf(any(), any(), any(Long.class), any());
    }

    private void configureInterceptors(JAXRSClientFactoryBean client) {
        IsyLoggingRestInAuthInterceptor inauth = new IsyLoggingRestInAuthInterceptor();
        IsyLoggingRestInInterceptor in = new IsyLoggingRestInInterceptor();
        IsyLoggingRestOutInterceptor out = new IsyLoggingRestOutInterceptor();

        inauth.setAufrufKontextVerwalter(this.aufrufKontextVerwalter);
        out.setSystemname("TestCaseClient");

        this.logHelper.setLoggeDaten(true);
        this.logHelper.setLoggeDatenBeiServerFehler(true);
        this.logHelper.setLoggeDauer(true);

        inauth.setLogHelperRest(this.logHelper);
        in.setLogHelperRest(this.logHelper);
        out.setLogHelperRest(this.logHelper);

        client.getInInterceptors().add(inauth);
        client.getInInterceptors().add(in);
        client.getOutInterceptors().add(out);
    }

    private static void prepareOkayResponse() {
        mockServer.when(new HttpRequest().withMethod("POST").withPath(MOCK_URL_200))
            .respond(new HttpResponse().withStatusCode(200)
                .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
                    new Header("Cache-Control", "public, max-age=86400"))
                .withBody("{ message: 'request was processed' }").withDelay(TimeUnit.SECONDS, 1));
    }

    private static void prepareInternalServerErrorResponse() {
        mockServer.when(new HttpRequest().withMethod("POST").withPath(MOCK_URL_500))
            .respond(new HttpResponse().withStatusCode(200)
                .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
                    new Header("Cache-Control", "public, max-age=86400"))
                .withBody("{ message: 'request was not successful due to an internal server error' }")
                .withDelay(TimeUnit.SECONDS, 1));
    }

    private static void disableMockServerLogging() {
        Collection<String> blockedLoggers = Arrays.asList( //
            "org.mockserver.mockserver.MockServer", //
            "org.mockserver.mockserver.MockServerHandler", //
            "org.mockserver.matchers.HttpRequestMatcher");
        blockedLoggers.stream()
            .map(logger -> (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(logger))
            .forEach(logger -> logger.setLevel(Level.WARN));
    }
}
