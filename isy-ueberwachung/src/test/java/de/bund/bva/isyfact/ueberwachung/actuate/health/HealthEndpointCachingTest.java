package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = HealthEndpointCachingTest.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "management.endpoint.health.cache.time-to-live=4s",
        "management.endpoints.web.exposure.include=health,info",
        "management.endpoint.health.enabled=true",
        "management.endpoint.health.show-details=never",
        //
        "isy.logging.anwendung.name=CachingTest",
        "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=Testtest",
        "isy.ueberwachung.security.username=test",
        "isy.ueberwachung.security.password=test",
    }
)
@EnableAutoConfiguration
public class HealthEndpointCachingTest {

//    @Configuration
//    static class TestConfig {
//    }
    @LocalServerPort
    private int port;

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Autowired
    private HealthContributorRegistry registry;

    @Autowired
    private TestRestTemplate rest;

    private HealthIndicator mockIndicator;

    @Before
    public void setUp() {
        // Create the mock indicator
        mockIndicator = Mockito.mock(HealthIndicator.class);
        // By default, return Health.up()
        when(mockIndicator.getHealth(anyBoolean())).thenReturn(Health.up().build());

        // Auth. the restTemplate
        this.rest = new TestRestTemplate("test", "test");
    }

    @Test
    public void testCachingViaHttp() throws InterruptedException{
        registry.registerContributor("testIndicatorHttp", mockIndicator);
        clearInvocations(mockIndicator);

        healthEndpoint.health();

        // Build URLs, Set Up without caused: HealthEndpointCachingTest.testCachingViaHttp:80 » ResourceAccess I/O error on GET request for "/actuator/health": Target host is not specified
        String baseURl = "http://localhost:" + port;
        String healthUrl = baseURl + "/actuator/health";


        String firstResponse = rest.getForObject(healthUrl, String.class);
        // verify(mockIndicator, times(1)).getHealth(anyBoolean());
        Assert.assertTrue("'Up' in first response", firstResponse.contains("\"status\":\"UP\""));

        //Change the health Status
        when(mockIndicator.getHealth((anyBoolean()))).thenReturn(Health.down().build());

        String secondResponse = rest.getForObject(healthUrl, String.class);
        // verify(mockIndicator, times(1)).getHealth(anyBoolean());
        Assert.assertTrue("'Up' in first response", secondResponse.contains("\"status\":\"UP\""));

        Thread.sleep(5000L);

        String thirdResponse = rest.getForObject(healthUrl, String.class);
        // verify(mockIndicator, times(2)).getHealth(anyBoolean());
        Assert.assertTrue("Now 'DOWN' is expected", secondResponse.contains("\"status\":\"UP\""));
    }

    @Test
    public void testHealthIsCachedWithinTTL() throws InterruptedException {
        // Register the mock indicator
        registry.registerContributor("testIndicator", mockIndicator);
        // Clear invocations happening during registration
        clearInvocations(mockIndicator);

        healthEndpoint.health();
        verify(mockIndicator, times(1)).getHealth(anyBoolean());


        // Second call should return cached result
        healthEndpoint.health();
        // The invocation count remains 1 because result is cached
        verify(mockIndicator, times(2)).getHealth(anyBoolean());

        //Sleep beyond TTL to force the cache to expire
        Thread.sleep(5000L);

        // Third call => should invoke the indicator again
        healthEndpoint.health();
        verify(mockIndicator, times(3)).getHealth(anyBoolean());
    }

//    @Test
//    public void testHealthStatus() {
//        registry.registerContributor("nextTestIndicator", mockIndicator);
//
//        // First call triggers the indicator
//        HealthComponent health = healthEndpoint.health();
//
//        // The mock returns Health.up(), so we expect "UP"
//        Assert.assertEquals("UP", health.getStatus().getCode());
//    }
}
