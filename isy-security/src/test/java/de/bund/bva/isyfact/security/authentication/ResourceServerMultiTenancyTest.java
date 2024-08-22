package de.bund.bva.isyfact.security.authentication;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.security.IsySecurityTestConfiguration;
import de.bund.bva.isyfact.security.example.config.SecurityConfig;
import de.bund.bva.isyfact.security.example.rest.ExampleRestController;
import de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderMock;

@ActiveProfiles("multi-tenancy")
@SpringBootTest(classes = {
        IsySecurityTestConfiguration.class, SecurityConfig.class, ExampleRestController.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class ResourceServerMultiTenancyTest {

    protected static final String ISSUER_PATH1 = "/auth/realms/testrealm1";
    protected static final String ISSUER_PATH2 = "/auth/realms/testrealm2";
    protected static final String ISSUER_PATH3 = "/auth/realms/testrealm3";

    private static final String HOST = "localhost";

    private static final int PORT1 = 9095;
    private static final int PORT2 = 9096;
    private static final int PORT3 = 9097;

    /**
     * Authentication and authorization via EmbeddedOidcProviderMock based on WireMock.
     * A ping using a token from embeddedOidcProvider1 and embeddedOidcProvider2 is possible (tested in shouldAllowPingFromTestrealm1() and shouldAllowPingFromTestrealm2()),
     *     as both tenants are defined in application-multi-tenancy.yaml.
     * A ping with a token from embeddedOidcProvider3 fails as unauthorized (tested in shouldDenyPingFromTestrealm3())
     *     since no matching tenant is defined in application-multi-tenancy.yaml.
     */
    @RegisterExtension
    public static final EmbeddedOidcProviderMock embeddedOidcProvider1 = new EmbeddedOidcProviderMock(HOST, PORT1, ISSUER_PATH1);
    @RegisterExtension
    public static final EmbeddedOidcProviderMock embeddedOidcProvider2 = new EmbeddedOidcProviderMock(HOST, PORT2, ISSUER_PATH2);
    @RegisterExtension
    public static final EmbeddedOidcProviderMock embeddedOidcProvider3 = new EmbeddedOidcProviderMock(HOST, PORT3, ISSUER_PATH3);
    @LocalServerPort
    private int port;

    private String pingUri;

    @BeforeEach
    public void setup() {
        embeddedOidcProvider1.removeAllClients();
        embeddedOidcProvider2.removeAllClients();
        embeddedOidcProvider3.removeAllClients();
        pingUri = "http://localhost:" + port + "/ping";
    }

    /**
     * Test with an issuer that is configured as a tenant in the resource server configuration.
     */
    @Test
    public void shouldAllowPingFromTestrealm1() {
        // manually get a token signed by the OIDC provider
        String token = embeddedOidcProvider1.getAccessTokenString("cc-client", "testuser", Optional.empty(),
                Collections.singleton("Rolle_A"));

        String body = WebClient.create().get().uri(pingUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertEquals("true", body);

    }

    /**
     * Test with an issuer that is configured as a tenant in the resource server configuration.
     */
    @Test
    public void shouldAllowPingFromTestrealm2() {
        // manually get a token signed by the OIDC provider
        String token = embeddedOidcProvider2.getAccessTokenString("cc-client", "testuser", Optional.empty(),
                Collections.singleton("Rolle_A"));

        String body = WebClient.create().get().uri(pingUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertEquals("true", body);
        verify(0, getRequestedFor(urlMatching(ISSUER_PATH2 + ".*")));
    }

    @Test
    public void shouldDenyPingFromTestrealm3() {
        // manually get a token signed by the OIDC provider
        String token = embeddedOidcProvider3.getAccessTokenString("cc-client", "testuser", Optional.empty(),
                Collections.singleton("Rolle_A"));

        WebClient.create().get().uri(pingUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchangeToMono(response -> {
                    // status 401 Unauthorized expected as the tenant was not set
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();
    }

}
