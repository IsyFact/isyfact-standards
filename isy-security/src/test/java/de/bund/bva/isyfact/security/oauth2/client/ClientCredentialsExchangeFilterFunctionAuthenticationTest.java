package de.bund.bva.isyfact.security.oauth2.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.IsySecurityTestConfiguration;
import de.bund.bva.isyfact.security.example.config.OAuth2WebClientConfiguration;
import de.bund.bva.isyfact.security.example.config.SecurityConfig;
import de.bund.bva.isyfact.security.example.rest.ExampleRestController;

import reactor.core.publisher.Mono;

@ActiveProfiles(profiles = "test-clients")
@SpringBootTest(classes = {
        IsySecurityTestConfiguration.class, SecurityConfig.class, ExampleRestController.class, OAuth2WebClientConfiguration.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// clear context so WebClient will fetch a fresh access token
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClientCredentialsExchangeFilterFunctionAuthenticationTest extends AbstractOidcProviderTest {

    @LocalServerPort
    private int port;

    /** WebClient configured with the ServletOAuth2AuthorizedClientExchangeFilterFunction. */
    @Autowired
    @Qualifier("cc-client")
    private WebClient webClient;

    private String pingUri;

    @BeforeEach
    public void setup() {
        embeddedOidcProvider.removeAllClients();
        pingUri = "http://localhost:" + port + "/ping";

        // make sure that the security context is empty
        // the ServletOAuth2AuthorizedClientExchangeFilterFunction would fail if an Authentication with a null principal exists
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowPingFromClientWithRole() {
        embeddedOidcProvider.addClient("client-credentials-test-client", "supersecretpassword", Collections.singleton("Rolle_A"));

        String body = webClient.get().uri(pingUri).exchangeToMono(response -> {
            assertEquals(HttpStatus.OK, response.statusCode());
            return response.bodyToMono(String.class);
        }).block();

        assertEquals("true", body);
    }

    @Test
    void shouldDenyPingFromClientWithoutRole() {
        embeddedOidcProvider.addClient("client-credentials-test-client", "supersecretpassword", Collections.emptySet());

        HttpStatusCode statusCode = webClient.get().uri(pingUri)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

        assertEquals(HttpStatus.FORBIDDEN, statusCode);
    }

    @Test
    void shouldDenyPingWithoutClient() {
        // create new WebClient without the ServletOAuth2AuthorizedClientExchangeFilterFunction
        HttpStatusCode statusCode = WebClient.create().get().uri(pingUri)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

        assertEquals(HttpStatus.UNAUTHORIZED, statusCode);
    }

}
