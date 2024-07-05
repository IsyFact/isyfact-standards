package de.bund.bva.isyfact.security.authentication;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.IsySecurityTestConfiguration;
import de.bund.bva.isyfact.security.example.config.SecurityConfig;
import de.bund.bva.isyfact.security.example.rest.ExampleRestController;

@ActiveProfiles("resource-server")
@SpringBootTest(classes = {
        IsySecurityTestConfiguration.class, SecurityConfig.class, ExampleRestController.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class ResourceServerJwtTest extends AbstractOidcProviderTest {

    @LocalServerPort
    private int port;

    private String pingUri;

    @BeforeEach
    public void setup() {
        embeddedOidcProvider.removeAllClients();
        pingUri = "http://localhost:" + port + "/ping";
    }

    @Test
    public void shouldFetchJwksFromOAuth2Server() {
        // manually get a token signed by the OIDC provider
        String token = embeddedOidcProvider.getAccessTokenString("cc-client", "testuser", Optional.empty(),
                Collections.singleton("Rolle_A"));

        // no endpoints on the OIDC provider should be queried until a request with bearer token was received
        verify(0, getRequestedFor(urlMatching(ISSUER_PATH + ".*")));

        String body = WebClient.create().get().uri(pingUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertEquals("true", body);

        // should query the provider config based on the issuer configured in "s.s.o.r.jwt.issuer-uri"
        verify(1, getRequestedFor(urlEqualTo(ISSUER_PATH + "/.well-known/openid-configuration")));
        // provider config contains the JWKS endpoint which contains the certs used to verify the token
        verify(1, getRequestedFor(urlEqualTo(ISSUER_PATH + "/protocol/openid-connect/certs")));
    }

}
