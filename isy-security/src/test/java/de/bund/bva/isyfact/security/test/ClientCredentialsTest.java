package de.bund.bva.isyfact.security.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.security.example.IsySpringBootApplication;

@Disabled("currently only runs as an integration test")
@SpringBootTest(classes = IsySpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientCredentialsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebClient webClient;

    @Test
    public void shouldAllowPingFromClientWithRole() {
        String body = webClient.get().uri("http://localhost:" + port + "/ping").exchangeToMono(response -> {
            assertEquals(HttpStatus.OK, response.statusCode());
            return response.bodyToMono(String.class);
        }).block();

        assertEquals("true", body);
    }

}
