package de.bund.bva.isyfact.security.test.oidcprovider;

import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.BHKNZ_CLAIM_NAME;
import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.DEFAULT_ROLES_CLAIM_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKParameterNames;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import de.bund.bva.isyfact.security.test.RsaKeyGenerator;

/**
 * Unit tests for {@link EmbeddedOidcProviderStub}.
 */
public class EmbeddedOidcProviderStubTest {

    public static final ObjectMapper mapper = new ObjectMapper();

    private static final int tokenLifespan = 30;

    private final EmbeddedOidcProviderStub oidcProviderStub =
            new EmbeddedOidcProviderStub("oidc-provider", 9096, "/auth/realms/testrealm", tokenLifespan);

    @Test
    void testAccessTokenWithBhknz() throws ParseException {
        UUID userId = UUID.randomUUID();
        String clientId = "testclient";
        String userName = "testuser";
        String bhknz = "123456";
        List<String> roles = Arrays.asList("testrole1", "testrole2");
        Instant preRequestTime = Instant.now().minusSeconds(1); // subtract a second because "iat" does not have millis

        String accessTokenString = oidcProviderStub.getAccessTokenString(userId, clientId, userName, Optional.of(bhknz),
                new HashSet<>(roles));
        JWT token = JWTParser.parse(accessTokenString);
        JWTClaimsSet claims = token.getJWTClaimsSet();

        assertEquals("http://oidc-provider:9096/auth/realms/testrealm", claims.getIssuer());
        assertEquals(userId.toString(), claims.getSubject());
        assertThat(claims.getAudience()).containsOnly(clientId);
        assertEquals(userName, claims.getStringClaim(StandardClaimNames.PREFERRED_USERNAME));
        assertThat(claims.getStringArrayClaim(DEFAULT_ROLES_CLAIM_NAME)).containsOnly("testrole1", "testrole2");
        assertEquals(bhknz, claims.getStringClaim(BHKNZ_CLAIM_NAME));

        Instant issueTime = claims.getIssueTime().toInstant();
        assertTrue(issueTime.isAfter(preRequestTime));
        assertTrue(issueTime.isBefore(Instant.now()));
        Instant expirationTime = claims.getExpirationTime().toInstant();
        assertEquals(expirationTime, issueTime.plusSeconds(tokenLifespan));
    }

    @Test
    void testAccessTokenWithoutBhknz() throws ParseException {
        UUID userId = UUID.randomUUID();
        String clientId = "testclient";
        String userName = "testuser";
        List<String> roles = Arrays.asList("testrole1", "testrole2");

        String accessTokenString = oidcProviderStub.getAccessTokenString(userId, clientId, userName, Optional.empty(),
                new HashSet<>(roles));
        JWT token = JWTParser.parse(accessTokenString);
        JWTClaimsSet claims = token.getJWTClaimsSet();

        assertEquals("http://oidc-provider:9096/auth/realms/testrealm", claims.getIssuer());
        assertEquals(userId.toString(), claims.getSubject());
        assertThat(claims.getAudience()).containsOnly(clientId);
        assertEquals(userName, claims.getStringClaim(StandardClaimNames.PREFERRED_USERNAME));
        assertThat(claims.getStringArrayClaim(DEFAULT_ROLES_CLAIM_NAME)).containsOnly("testrole1", "testrole2");
        assertFalse(claims.getClaims().containsKey(BHKNZ_CLAIM_NAME));
    }

    @Test
    void testAccessTokenResponse() throws JsonProcessingException, ParseException {
        String clientId = "testclient";
        String userName = "testuser";
        String bhknz = "123456";
        List<String> roles = Arrays.asList("testrole1", "testrole2");

        String accessTokenResponse = oidcProviderStub.getAccessTokenResponse(clientId, userName, Optional.of(bhknz), new HashSet<>(roles));

        JsonNode tree = mapper.readTree(accessTokenResponse);

        // test if valid JWT, content is validated in different tests
        assertNotNull(JWTParser.parse(tree.get("access_token").asText()));
        assertEquals("Bearer", tree.get("token_type").asText());
        Instant expiresIn = Instant.ofEpochSecond(tree.get("expires_in").asLong());
        assertTrue(expiresIn.isAfter(Instant.now()));
        assertTrue(expiresIn.isBefore(Instant.now().plusSeconds(tokenLifespan).plusSeconds(60)));
    }

    @Test
    void testOIDCConfigResponse() throws JsonProcessingException {
        String configResponse = oidcProviderStub.getOIDCConfigResponse("/certs", "/auth", "/token");

        JsonNode tree = mapper.readTree(configResponse);

        assertEquals("http://oidc-provider:9096/auth/realms/testrealm", tree.get("issuer").asText());
        assertEquals("http://oidc-provider:9096/auth/realms/testrealm/certs", tree.get("jwks_uri").asText());
        assertEquals("http://oidc-provider:9096/auth/realms/testrealm/auth", tree.get("authorization_endpoint").asText());
        assertEquals("http://oidc-provider:9096/auth/realms/testrealm/token", tree.get("token_endpoint").asText());
    }

    @Test
    void testJwkResponse() throws JsonProcessingException {
        RSAPublicKey expectedPublicKey = (RSAPublicKey) RsaKeyGenerator.decodePublicKey(oidcProviderStub.getPublicKey());

        String jwkResponse = oidcProviderStub.getJwksResponse();

        JsonNode tree = mapper.readTree(jwkResponse);
        JsonNode key = tree.get("keys").get(0);

        assertFalse(key.get(JWKParameterNames.KEY_ID).asText().isEmpty());
        assertEquals("RSA", key.get(JWKParameterNames.KEY_TYPE).asText());
        assertEquals("RS256", key.get(JWKParameterNames.ALGORITHM).asText());
        assertEquals("sig", key.get(JWKParameterNames.PUBLIC_KEY_USE).asText());
        assertEquals(Base64URL.encode(expectedPublicKey.getModulus()).toString(), key.get(JWKParameterNames.RSA_MODULUS).asText());
        assertEquals(Base64URL.encode(expectedPublicKey.getPublicExponent()).toString(), key.get(JWKParameterNames.RSA_EXPONENT).asText());
    }

}
