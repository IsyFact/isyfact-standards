package de.bund.bva.isyfact.security.oauth2.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import de.bund.bva.isyfact.security.authentication.ClaimsOnlyOAuth2AuthenticationToken;
import de.bund.bva.isyfact.security.authentication.ClaimsOnlyOAuth2Token;

public class IsySecurityTokenUtilTest {

    public static final String TEST_SUB = "test_sub";
    public static final String TEST_USER_ID = "test_userId";
    public static final String TEST_LOGIN = "test_login";
    public static final String TEST_BHKNZ = "test_bhknz";
    public static final String TEST_DISPLAY_NAME = "test_displayname";

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext(); // Sicherstellen, dass der Kontext vor jedem Test zurückgesetzt wird
    }

    public static Stream<Authentication> getUserId() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("internekennung", TEST_USER_ID)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.userId(TEST_USER_ID))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getUserId(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals(TEST_USER_ID, IsySecurityTokenUtil.getUserId());
    }

    public static Stream<Authentication> getUserIdWhenNull() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("internekennung", null)),
                buildBearerTokenAuthentication(builder -> builder.claim("internekennung", null)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.userId(null))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getUserIdWhenNull(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals(TEST_SUB, IsySecurityTokenUtil.getUserId());
    }

    public static Stream<Authentication> getUserIdWhenEmpty() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("internekennung", "")),
                buildBearerTokenAuthentication(builder -> builder.claim("internekennung", "")),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.userId(""))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getUserIdWhenEmpty(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals("", IsySecurityTokenUtil.getUserId());
    }

    public static Stream<Authentication> getLogin() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("preferred_username", TEST_LOGIN)),
                buildBearerTokenAuthentication(builder -> builder.claim("preferred_username", TEST_LOGIN)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.login(TEST_LOGIN))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getLogin(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertTrue(IsySecurityTokenUtil.getLogin().isPresent());
        assertEquals(TEST_LOGIN, IsySecurityTokenUtil.getLogin().get());
    }

    public static Stream<Authentication> getLoginWhenEmpty() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("preferred_username", "")),
                buildBearerTokenAuthentication(builder -> builder.claim("preferred_username", "")),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.login(""))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getLoginWhenEmpty(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> optLogin = IsySecurityTokenUtil.getLogin();

        assertTrue(optLogin.isPresent());
        assertEquals("", optLogin.get());
    }

    public static Stream<Authentication> getLoginWhenNull() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("preferred_username", null)),
                buildBearerTokenAuthentication(builder -> builder.claim("preferred_username", null)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.login(null))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getLoginWhenNull(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertFalse(IsySecurityTokenUtil.getLogin().isPresent());
    }

    public static Stream<Authentication> getBhknz() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("bhknz", TEST_BHKNZ)),
                buildBearerTokenAuthentication(builder -> builder.claim("bhknz", TEST_BHKNZ)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.bhknz(TEST_BHKNZ))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getBhknz(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> optBhknz = IsySecurityTokenUtil.getBhknz();

        assertTrue(optBhknz.isPresent());
        assertEquals(TEST_BHKNZ, optBhknz.get());
    }

    private static Stream<Authentication> getBhknzWhenEmpty() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("bhknz", "")),
                buildBearerTokenAuthentication(builder -> builder.claim("bhknz", "")),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.bhknz(""))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getBhknzWhenEmpty(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> optBhknz = IsySecurityTokenUtil.getBhknz();

        assertTrue(optBhknz.isPresent());
        assertEquals("", optBhknz.get());
    }

    private static Stream<Authentication> getBhknzWhenNull() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("bhknz", null)),
                buildBearerTokenAuthentication(builder -> builder.claim("bhknz", null)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.bhknz(null))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getBhknzWhenNull(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertFalse(IsySecurityTokenUtil.getBhknz().isPresent());
    }

    public static Stream<Authentication> getDisplayName() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("name", TEST_DISPLAY_NAME)),
                buildBearerTokenAuthentication(builder -> builder.claim("name", TEST_DISPLAY_NAME)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.displayName(TEST_DISPLAY_NAME))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getDisplayName(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> optDisplayName = IsySecurityTokenUtil.getDisplayName();

        assertTrue(optDisplayName.isPresent());
        assertEquals(TEST_DISPLAY_NAME, optDisplayName.get());
    }

    public static Stream<Authentication> getDisplayNameEmpty() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("name", "")),
                buildBearerTokenAuthentication(builder -> builder.claim("name", "")),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.displayName(""))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getDisplayNameEmpty(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> optDisplayName = IsySecurityTokenUtil.getDisplayName();

        assertTrue(optDisplayName.isPresent());
        assertEquals("", optDisplayName.get());
    }

    public static Stream<Authentication> getDisplayNameNull() {
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.claim("name", null).claim("preferred_username", TEST_LOGIN)),
                buildBearerTokenAuthentication(builder -> builder.claim("name", null).claim("preferred_username", TEST_LOGIN)),
                buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.displayName(null).login(TEST_LOGIN))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getDisplayNameNull(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> optDisplayName = IsySecurityTokenUtil.getDisplayName();

        assertTrue(optDisplayName.isPresent());
        assertEquals(TEST_LOGIN, optDisplayName.get());
    }

    public static Stream<Authentication> hasTokenExpiredTrueWhenJwtBearerExpired() {
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.MINUTES);
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.expiresAt(expiresAt)),
                buildBearerTokenAuthentication(builder -> builder.expiresAt(expiresAt))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void hasTokenExpiredTrueWhenJwtBearerExpired(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertTrue(IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(61)));
    }

    public static Stream<Authentication> hasTokenExpiredFalseWhenJwtBearerNotExpired() {
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.expiresAt(expiresAt)),
                buildBearerTokenAuthentication(builder -> builder.expiresAt(expiresAt))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void hasTokenExpiredFalseWhenJwtBearerNotExpired(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertFalse(IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(60)));
    }

    public static Stream<Authentication> hasTokenExpiredTrueWhenJwtBearerWithoutExpiredAtProperty() {
        // set a non expired timestamp first and then remove the claim
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
        return Stream.of(
                buildJwtAuthenticationToken(builder -> builder.expiresAt(expiresAt).claims(claim -> claim.remove(JwtClaimNames.EXP))),
                buildBearerTokenAuthentication(builder -> builder.expiresAt(expiresAt).claims(claims -> claims.remove(IdTokenClaimNames.EXP)))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void hasTokenExpiredTrueWhenJwtBearerWithoutExpiredAtProperty(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertTrue(IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(60)));
    }

    @Test
    public void hasTokenExpiredTrueWhenClaimOnlyTokenWithEXPCLaim() {
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
        SecurityContextHolder.getContext().setAuthentication(buildClaimsOnlyOAuth2AuthenticationToken(builder -> builder.claim(JwtClaimNames.EXP, expiresAt)));

        assertTrue(IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(60)));
    }

    @Test
    public void getAuthenticationTokenThrowsExceptionWhenTokenIsNotOAuth2() {
        Authentication notOAuth2Authentication = new TestingAuthenticationToken("principal", "credentials");
        SecurityContextHolder.getContext().setAuthentication(notOAuth2Authentication);

        assertThrows(OAuth2AuthenticationException.class, IsySecurityTokenUtil::getAuthenticationToken);
    }

    @Test
    public void getAuthenticationTokenThrowsExceptionWhenTokenIsNull() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertThrows(OAuth2AuthenticationException.class, IsySecurityTokenUtil::getAuthenticationToken);
    }

    @Test
    public void getAuthenticationTokenThrowsExceptionWhenSecurityContextIsEmpty() {
        SecurityContextHolder.clearContext();

        assertThrows(OAuth2AuthenticationException.class, IsySecurityTokenUtil::getAuthenticationToken);
    }

    private static JwtAuthenticationToken buildJwtAuthenticationToken(UnaryOperator<Jwt.Builder> builder) {
        Jwt jwt = builder.apply(Jwt.withTokenValue("dummy")
                        .header("dummy_header", "dummy_header_value")
                        .claim(StandardClaimNames.SUB, TEST_SUB))
                .build();
        return new JwtAuthenticationToken(jwt);
    }

    private static BearerTokenAuthentication buildBearerTokenAuthentication(UnaryOperator<OidcIdToken.Builder> builder) {
        OidcIdToken idToken = builder.apply(OidcIdToken.withTokenValue("dummy")
                        .claim(StandardClaimNames.SUB, TEST_SUB))
                .build();
        DefaultOidcUser principal = new DefaultOidcUser(null, idToken);
        return new BearerTokenAuthentication(principal, new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                principal.getIdToken().getTokenValue(), principal.getIssuedAt(), principal.getExpiresAt()),
                principal.getAuthorities());
    }

    private static ClaimsOnlyOAuth2AuthenticationToken buildClaimsOnlyOAuth2AuthenticationToken(UnaryOperator<ClaimsOnlyOAuth2Token.Builder> builder) {
        ClaimsOnlyOAuth2Token token = builder.apply(ClaimsOnlyOAuth2Token.withSubject(TEST_SUB)).build();
        return token.asAuthentication();
    }
}
