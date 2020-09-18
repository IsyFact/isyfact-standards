package de.bund.bva.isyfact.ueberwachung.actuate.health;

import static de.bund.bva.isyfact.ueberwachung.actuate.health.HealthEndpointIntegrationTest.DELAY_MS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.HealthWebEndpointResponseMapper;
import org.springframework.boot.actuate.health.ShowDetails;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

/**
 * Test zum Überprüfen, ob der {@link IsyHealthEndpoint} benutzt wird und die Konfiguration greift.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { IsyHealthAutoConfiguration.class, HealthEndpointIntegrationTest.TestConfiguration.class },
        properties = {
                "isy.logging.anwendung.name=HealthEndpointIntegrationTest",
                "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
                "isy.logging.anwendung.typ=Integrationstest",
                "isy.task.autostart=true",
                "isy.task.default.host=.*",
                "isy.task.tasks.isyHealthTask.initial-delay=" + DELAY_MS
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class HealthEndpointIntegrationTest {

    static final int DELAY_MS = 3000;

    private static final String testComponentName = "testComponent"; // muss Name der HealthIndicator-Bean entsprechen
    private static final String testInstanceName = "testInstance";

    @LocalServerPort
    private int localPort;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private HealthWebEndpointResponseMapper healthWebEndpointResponseMapper;

    @BeforeClass
    public static void initClass() {
        new MessageSourceHolder().setMessageSource(new ResourceBundleMessageSource());
    }

    @Test
    public void test1_initialerStatusUnknown() {
        ResponseEntity<NachbarsystemHealth> healthResponse = getHealthResponse();

        assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
        assertEquals(Status.UNKNOWN, healthResponse.getBody().getStatus());
        assertTrue(healthResponse.getBody().getDetails().isEmpty());
    }

    @Test
    public void test2_gecachtOhneDetails() throws InterruptedException {
        Thread.sleep(DELAY_MS + 500);

        ResponseEntity<NachbarsystemHealth> healthResponse = getHealthResponse();

        assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
        assertEquals(Status.UP, healthResponse.getBody().getStatus());
        assertTrue(healthResponse.getBody().getDetails().isEmpty());
    }

    /**
     * Wenn "management.endpoint.health.show-details=never" gesetzt ist, liefern die Endpoints für component und
     * componentInstance immer ein 404 zurück, auch wenn der HealthIndicator existiert.
     *
     * @see org.springframework.boot.actuate.health.HealthWebEndpointResponseMapper#mapDetails(Supplier, SecurityContext)
     */
    @Test
    public void test3_andereEndpointsLiefern404() {
        try {
            getHealthResponse(testComponentName);
            fail("Abfrage sollte exception werfen wegen 404-Statuscode");
        } catch(HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        } catch(Exception e){
            fail("unerwartete Exception: "+ e.getMessage());
        }

        try {
            getHealthResponse(testComponentName, testInstanceName);
            fail("Abfrage sollte exception werfen wegen 404-Statuscode");
        } catch(HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        } catch(Exception e){
            fail("unerwartete Exception: "+ e.getMessage());
        }
    }

    @Test
    public void test4_metricsEnabled() {
        ResponseEntity<NachbarsystemHealth> responseEntity = getClientResponse("metrics");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void test5_infoDisabled() {

        try {
            getClientResponse("info");
            fail("info ist disabled und Abfrage sollte Exception werfen");
        } catch(HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        } catch(Exception e){
            fail("unerwartete Exception: "+ e.getMessage());
        }
    }

    @Test
    public void test6_andereEndpointsSindDaWennShowDetailsGleichAlways() {
        ReflectionTestUtils.setField(healthWebEndpointResponseMapper, "showDetails", ShowDetails.ALWAYS);

        // die eigene Health zeigt jetzt Details an
        ResponseEntity<NachbarsystemHealth> healthResponse = getHealthResponse();
        assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
        assertEquals(Status.UP, healthResponse.getBody().getStatus());
        assertFalse(healthResponse.getBody().getDetails().isEmpty());

        // die anderen Endpoint sind da und der CompositeIndicator zeigt Details an
        ResponseEntity<NachbarsystemHealth> componentResponse = getHealthResponse(testComponentName);
        assertEquals(HttpStatus.OK, componentResponse.getStatusCode());
        assertEquals(Status.UP, componentResponse.getBody().getStatus());
        assertFalse(componentResponse.getBody().getDetails().isEmpty());

        ResponseEntity<NachbarsystemHealth> componentInstanceResponse = getHealthResponse(testComponentName, testInstanceName);
        assertEquals(HttpStatus.OK, componentInstanceResponse.getStatusCode());
        assertEquals(Status.UP, componentInstanceResponse.getBody().getStatus());
        assertTrue(componentInstanceResponse.getBody().getDetails().isEmpty());
    }

    private ResponseEntity<NachbarsystemHealth> getHealthResponse(String... path) {
        return getClientResponse("health", path);
    }

    private ResponseEntity<NachbarsystemHealth> getClientResponse(String endpoint, String... path) {
        String p = "";
        if (path != null) {
            p = "/" + String.join("/", path);
        }
        String uri = "http://localhost:" + localPort + "/actuator/" + endpoint + p;

        return restTemplate.getForEntity(uri, NachbarsystemHealth.class);
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        public HealthIndicator testComponent(HealthAggregator healthAggregator) {
            Map<String, HealthIndicator> healthIndicatorMap = new HashMap<>();
            healthIndicatorMap.put("testInstance", () -> Health.up().build());
            return new CompositeHealthIndicator(healthAggregator, healthIndicatorMap);
        }

    }

}
