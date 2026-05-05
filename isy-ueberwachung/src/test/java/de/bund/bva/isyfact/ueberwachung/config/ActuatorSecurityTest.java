package de.bund.bva.isyfact.ueberwachung.config;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

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
                "isy.ueberwachung.security.jwk-set-uri=https://<keycloak-host>:11776/auth/realms/<realm>/protocol/openid-connect/certs"
        }
)
@EnableAutoConfiguration
@AutoConfigureMockMvc
class ActuatorSecurityTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * Health should be available without authentication.
     */
    @Test
    void health() {
        webClient.get().uri("/actuator/health")
                .exchange().expectStatus().isOk();
    }

    /**
     * Actuators should not return status with basic Auth
     */
    @Test
    void actuatorBasicAuth() {
        webClient.get().uri("/actuator")
                .headers(headers -> headers.setBasicAuth("dummy", "dummy"))
                .exchange().expectStatus().isUnauthorized();
    }

    /**
     * Actuators should not return status without authentication
     */
    @Test
    void unauthorized() {
        webClient.get().uri("/actuator")
                .exchange().expectStatus().isUnauthorized();
    }

    /**
     * Actuators should not return status with wrong token
     */
    @Test
    void actuatorInvalidToken() {
        webClient.get().uri("/actuator/metrics")
                .headers(headers -> headers.setBearerAuth("test")
                )
                .exchange().expectStatus().isUnauthorized();
    }

    /**
     * Actuators should return status when valid token is in request.
     *
     * @throws Exception when endpoint not reachable
     */
    @Test
    void actuatorValidToken() throws Exception {
        mockMvc.perform(get("/actuator")
                        .with(jwt().jwt(jwt -> {
                            jwt.claim("sub", "user123");
                            jwt.claim("scope", "read");
                        })))
                .andExpect(status().isOk());
    }

    /**
     * Actuators should return status when valid token is in request.
     *
     * @throws Exception when endpoint not reachable
     */
    @Test
    void actuatorInvalidTokenMock() throws Exception {
        mockMvc.perform(get("/actuator").header("Authorization", "Bearer totallyInvalidToken"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Actuators metrics should return values
     *
     * @throws Exception when endpoint not reachable
     */
    @Test
    void actuatorMetrics() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(jwt().jwt(jwt -> {
                            jwt.claim("sub", "user123");
                            jwt.claim("scope", "read");
                        })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray())
                .andExpect(jsonPath("$.names").value(org.hamcrest.Matchers.hasItem("jvm.memory.used")));
    }
}
