package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;

@SpringBootTest(
    classes = IsyHealthAutoConfiguration.class,
    properties = {
        "isy.logging.anwendung.name=HealthIntegrationTest",
        "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=Integrationstest",
        "isy.ueberwachung.security.username=test",
        "isy.ueberwachung.security.password=123"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class HealthIntegrationSecurityTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int localPort;

    @Test
    void testMetricsRequiresAuthorization() {
        Assertions.assertThatThrownBy(() -> getClientResponse("metrics"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasStackTraceContaining("HTTP Status 401 – Unauthorized");
    }

    @Test
    void testMetricsAvailableWithAuthorization() {
        ResponseEntity<NachbarsystemHealth> responseEntity = getClientResponseWithAuth("metrics");
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testInfoRequiresAuthorization() {
        Assertions.assertThatThrownBy(() -> getClientResponse("info"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasStackTraceContaining("HTTP Status 401 – Unauthorized");
    }

    @Test
    void testInfoAvailableWithAuthorization() {
        ResponseEntity<NachbarsystemHealth> responseEntity = getClientResponseWithAuth("info");
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testHealthRequiresAuthorization() {
        ResponseEntity<NachbarsystemHealth> responseEntity = getClientResponse("health");
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<NachbarsystemHealth> getClientResponse(String endpoint, String... path) {
        String p = "";
        if (path != null) {
            p = "/" + String.join("/", path);
        }
        String uri = "http://localhost:" + localPort + "/actuator/" + endpoint + p;

        return restTemplate.getForEntity(uri, NachbarsystemHealth.class);
    }

    private ResponseEntity<NachbarsystemHealth> getClientResponseWithAuth(String endpoint, String... path) {
        String p = "";
        if (path != null) {
            p = "/" + String.join("/", path);
        }
        String uri = "http://localhost:" + localPort + "/actuator/" + endpoint + p;

        return new RestTemplateBuilder()
            .basicAuthentication("test", "123")
            .build()
            .getForEntity(uri, NachbarsystemHealth.class);
    }
}
