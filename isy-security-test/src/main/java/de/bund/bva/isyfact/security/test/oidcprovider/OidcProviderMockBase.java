package de.bund.bva.isyfact.security.test.oidcprovider;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.PASSWORD;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.GRANT_TYPE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.NegativeRegexPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

/**
 * This class mock an OIDC provider for tests environments in which no dedicated OIDC provider can be used.
 * This requires WireMock as a standalone service.
 */
public abstract class OidcProviderMockBase extends EmbeddedOidcProviderStub {

    /** Name of the HTTP header that contains the BHKNZ and second OU. */
    static final String BHKNZ_HEADER_NAME = "x-client-cert-bhknz";

    static final String OIDC_CONFIG_ENDPOINT = "/.well-known/openid-configuration";

    static final String AUTHORIZATION_ENDPOINT = "/protocol/openid-connect/auth";

    static final String JWKS_ENDPOINT = "/protocol/openid-connect/certs";

    static final String TOKEN_ENDPOINT = "/protocol/openid-connect/token";

    private static final String DEFAULT_SECOND_OU = "TESTOU";

    private String secondOu = DEFAULT_SECOND_OU;

    private final Map<String, Set<StubMapping>> userMappings = new HashMap<>();

    private final Map<String, Set<StubMapping>> clientMappings = new HashMap<>();

    public OidcProviderMockBase(String host, int port, String issuerPath) {
        super(host, port, issuerPath);
    }

    public OidcProviderMockBase(String host, int port, String issuerPath, int tokenLifespan) {
        super(host, port, issuerPath, tokenLifespan);
    }

    public OidcProviderMockBase(String host, int port, String issuerPath, String publicKey, String privateKey) {
        super(host, port, issuerPath, publicKey, privateKey);
    }

    public OidcProviderMockBase(String host, int port, String issuerPath, String publicKey, String privateKey, int tokenLifespan) {
        super(host, port, issuerPath, publicKey, privateKey, tokenLifespan);
    }

    public String getSecondOu() {
        return secondOu;
    }

    /**
     * Sets the second OU value which is checked on login. The default is {@link #DEFAULT_SECOND_OU}. The value
     * <em>must</em> be set before the first call to {@link #addUser(String, String, String, String, Optional, Set)}!
     *
     * @param secondOu
     *         second OU to check during login
     */
    public void setSecondOu(String secondOu) {
        this.secondOu = secondOu;
    }

    protected void init(String host, int port) {
        WireMock.configureFor(host, port);
        WireMock.reset();
        setupOIDCConfigEndpoint();
        setupDefaultTokenEndpoints();
        setupJwksEndpoint();
    }

    public void addUser(String clientId, String secret, String username, String password, Optional<String> bhknz, Set<String> roles) {
        final String accessTokenResponse = getAccessTokenResponse(clientId, username, bhknz, roles);
        userMappings.put(username, generateUserMapping(clientId, secret, username, password, bhknz, accessTokenResponse));
    }

    public void addClient(String clientId, String secret, Set<String> roles) {
        final String accessTokenResponse = getAccessTokenResponse(clientId, "service-account-" + clientId, Optional.empty(), roles);
        clientMappings.put(clientId, generateClientMapping(clientId, secret, accessTokenResponse));
    }

    public void removeUser(String username) {
        final Set<StubMapping> userMapping = userMappings.remove(username);
        if (userMapping != null) {
            for (StubMapping mapping : userMapping) {
                WireMock.removeStub(mapping);
            }
        }
    }

    public void removeAllUsers() {
        for (String user : userMappings.keySet()) {
            removeUser(user);
        }
    }

    public void removeClient(String clientId) {
        final Set<StubMapping> clientMapping = clientMappings.remove(clientId);
        if (clientMapping != null) {
            for (StubMapping mapping : clientMapping) {
                WireMock.removeStub(mapping);
            }
        }
    }

    public void removeAllClients() {
        for (String client : clientMappings.keySet()) {
            removeClient(client);
        }
    }

    /**
     * The OpenID configuration endpoint is called to get the location of the other endpoints (like the JWKS, token and issuer endpoint).
     *
     * @return stub mapping for the OpenID configuration endpoint
     */
    private StubMapping setupOIDCConfigEndpoint() {
        return stubFor(get(urlEqualTo(appendToIssuerPath(OIDC_CONFIG_ENDPOINT)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(getOIDCConfigResponse(JWKS_ENDPOINT, AUTHORIZATION_ENDPOINT, TOKEN_ENDPOINT))
                ));
    }

    private StubMapping setupJwksEndpoint() {
        return stubFor(get(urlEqualTo(appendToIssuerPath(JWKS_ENDPOINT)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(getJwksResponse())
                ));
    }

    /**
     * Set up error responses when no stub for the token endpoint (user or client) were found or the request contains invalid data.
     */
    private Set<StubMapping> setupDefaultTokenEndpoints() {
        Set<StubMapping> stubMappings = new HashSet<>();

        String tokenEndpoint = appendToIssuerPath(TOKEN_ENDPOINT);

        String clientCredentialsGrantType = String.format("%s=%s", GRANT_TYPE, CLIENT_CREDENTIALS.getValue());

        stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(2)
                .withRequestBody(new NegativeRegexPattern(String.format(".*%s=.*", GRANT_TYPE)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createErrorResponse("invalid_request", "Missing grant type"))
                )));

        stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(3)
                .withRequestBody(new EqualToPattern(clientCredentialsGrantType))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createErrorResponse("unsupported_grant_type", "Invalid client-id or secret"))
                )));

        stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(4)
                .withRequestBody(new NegativeRegexPattern(".*username=.+"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())
                        .withHeader(HttpHeaders.WWW_AUTHENTICATE, "dummy")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createErrorResponse("invalid_grant", "Missing username"))
                )));

        stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(4)
                .withRequestBody(new NegativeRegexPattern(".*password=.+"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())
                        .withHeader(HttpHeaders.WWW_AUTHENTICATE, "dummy")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createErrorResponse("invalid_grant", "Missing password"))
                )));

        return stubMappings;
    }

    private Set<StubMapping> generateUserMapping(String clientId, String secret, String username, String password, Optional<String> bhknz,
                                                 String accessTokenResponse) {
        Set<StubMapping> stubMappings = new HashSet<>();

        String tokenEndpoint = appendToIssuerPath(TOKEN_ENDPOINT);
        String validLogin = String.format("%s=%s&username=%s&password=%s", GRANT_TYPE, PASSWORD.getValue(), username, password);

        if (bhknz.isPresent()) {
            String bhknzPattern = String.format("%1$s:%2$s|%2$s:%1$s", bhknz.get(), secondOu);

            // Require the bhknz header if the user has a bhknz
            stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(1)
                    .withHeader(BHKNZ_HEADER_NAME, new RegexPattern(bhknzPattern))
                    .withRequestBody(new ContainsPattern(validLogin)).withBasicAuth(clientId, secret)
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .withBody(accessTokenResponse)
                    )));
        } else {
            // Only allow request without the bhknz header if the user does not have a bhknz.
            // This makes sure that the token will never contain the bhknz if the header isn't present,
            // even if a dedicated OIDC provider might return it anyway.
            // Otherwise it is impossible to distinguish request with or without the bhknz header.
            stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(1)
                    .withRequestBody(new ContainsPattern(validLogin)).withBasicAuth(clientId, secret)
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .withBody(accessTokenResponse)
                    )));
        }

        if (bhknz.isPresent()) {
            stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(5)
                    .withRequestBody(new ContainsPattern(validLogin))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.UNAUTHORIZED.value())
                            .withHeader(HttpHeaders.WWW_AUTHENTICATE, "dummy")
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .withBody(createErrorResponse("invalid_grant", "Invalid bhknz"))
                    )));
        }

        stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(5)
                .withRequestBody(new NegativeRegexPattern(String.format(".*username=%s.*", username)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())
                        .withHeader(HttpHeaders.WWW_AUTHENTICATE, "dummy")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createErrorResponse("invalid_grant", "Invalid username"))
                )));

        stubMappings.add(stubFor(post(urlEqualTo(tokenEndpoint)).atPriority(5)
                .withRequestBody(new NegativeRegexPattern(String.format(".*password=%s.*", password)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())
                        .withHeader(HttpHeaders.WWW_AUTHENTICATE, "dummy")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createErrorResponse("invalid_grant", "Invalid password"))
                )));

        return stubMappings;
    }

    private Set<StubMapping> generateClientMapping(String clientId, String secret, String accessTokenResponse) {
        Set<StubMapping> stubMappings = new HashSet<>();

        String clientCredentialsGrantType = String.format("%s=%s", GRANT_TYPE, CLIENT_CREDENTIALS.getValue());

        stubMappings.add(stubFor(
                post(urlEqualTo(appendToIssuerPath(TOKEN_ENDPOINT)))
                        .atPriority(1)
                        .withRequestBody(new EqualToPattern(clientCredentialsGrantType)).withBasicAuth(clientId, secret)
                        .willReturn(aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.OK.value())
                                .withBody(accessTokenResponse)
                        )));

        return stubMappings;
    }

    private String createErrorResponse(String errorCode, String errorDescription) {
        return "{\n" +
                "  \"error\": \"" + errorCode + "\"," +
                "  \"error_description\": \"" + errorDescription + "\"" +
                "}";
    }

    private String appendToIssuerPath(String endpoint) {
        return appendPath(getIssuer(), endpoint).getPath();
    }

}
