package de.bund.bva.isyfact.security;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.extension.RegisterExtension;

import de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderMock;

/**
 * Superclass that can be extended to automatically start an embedded OIDC provider in a JUnit test.
 * Clients and users can be added and removed by calling methods on the {@link #embeddedOidcProvider}.
 */
public abstract class AbstractOidcProviderTest {

    protected static final String ISSUER_PATH = "/auth/realms/testrealm";

    private static final String HOST = "localhost";

    private static final int PORT = 9095;

    /**
     * Authentication and authorization via EmbeddedOidcProviderMock based on WireMock.
     */
    @RegisterExtension
    public static final EmbeddedOidcProviderMock embeddedOidcProvider = new EmbeddedOidcProviderMock(HOST, PORT, ISSUER_PATH, 300);

    protected static void registerTestClients() {
        // client with authorization-grant-type=password
        embeddedOidcProvider.addUser("resource-owner-password-credentials-test-client", "hypersecretpassword",
                "testuser", "pw1234", Optional.empty(), Collections.singleton("Rolle_A"));
        embeddedOidcProvider.addUser("resource-owner-password-credentials-test-client", "hypersecretpassword",
                "testuser-with-bhknz", "pw1234", Optional.of("123456"), Collections.singleton("Rolle_B"));
        // client with authorization-grant-type=client_credentials
        embeddedOidcProvider.addClient("client-credentials-test-client", "supersecretpassword", Collections.singleton("Rolle_A"));
    }

    protected static String getIssuer() {
        return embeddedOidcProvider.getIssuer().toString();
    }
}
