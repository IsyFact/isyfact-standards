package de.bund.bva.isyfact.security.oauth2.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class IsySecurityTokenUtilTest {


    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext(); // Sicherstellen, dass der Kontext vor jedem Test zurückgesetzt wird
    }

    @Test
    public void testGetUserId() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(StandardClaimNames.SUB, "test_sub");
        attributes.put("internekennung", "test_userId");
        mockSecurityContextWithTokenAttributes(attributes);

        assertEquals("test_userId", IsySecurityTokenUtil.getUserId());
    }

    @Test
    public void testGetUserIdWhenNull() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("internekennung", null);
        attributes.put(StandardClaimNames.SUB, "test_sub");
        mockSecurityContextWithTokenAttributes(attributes);

        assertEquals("test_sub", IsySecurityTokenUtil.getUserId());
    }


    @Test
    public void testGetUserIdWhenEmpty() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("internekennung", "");
        mockSecurityContextWithTokenAttributes(attributes);

        assertEquals("", IsySecurityTokenUtil.getUserId());
    }

    @Test
    public void testGetLogin() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("preferred_username", "test_login");
        mockSecurityContextWithTokenAttributes(attributes);

        assertTrue(IsySecurityTokenUtil.getLogin().isPresent());
        assertEquals("test_login", IsySecurityTokenUtil.getLogin().get());
    }

    @Test
    public void testGetLoginWhenEmpty() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("preferred_username", "");
        mockSecurityContextWithTokenAttributes(attributes);

        Optional<String> optLogin = IsySecurityTokenUtil.getLogin();

        assertTrue(optLogin.isPresent());
        assertEquals("", optLogin.get());
    }

    @Test
    public void testGetLoginWhenNull() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("preferred_username", null);
        mockSecurityContextWithTokenAttributes(attributes);

        assertFalse(IsySecurityTokenUtil.getLogin().isPresent());
    }

    @Test
    public void testGetBhknz() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("bhknz", "test_bhknz");
        mockSecurityContextWithTokenAttributes(attributes);

        Optional<String> optBhknz = IsySecurityTokenUtil.getBhknz();

        assertTrue(optBhknz.isPresent());
        assertEquals("test_bhknz", optBhknz.get());
    }

    @Test
    public void testGetBhknzWhenEmpty() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("bhknz", "");
        mockSecurityContextWithTokenAttributes(attributes);

        Optional<String> optBhknz = IsySecurityTokenUtil.getBhknz();

        assertTrue(optBhknz.isPresent());
        assertEquals("", optBhknz.get());
    }

    @Test
    public void testGetBhknzWhenNull() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("bhknz", null);
        mockSecurityContextWithTokenAttributes(attributes);

        assertFalse(IsySecurityTokenUtil.getBhknz().isPresent());
    }

    @Test
    public void testGetDisplayName() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "test_displayname");
        mockSecurityContextWithTokenAttributes(attributes);

        Optional<String> optDisplayName = IsySecurityTokenUtil.getDisplayName();

        assertTrue(optDisplayName.isPresent());
        assertEquals("test_displayname", optDisplayName.get());
    }

    @Test
    public void testGetDisplayNameEmpty() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "");
        mockSecurityContextWithTokenAttributes(attributes);

        Optional<String> optDisplayName = IsySecurityTokenUtil.getDisplayName();

        assertTrue(optDisplayName.isPresent());
        assertEquals("", optDisplayName.get());
    }

    @Test
    public void testGetDisplayNameNull() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", null);
        attributes.put("preferred_username", "test_login");
        mockSecurityContextWithTokenAttributes(attributes);

        Optional<String> optDisplayName = IsySecurityTokenUtil.getDisplayName();

        assertTrue(optDisplayName.isPresent());
        assertEquals("test_login", optDisplayName.get());
    }

    @Test
    public void testHasTokenExpiredWhenTokenExpired() {
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.MINUTES);

        Jwt token = Mockito.mock(Jwt.class);
        when(token.getExpiresAt()).thenReturn(expiresAt);

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(token));

        assertTrue(IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(61)));
        verify(token).getExpiresAt();
    }

    @Test
    public void testHasTokenExpiredWhenTokenNotExpired() {
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);

        Jwt token = Mockito.mock(Jwt.class);
        when(token.getExpiresAt()).thenReturn(expiresAt);

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(token));

        assertFalse(IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(60)));
        verify(token).getExpiresAt();
    }

    @Test
    public void testHasTokenExpiredWhenTokenWithoutExpiredAtProperty() {
        Jwt token = Mockito.mock(Jwt.class);
        when(token.getExpiresAt()).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(token));

        assertTrue(IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(60)));
        verify(token).getExpiresAt();
    }

    @Test
    public void getAuthenticationTokenThrowsExceptionWhenTokenIsNotOAuth2() {
        Authentication notOAuth2Authentication = new TestingAuthenticationToken("principal", "credentials");
        SecurityContextHolder.getContext().setAuthentication(notOAuth2Authentication);

        assertThrows(OAuth2AuthenticationException.class, IsySecurityTokenUtil::getAuthenticationToken);
    }

    private void mockSecurityContextWithTokenAttributes(Map<String, Object> attributes) {
        Map<String, Object> attrMap = new HashMap<>(attributes);
        AbstractOAuth2TokenAuthenticationToken<?> authenticationToken = Mockito.mock(AbstractOAuth2TokenAuthenticationToken.class);
        when(authenticationToken.getTokenAttributes()).thenReturn(Collections.unmodifiableMap(attrMap));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
