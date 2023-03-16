package de.bund.bva.isyfact.security.test.oidcprovider;

/**
 * This class mocks an OIDC provider for tests environments in which no dedicated OIDC provider can be used.
 */
public class RemoteOidcProviderMock extends OidcProviderMockBase {

    public RemoteOidcProviderMock(String host, int port, String issuerPath) {
        super(host, port, issuerPath);
        init(host, port);
    }

    public RemoteOidcProviderMock(String host, int port, String issuerPath, int tokenLifespan) {
        super(host, port, issuerPath, tokenLifespan);
        init(host, port);
    }

    public RemoteOidcProviderMock(String host, int port, String issuerPath, String publicKey, String privateKey) {
        super(host, port, issuerPath, publicKey, privateKey);
        init(host, port);
    }

    public RemoteOidcProviderMock(String host, int port, String issuerPath, String publicKey, String privateKey, int tokenLifespan) {
        super(host, port, issuerPath, publicKey, privateKey, tokenLifespan);
        init(host, port);
    }

}
