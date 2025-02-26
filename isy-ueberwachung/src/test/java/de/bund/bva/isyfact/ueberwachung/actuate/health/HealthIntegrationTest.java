package de.bund.bva.isyfact.ueberwachung.actuate.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.DefaultHealthContributorRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthEndpointWebExtension;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.bund.bva.isyfact.ueberwachung.TestApplicationDummy;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.config.ActuatorSecurityConfigurationProperties;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = { TestApplicationDummy.class, IsyHealthAutoConfiguration.class, HealthIntegrationTest.TestConfig.class},
    properties = {
        "isy.logging.anwendung.name=HealthIntegrationTest",
        "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=Integrationstest",
        "isy.ueberwachung.security.username=test",
        "isy.ueberwachung.security.password=test",
        "management.endpoint.health.cache.time-to-live=1s",
        "management.endpoints.web.exposure.include=health,info,metrics"
    }
)
class HealthIntegrationTest {

    static final int DELAY_MS = 5000;

    // must correspond to the name of the HealthIndicator-Bean
    private static final String testComponentName = "testComponent";
    private static final String testInstanceName = "testInstance";

    @Autowired
    WebTestClient webClient;

    @Autowired
    private WebEndpointProperties webEndpointProperties;

    @Autowired
    private ActuatorSecurityConfigurationProperties securityProperties;

    @Autowired
    private HealthContributorRegistry healthContributorRegistry;

    @Test
    void test1_initialerStatusUnknown() {
        var healthResponse = actuatorCall("/health").expectStatus().isOk()
            .expectBody(NachbarsystemHealth.class)
            .returnResult().getResponseBody();
        assertThat(healthResponse).isNotNull();
        assertThat(healthResponse.getStatus()).isEqualTo(Status.UNKNOWN);
        assertThat(healthResponse.getDetails()).isEmpty();
    }

    @Test
    void test2_gecachtOhneDetails() throws InterruptedException {
        Thread.sleep(DELAY_MS + 500);

        var healthResponse = actuatorCall("/health").expectStatus().isOk()
            .expectBody(NachbarsystemHealth.class)
            .returnResult().getResponseBody();
        assertThat(healthResponse).isNotNull();
        assertThat(healthResponse.getStatus()).isEqualTo(Status.UP);
        assertThat(healthResponse.getDetails()).isEmpty();
    }

    /**
     * If the property "management.endpoint.health.show-details" is set to "never" getting the health of a nested
     * component always return 404, even if the HealthContributor exists.
     *
     * @see HealthEndpointWebExtension#health(ApiVersion, SecurityContext, String...)
     */
    @Test
    void test3_andereEndpointsLiefern404() {
        actuatorCall("/health/" + testComponentName).expectStatus().isNotFound();
        actuatorCall("/health/" + testComponentName + "/" + testInstanceName).expectStatus().isNotFound();
    }

    @Test
    void test4_metricsEnabled() {
        actuatorCall("/metrics").expectStatus().isOk();
    }

    @Test
    void test5_infoEnabled() {
        actuatorCall("/info").expectStatus().isOk();
    }

    private WebTestClient.ResponseSpec actuatorCall(String endpoint) {
        return webClient
            .get().uri(webEndpointProperties.getBasePath() + endpoint)
            .headers(headers -> headers.setBasicAuth(
                securityProperties.getUsername(),
                securityProperties.getPassword())
            )
            .exchange();
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {



        @Bean
        public HealthContributorRegistry healthContributorRegistry() {
            return new DefaultHealthContributorRegistry();
        }

        @Bean
        @Primary
        public WebEndpointProperties webEndpointProperties() {
            return new WebEndpointProperties();
        }

        @Bean
        @Primary
        public ActuatorSecurityConfigurationProperties securityProperties() {
            return new ActuatorSecurityConfigurationProperties();
        }

        // creates a new indicator under /actuator/health/testComponent/testInstance
        @Bean(testComponentName)
        public HealthContributor testComponent() {
            Map<String, HealthIndicator> healthIndicatorMap = new HashMap<>();
            healthIndicatorMap.put(testInstanceName, () -> Health.up().build());
            return CompositeHealthContributor.fromMap(healthIndicatorMap);
        }
    }

    // Added as, the tests doesn’t directly check if the cache refreshes after the TTL is up?!
    // test2_gecachtOhneDetails kinda shows caching works, but it doesn’t test what happens if the health status changes between calls.

    @Test
    void test6_cacheRefreshAfterTtl() throws InterruptedException {
        // First call populates the cache
        var initialResponse = actuatorCall("/health").expectStatus().isOk()
            .expectBody(NachbarsystemHealth.class)
            .returnResult().getResponseBody();
        assertThat(initialResponse).isNotNull();
        assertThat(initialResponse.getStatus()).isEqualTo(Status.UP);

        // Simulate a delay for the TTL to expire
        Thread.sleep(DELAY_MS + 1000);

        // Second call should refresh the health status
        var refreshedResponse = actuatorCall("/health").expectStatus().isOk()
            .expectBody(NachbarsystemHealth.class)
            .returnResult().getResponseBody();
        assertThat(refreshedResponse).isNotNull();
        assertThat(refreshedResponse.getStatus()).isEqualTo(Status.UP); // Assuming no failure simulation
        assertThat(refreshedResponse).isNotSameAs(initialResponse);
    }

    // No test simulates a component failing (e.g., returning DOWN) to confirm that a refreshed
    // health check actually picks up the new status.

    @Test
    void test7_cacheReflectsFailureAfterTtl() throws InterruptedException {
        // Simulate failure by replacing testComponent health
        replaceHealthContributorWithFailure();

        // Wait for TTL to expire
        Thread.sleep(DELAY_MS + 1000);

        // Call after TTL expiry should reflect the failure
        var failureResponse = actuatorCall("/health").expectStatus().isOk()
            .expectBody(NachbarsystemHealth.class)
            .returnResult().getResponseBody();
        assertThat(failureResponse).isNotNull();
        assertThat(failureResponse.getStatus()).isEqualTo(Status.DOWN);
    }
    private void replaceHealthContributorWithFailure() {
        // Replace the test component's health indicator with one that returns DOWN
        Map<String, HealthIndicator> failingIndicators = new HashMap<>();
        failingIndicators.put(testInstanceName, () -> Health.down().withDetail("reason", "Simulated failure").build());
        healthContributorRegistry.registerContributor(testComponentName, CompositeHealthContributor.fromMap(failingIndicators));
    }
}
