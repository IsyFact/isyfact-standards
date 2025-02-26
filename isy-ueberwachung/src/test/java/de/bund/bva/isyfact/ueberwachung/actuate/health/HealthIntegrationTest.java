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
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthEndpointWebExtension;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.config.ActuatorSecurityConfigurationProperties;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = {IsyHealthAutoConfiguration.class, HealthIntegrationTest.TestConfig.class},
    properties = {
        "isy.logging.anwendung.name=HealthIntegrationTest",
        "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=Integrationstest",
        "isy.ueberwachung.security.username=test",
        "isy.ueberwachung.security.password=test"
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

    //  Not applicable with the default caching behaviour as the initialized Status us up.
    // If this UNKNOWN is still the desired behaciour a custom Component that initializes to UNKNOWN can be implemented.
//    @Test
//    void test1_initialerStatusUnknown() {
//        var healthResponse = actuatorCall("/health").expectStatus().isOk()
//            .expectBody(NachbarsystemHealth.class)
//            .returnResult().getResponseBody();
//        assertThat(healthResponse).isNotNull();
//        assertThat(healthResponse.getStatus()).isEqualTo(Status.UNKNOWN);
//        assertThat(healthResponse.getDetails()).isEmpty();
//    }

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

        // creates a new indicator under /actuator/health/testComponent/testInstance
        @Bean(testComponentName)
        public HealthContributor testComponent() {
            Map<String, HealthIndicator> healthIndicatorMap = new HashMap<>();
            healthIndicatorMap.put(testInstanceName, () -> Health.up().build());
            return CompositeHealthContributor.fromMap(healthIndicatorMap);
        }
    }
}
