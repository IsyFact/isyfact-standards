package de.bund.bva.isyfact.ueberwachung.config;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyActuatorSecurityAutoConfiguration;

/**
 * Checks whether a response is received with authentication and whether it is rejected as 401 without authentication.
 */
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = {IsyActuatorSecurityAutoConfiguration.class},
    properties = {
        "isy.logging.anwendung.name=ActuatorSecurityTest",
        "isy.logging.anwendung.version=0.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=JUnit5Test",
        "isy.ueberwachung.security.username=test",
        "isy.ueberwachung.security.password=test1"
    }
)
@EnableAutoConfiguration
class ActuatorSecurityTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    private ActuatorSecurityConfigurationProperties securityProperties;

    @Test
    void actuator() {
        webClient.get().uri("/actuator")
            .headers(headers -> headers.setBasicAuth(
                securityProperties.getUsername(),
                securityProperties.getPassword())
            )
            .exchange().expectStatus().is2xxSuccessful();
    }

    @Test
    void unauthorized() {
        webClient.get().uri("/actuator")
            .exchange().expectStatus().isUnauthorized();
    }
}
