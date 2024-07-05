package de.bund.bva.isyfact.task;

import org.junit.jupiter.api.extension.RegisterExtension;

import de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderMock;

/**
 * Superclass that can be extended to automatically start an embedded OIDC provider in a JUnit test.
 * Clients and users can be added and removed by calling methods on the {@link #embeddedOidcProvider}.
 */
public abstract class AbstractOidcProviderTest {

    protected static final String ISSUER_PATH = "/auth/realms/testrealm";

    private static final String HOST = "localhost";

    private static final int PORT = 9093;

    /**
     * Authentication and authorization via EmbeddedOidcProviderMock based on WireMock.
     */
    @RegisterExtension
    public static final EmbeddedOidcProviderMock embeddedOidcProvider = new EmbeddedOidcProviderMock(HOST, PORT, ISSUER_PATH);


    public String getIssuerLocation() {
        return String.format("http://%s:%d%s", HOST,PORT,ISSUER_PATH);
    }
}
