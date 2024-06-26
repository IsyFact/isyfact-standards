package de.bund.bva.isyfact.security.test.oidcprovider;

import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import de.bund.bva.isyfact.security.test.RsaKeyGenerator;

/**
 * This class simulates the base functions of an OpenID Connect (OIDC) provider for tests in environments
 * in which no dedicated OIDC provider can be used.
 */
public class EmbeddedOidcProviderStub {

    /** Default name of the claim to store the user roles in. */
    public static final String DEFAULT_ROLES_CLAIM_NAME = "roles";

    /** Name of the claim to store the BHKNZ in. */
    public static final String BHKNZ_CLAIM_NAME = "bhknz";

    /** The host on which the OIDC provider stub runs. */
    private final String host;

    /** The port used for the OIDC provider stub. */
    private final int port;

    /** The issuer for the access tokens. */
    private final URI issuer;

    /** The key ID for the access tokens. */
    private final String keyId;

    /** The public key for the access tokens. */
    private final RSAPublicKey publicKey;

    /** The private key for the access tokens. */
    private final PrivateKey privateKey;

    /** The timestamp at which the token expires. */
    private final int tokenLifespan;

    /** Claim name to store roles in the access token. */
    private String rolesClaimName = DEFAULT_ROLES_CLAIM_NAME;

    public EmbeddedOidcProviderStub(String host, int port, String issuerPath) {
        this(host, port, issuerPath, new RsaKeyGenerator().getKeyPair());
    }

    public EmbeddedOidcProviderStub(String host, int port, String issuerPath, int tokenLifespan) {
        this(host, port, issuerPath, new RsaKeyGenerator().getKeyPair(), tokenLifespan);
    }

    public EmbeddedOidcProviderStub(String host, int port, String issuerPath, String publicKey, String privateKey) {
        this(host, port, issuerPath, RsaKeyGenerator.decodeKeyPair(publicKey, privateKey));
    }

    public EmbeddedOidcProviderStub(String host, int port, String issuerPath, String publicKey, String privateKey, int tokenLifespan) {
        this(host, port, issuerPath, RsaKeyGenerator.decodeKeyPair(publicKey, privateKey), tokenLifespan);
    }

    public EmbeddedOidcProviderStub(String host, int port, String issuerPath, KeyPair keyPair) {
        this(host, port, issuerPath, keyPair, 60);
    }

    /**
     * Create an OIDC provider stub with the given configuration.
     *
     * @param host
     *         the host the provider is running on
     * @param port
     *         the port the provider is running on
     * @param issuerPath
     *         the path that is part of the issuer identifier, may be the empty string
     * @param keyPair
     *         RSA key pair to use for signing JWT and to return from the JWKS endpoint
     * @param tokenLifespan
     *         lifespan of the access tokens issued by the provider
     */
    public EmbeddedOidcProviderStub(String host, int port, String issuerPath, KeyPair keyPair, int tokenLifespan) {
        this.host = host;
        this.port = port;
        this.tokenLifespan = tokenLifespan;

        this.issuer = appendPath(URI.create(String.format("http://%s:%s", host, port)), issuerPath);

        this.keyId = UUID.randomUUID().toString();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    /**
     * Sets the claim to store the user roles in. The default is {@link #DEFAULT_ROLES_CLAIM_NAME}.
     * The value <em>must</em> be set before any access token is generated!
     *
     * @param rolesClaimName
     *         name of the roles claim
     */
    public void setRolesClaimName(String rolesClaimName) {
        this.rolesClaimName = rolesClaimName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public URI getIssuer() {
        return issuer;
    }

    public String getPublicKey() {
        return RsaKeyGenerator.encodePublicKey(publicKey);
    }

    /**
     * Returns an OIDC Configuration Response with the URIs that point to the other endpoints provided by the mock.
     *
     * @param jwksEndpoint
     *         path to the JWKS endpoint
     * @param authorizationEndpoint
     *         path to the authorization endpoint
     * @param tokenEndpoint
     *         path to the token endpoint
     * @return JSON representation of the configuration
     */
    public String getOIDCConfigResponse(String jwksEndpoint, String authorizationEndpoint, String tokenEndpoint) {

        OIDCProviderMetadata config = new OIDCProviderMetadata(new Issuer(issuer), Collections.singletonList(SubjectType.PUBLIC),
                appendPath(issuer, jwksEndpoint));
        config.setAuthorizationEndpointURI(appendPath(issuer, authorizationEndpoint));
        config.setTokenEndpointURI(appendPath(issuer, tokenEndpoint));

        return config.toJSONObject().toJSONString();
    }

    public String getJwksResponse() {
        // returns only the public keys from the JWK set
        return getJwkSet().toString();
    }

    public String getAccessTokenResponse(String clientId, String userName, Optional<String> bhknz, Set<String> roles) {
        return getAccessTokenResponse(getAccessToken(UUID.randomUUID(), clientId, userName, bhknz, roles, tokenLifespan));
    }

    public String getAccessTokenResponse(JwtClaimsSet claims) {
        Scope scope = Scope.parse("openid profile email");

        BearerAccessToken accessToken = new BearerAccessToken(getAccessTokenString(claims), tokenLifespan, scope);
        AccessTokenResponse response = new AccessTokenResponse(new Tokens(accessToken, null));

        return response.toJSONObject().toJSONString();
    }

    public String getAccessTokenString(String clientId, String userName, Optional<String> bhknz, Set<String> roles) {
        return getAccessTokenString(UUID.randomUUID(), clientId, userName, bhknz, roles);
    }

    public String getAccessTokenString(UUID userId, String clientId, String userName, Optional<String> bhknz, Set<String> roles) {
        return getAccessTokenString(getAccessToken(userId, clientId, userName, bhknz, roles));
    }

    public JwtClaimsSet getAccessToken(UUID userId, String clientId, String userName, Optional<String> bhknz, Set<String> roles) {
        return getAccessToken(userId, clientId, userName, bhknz, roles, tokenLifespan);
    }

    public JwtClaimsSet getAccessToken(UUID userId, String clientId, String userName, Optional<String> bhknz, Set<String> roles, int newTokenLifespan) {
        JwtClaimsSet.Builder accessTokenBuilder = JwtClaimsSet.builder()
                // required OIDC token claims
                .issuer(issuer.toString())
                .subject(userId.toString())
                .audience(Collections.singletonList(clientId)) // MUST contain the client_id, may contain other audiences
                .expiresAt(Instant.now().plusSeconds(newTokenLifespan))
                .issuedAt(Instant.now())
                // optional claims
                .claim(IdTokenClaimNames.AZP, clientId)
                .claim(StandardClaimNames.PREFERRED_USERNAME, userName) // OIDC standard claim
                .claim(rolesClaimName, roles); // claim configurable in isy-security

        bhknz.ifPresent(bk -> accessTokenBuilder.claim(BHKNZ_CLAIM_NAME, bk));

        return accessTokenBuilder.build();
    }

    public String getAccessTokenString(JwtClaimsSet claims) {
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .contentType("JSON")
                .keyId(keyId)
                .type("JWT")
                .build();

        JWKSource<SecurityContext> jwkSet = new ImmutableJWKSet<>(getJwkSet());

        Jwt jwt = new NimbusJwtEncoder(jwkSet).encode(JwtEncoderParameters.from(jwsHeader, claims));

        return jwt.getTokenValue();
    }

    public JWKSet getJwkSet() {
        JWK jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(keyId)
                .keyUse(KeyUse.SIGNATURE)
                .build();
        return new JWKSet(jwk);
    }

    protected URI appendPath(URI baseUri, String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(baseUri);
        if (!baseUri.getPath().endsWith("/")) {
            builder.path("/");
        }
        builder.path(path);
        return builder.build().toUri();
    }

}
