package de.bund.bva.isyfact.security;

import org.junit.jupiter.api.extension.RegisterExtension;

import de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderMock;

/**
 * Superclass that can be extended to automatically start an embedded OIDC provider in a JUnit test.
 * Clients and users can be added and removed by calling methods on the {@link #embeddedOidcProvider}.
 */
public abstract class AbstractOidcProviderTest {

    protected static final String ISSUER_PATH = "/auth/realms/testrealm";

    /**
     * Authentication and authorization via EmbeddedOidcProviderMock based on WireMock.
     */
    @RegisterExtension
    public static final EmbeddedOidcProviderMock embeddedOidcProvider = new EmbeddedOidcProviderMock("localhost", 9095, ISSUER_PATH);

}
