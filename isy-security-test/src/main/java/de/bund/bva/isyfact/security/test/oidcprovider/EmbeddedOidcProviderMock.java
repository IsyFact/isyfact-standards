package de.bund.bva.isyfact.security.test.oidcprovider;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

/**
 * This class mocks an OIDC provider for tests environments in which no dedicated OIDC provider can be used.
 * WireMock is used to simulate the REST services.
 * It implements JUnit 5 callbacks, so it can be registered as a JUnit extension to automatically start WireMock.
 */
public class EmbeddedOidcProviderMock extends OidcProviderMockBase implements AfterAllCallback, BeforeAllCallback {

    private WireMockServer oidcServerStub;

    public EmbeddedOidcProviderMock(String host, int port, String issuerPath) {
        super(host, port, issuerPath);
    }

    public EmbeddedOidcProviderMock(String host, int port, String issuerPath, int tokenLifespan) {
        super(host, port, issuerPath, tokenLifespan);
    }

    public EmbeddedOidcProviderMock(String host, int port, String issuerPath, String publicKey, String privateKey) {
        super(host, port, issuerPath, publicKey, privateKey);
    }

    public EmbeddedOidcProviderMock(String host, int port, String issuerPath, String publicKey, String privateKey, int tokenLifespan) {
        super(host, port, issuerPath, publicKey, privateKey, tokenLifespan);
    }

    public WireMockServer getOidcServer() {
        return oidcServerStub;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        if (oidcServerStub == null) {
            oidcServerStub = new WireMockServer(getPort());
            WireMock.configureFor(getPort());
        }
        if (!oidcServerStub.isRunning()) {
            oidcServerStub.start();
        }
        init("localhost", getPort());
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        oidcServerStub.stop();
        oidcServerStub = null;
    }

}
