package de.bund.bva.isyfact.security.test.oidcprovider;

import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.BHKNZ_CLAIM_NAME;
import static de.bund.bva.isyfact.security.test.oidcprovider.OidcProviderMockBase.BHKNZ_HEADER_NAME;
import static de.bund.bva.isyfact.security.test.oidcprovider.OidcProviderMockBase.JWKS_ENDPOINT;
import static de.bund.bva.isyfact.security.test.oidcprovider.OidcProviderMockBase.OIDC_CONFIG_ENDPOINT;
import static de.bund.bva.isyfact.security.test.oidcprovider.OidcProviderMockBase.TOKEN_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import reactor.core.publisher.Mono;

class EmbeddedOidcProviderMockTest {

    public static final ObjectMapper mapper = new ObjectMapper();

    private static final String host = "localhost";

    private static final int port = 9096;

    private static final String issuerPath = "/auth/realms/testrealm";

    @RegisterExtension
    private static final EmbeddedOidcProviderMock mock = new EmbeddedOidcProviderMock(host, port, issuerPath);

    public static final String CC_ID = "client-client";
    public static final String CC_SECRET = "cc-secret";
    public static final String UC_ID = "user-client";
    public static final String UC_SECRET = "uc-secret";
    public static final String USER_WITHOUT_BHKNZ = "user-without-bhknz";
    public static final String USER_WITH_BHKNZ = "user-with-bhknz";
    public static final String USER_PASSWORD = "test";
    public static final String USER_BHKNZ = "123456";
    public static final String USER_OU = "USEROU";
    public static final String INVALID_BHKNZ = "999999";

    private static WebClient webClient;

    @BeforeAll
    public static void setupWebClient() {
        webClient = WebClient.builder().baseUrl("http://" + host + ":" + port + issuerPath).build();
        mock.setSecondOu(USER_OU);
        mock.addUser(UC_ID, UC_SECRET, USER_WITHOUT_BHKNZ, USER_PASSWORD, Optional.empty(), Collections.emptySet());
        mock.addUser(UC_ID, UC_SECRET, USER_WITH_BHKNZ, USER_PASSWORD, Optional.of(USER_BHKNZ), Collections.emptySet());
        mock.addClient(CC_ID, CC_SECRET, Collections.emptySet());
    }

    @Test
    void testOidcConfigEndpoint() {
        HttpStatusCode status = webClient.get().uri(OIDC_CONFIG_ENDPOINT)
                .exchangeToMono(response -> Mono.just(response.statusCode())).block();

        assertEquals(HttpStatus.OK, status);
    }

    @Test
    void testJwksEndpoint() {
        HttpStatusCode status = webClient.get().uri(JWKS_ENDPOINT)
                .exchangeToMono(response -> Mono.just(response.statusCode())).block();

        assertEquals(HttpStatus.OK, status);
    }

    @Test
    void testTokenEndpointWithoutBodyFails() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .exchangeToMono(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_request", "Missing grant type");
    }

    @Test
    void testTokenEndpointWithOnlyGrantTypeFails() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .exchangeToMono(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("unsupported_grant_type", "Invalid client-id or secret");
    }

    @Test
    void testTokenEndpointWithoutUsernameFails() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_grant", "Missing username");
    }

    @Test
    void testTokenEndpointWithoutPasswordFails() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITHOUT_BHKNZ)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_grant", "Missing password");
    }

    @Test
    void testUserWithoutBhknzDoesNotHaveBhknzInToken() throws JsonProcessingException, ParseException {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> headers.setBasicAuth(UC_ID, UC_SECRET))
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITHOUT_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        String token = mapper.readTree(body).get("access_token").asText();
        JWTClaimsSet claims = JWTParser.parse(token).getJWTClaimsSet();

        assertEquals(USER_WITHOUT_BHKNZ, claims.getStringClaim(StandardClaimNames.PREFERRED_USERNAME));
        assertFalse(claims.getClaims().containsKey(BHKNZ_CLAIM_NAME));
    }

    @Test
    void testUserWithoutBhknzAndWithBhknzHeaderDoesNotHaveBhknzInToken() throws JsonProcessingException, ParseException {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> {
                    headers.setBasicAuth(UC_ID, UC_SECRET);
                    headers.add(BHKNZ_HEADER_NAME, String.format("%s:%s", INVALID_BHKNZ, USER_OU));
                })
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITHOUT_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        String token = mapper.readTree(body).get("access_token").asText();
        JWTClaimsSet claims = JWTParser.parse(token).getJWTClaimsSet();

        assertEquals(USER_WITHOUT_BHKNZ, claims.getStringClaim(StandardClaimNames.PREFERRED_USERNAME));
        assertFalse(claims.getClaims().containsKey(BHKNZ_CLAIM_NAME));
    }

    @Test
    void testUserWithBhknzAndBhknzHeaderHasBhknzInToken1() throws JsonProcessingException, ParseException {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> {
                    headers.setBasicAuth(UC_ID, UC_SECRET);
                    headers.add(BHKNZ_HEADER_NAME, String.format("%s:%s", USER_BHKNZ, USER_OU)); // bhknz first value
                })
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITH_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        String token = mapper.readTree(body).get("access_token").asText();
        JWTClaimsSet claims = JWTParser.parse(token).getJWTClaimsSet();

        assertEquals(USER_WITH_BHKNZ, claims.getStringClaim(StandardClaimNames.PREFERRED_USERNAME));
        assertEquals(USER_BHKNZ, claims.getStringClaim(BHKNZ_CLAIM_NAME));
    }

    @Test
    void testUserWithBhknzAndBhknzHeaderHasBhknzInToken2() throws JsonProcessingException, ParseException {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> {
                    headers.setBasicAuth(UC_ID, UC_SECRET);
                    headers.add(BHKNZ_HEADER_NAME, String.format("%s:%s", USER_OU, USER_BHKNZ)); // OU first value
                })
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITH_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        String token = mapper.readTree(body).get("access_token").asText();
        JWTClaimsSet claims = JWTParser.parse(token).getJWTClaimsSet();

        assertEquals(USER_WITH_BHKNZ, claims.getStringClaim(StandardClaimNames.PREFERRED_USERNAME));
        assertEquals(USER_BHKNZ, claims.getStringClaim(BHKNZ_CLAIM_NAME));
    }

    @Test
    void testUserWithBhknzAndWithoutBhknzHeaderIsUnauthorized() {
        // this test does not reflect how a dedicated OIDC provider might act
        // but instead tests the behaviour required by the mock to distinguish between users with and without bhknz
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> headers.setBasicAuth(UC_ID, UC_SECRET))
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITH_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_grant", "Invalid bhknz");
    }

    @Test
    void testUserWithBhknzAndWithoutBhknzInHeaderIsUnauthorized() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> {
                    headers.setBasicAuth(UC_ID, UC_SECRET);
                    headers.add(BHKNZ_HEADER_NAME, String.format("%s:%s", INVALID_BHKNZ, USER_OU));
                })
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITH_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_grant", "Invalid bhknz");
    }

    @Test
    void testUserWithBhknzAndWithoutOUInHeaderIsUnauthorized() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> {
                    headers.setBasicAuth(UC_ID, UC_SECRET);
                    headers.add(BHKNZ_HEADER_NAME, String.format("%s:%s", USER_BHKNZ, "wrong"));
                })
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITH_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_grant", "Invalid bhknz");
    }

    @Test
    void testUserWithInvalidUsernameFails() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> headers.setBasicAuth(UC_ID, UC_SECRET))
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, "invalid")
                        .with(OAuth2ParameterNames.PASSWORD, USER_PASSWORD)
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_grant", "Invalid username");
    }

    @Test
    void testUserWithInvalidPasswordFails() {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> headers.setBasicAuth(UC_ID, UC_SECRET))
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue())
                        .with(OAuth2ParameterNames.USERNAME, USER_WITHOUT_BHKNZ)
                        .with(OAuth2ParameterNames.PASSWORD, "invalid")
                ).exchangeToMono(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        assertThat(body).contains("invalid_grant", "Invalid password");
    }

    @Test
    void testClientWorks() throws JsonProcessingException, ParseException {
        String body = webClient.post().uri(TOKEN_ENDPOINT)
                .headers(headers -> headers.setBasicAuth(CC_ID, CC_SECRET))
                .body(BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .exchangeToMono(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return response.bodyToMono(String.class);
                }).block();

        String token = mapper.readTree(body).get("access_token").asText();
        JWTClaimsSet claims = JWTParser.parse(token).getJWTClaimsSet();

        assertEquals("service-account-client-client", claims.getStringClaim(StandardClaimNames.PREFERRED_USERNAME));
        assertFalse(claims.getClaims().containsKey(BHKNZ_CLAIM_NAME));
    }

}
