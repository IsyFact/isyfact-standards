package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "management.endpoint.health.cache.time-to-live=5s",
    "management.endpoints.enabled-by-default=false",
    "management.endpoint.health.show-details=never",
    "management.endpoint.health.enabled=true",
    "management.endpoint.metrics.enabled=true",
    "management.endpoint.info.enabled=true",
    "management.endpoints.jmx.exposure.exclude=*",
    "management.endpoints.web.exposure.include=health,metrics,info"
})
class HealthEndpointIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testHealthCachingWithinTTL() {
        // First call to /actuator/health
        var firstResponse = webClient.get().uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .returnResult();

        assertThat(firstResponse).isNotNull();

        // Second call within TTL period
        var secondResponse = webClient.get().uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .returnResult();

        assertThat(secondResponse).isNotNull();

        // Ensure responses are the same
        assertThat(secondResponse).isEqualTo(firstResponse);
    }

    @Test
    void testHealthRefreshAfterTTL() throws InterruptedException {
        // First call to /actuator/health
        var firstResponse = webClient.get().uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .returnResult();

        assertThat(firstResponse).isNotNull();

        // Wait for TTL to expire
        Thread.sleep(2000);


        // Second call after TTL
        var refreshedResponse = webClient.get().uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .returnResult();

        assertThat(refreshedResponse).isNotNull();

        // Ensure the response after TTL is different (cache refreshed)
        assertThat(refreshedResponse).isNotEqualTo(firstResponse);
    }
}
