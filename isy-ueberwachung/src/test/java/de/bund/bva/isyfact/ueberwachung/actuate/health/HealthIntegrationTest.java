package de.bund.bva.isyfact.ueberwachung.actuate.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthEndpointWebExtension;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

/**
 * Class for verifying whether the health endpoint functions correctly with the caching registry.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IsyHealthAutoConfiguration.class, HealthIntegrationTest.TestConfiguration.class},
    properties = {
        "isy.logging.anwendung.name=HealthIntegrationTest",
        "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=Integrationstest",
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class
})
public class HealthIntegrationTest {

    static final int DELAY_MS = 5000;

    private static final String testComponentName = "testComponent"; // must correspond to the name of the HealthIndicator-Bean
    private static final String testInstanceName = "testInstance";

    @LocalServerPort
    private int localPort;

    private final RestTemplate restTemplate = new RestTemplate();

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
     * If the property "management.endpoint.health.show-details" is set to "never" getting the health of a nested
     * component always return 404, even if the HealthContributor exists.
     *
     * @see HealthEndpointWebExtension#health(ApiVersion, SecurityContext, String...)
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
    public void test5_infoEnabled() {
        ResponseEntity<NachbarsystemHealth> responseEntity = getClientResponse("info");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
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

        // creates a new indicator under /actuator/health/testComponent/testInstance
        @Bean
        public HealthContributor testComponent() {
            Map<String, HealthIndicator> healthIndicatorMap = new HashMap<>();
            healthIndicatorMap.put("testInstance", () -> Health.up().build());
            return CompositeHealthContributor.fromMap(healthIndicatorMap);
        }

    }

}
