package de.bund.bva.isyfact.security.oauth2.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordClientRegistrationAuthenticationProvider;

/**
 * Tests the caching of the Authentifizierungsmanager with more authentication attempts than max. cached elements.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "isy.security.cache.ttl=300",
    "isy.security.cache.maxelements=2",
    "isy.security.cache.token-expiration-time-offset=10",
    "isy.security.cache.salt-bytes=64",
    "isy.security.cache.hash-algorithm=SHA-512"
})
public class AuthentifizierungsmanagerWithLimitedCacheTest extends AbstractOidcProviderTest {

    @MockitoBean
    private PasswordClientRegistrationAuthenticationProvider passwordClientRegistrationAuthenticationProvider;

    @Autowired
    private Authentifizierungsmanager authentifizierungsmanager;

    private JwtAuthenticationToken mockJwt;

    private Jwt mockToken;

    @BeforeEach
    public void configureMocks() throws NoSuchFieldException, IllegalAccessException {
        // clear authenticated principal
        SecurityContextHolder.getContext().setAuthentication(null);
        mockJwt = mock(JwtAuthenticationToken.class);

        mockToken = mock(Jwt.class);
        Field field = AbstractOAuth2TokenAuthenticationToken.class.getDeclaredField("token");
        field.setAccessible(true);
        field.set(mockJwt, mockToken);
        when(mockJwt.getToken()).thenCallRealMethod();
        when(mockToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(300));

        when(passwordClientRegistrationAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(passwordClientRegistrationAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testCacheWithMoreAuthenticationAttemptsThanCachedElements() {

        // Override value from @BeforeEach otherwise the authentication.isAuthenticated returns always false
        when(mockJwt.isAuthenticated()).thenReturn(true);

        // First authentication attempt with credentials from testid1
        // Provider is called
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid1", "testsecret1", "testuser1", "testpw1");
        verify(passwordClientRegistrationAuthenticationProvider, times(1)).authenticate(any());
        SecurityContextHolder.clearContext();
        clearInvocations(passwordClientRegistrationAuthenticationProvider);

        // Second authentication attempt with credentials from testid1
        // provider is not called, authentication data is taken from cache
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid1", "testsecret1", "testuser1", "testpw1");
        verify(passwordClientRegistrationAuthenticationProvider, never()).authenticate(any());
        SecurityContextHolder.clearContext();

        // First authentication attempt with credentials from testid2
        // Provider is called
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid2", "testsecret2", "testuser2", "testpw2");
        verify(passwordClientRegistrationAuthenticationProvider, times(1)).authenticate(any());
        SecurityContextHolder.clearContext();
        clearInvocations(passwordClientRegistrationAuthenticationProvider);

        // Second authentication attempt with credentials from testid2
        // provider is not called, authentication data is taken from cache
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid2", "testsecret2", "testuser2", "testpw2");
        verify(passwordClientRegistrationAuthenticationProvider, never()).authenticate(any());
        SecurityContextHolder.clearContext();

        // First authentication attempt with credentials from testid3
        // Provider is called
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid3", "testsecret3", "testuser3", "testpw3");
        verify(passwordClientRegistrationAuthenticationProvider, times(1)).authenticate(any());
        SecurityContextHolder.clearContext();
        clearInvocations(passwordClientRegistrationAuthenticationProvider);

        // Now third authentication attempt with credentials from testid1
        // Provider is called because there is no more cached data for testid1 due to the maxelements specification
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid1", "testsecret1", "testuser1", "testpw1");
        verify(passwordClientRegistrationAuthenticationProvider, times(1)).authenticate(any());
    }

    @Test
    public void testCacheWithExpiredToken() throws InterruptedException {
        // set expiry time of token in 20 seconds
        when(mockToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(20));

        when(mockJwt.isAuthenticated()).thenReturn(true);

        // First authentication attempt with credentials from testid1
        // Provider is called
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid1", "testsecret1", "testuser1", "testpw1");
        verify(passwordClientRegistrationAuthenticationProvider, times(1)).authenticate(any());
        SecurityContextHolder.clearContext();
        clearInvocations(passwordClientRegistrationAuthenticationProvider);

        // Second authentication attempt with credentials from testid1
        // provider is not called, authentication data is taken from cache
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid1", "testsecret1", "testuser1", "testpw1");
        verify(passwordClientRegistrationAuthenticationProvider, never()).authenticate(any());
        SecurityContextHolder.clearContext();

        // wait for 10 seconds to ensure we don't have enough time left for the token
        Thread.sleep(10000);

        // Now third authentication attempt with credentials from testid1
        // Provider is called because the cached token is expired
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid1", "testsecret1", "testuser1", "testpw1");
        verify(passwordClientRegistrationAuthenticationProvider, times(1)).authenticate(any());
        clearInvocations(passwordClientRegistrationAuthenticationProvider);

        // refresh token expiry
        when(mockToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(20));
        // fourth authentication attempt with credentials from testid1
        // provider is not called, authentication data is taken from cache
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid1", "testsecret1", "testuser1", "testpw1");
        verify(passwordClientRegistrationAuthenticationProvider, never()).authenticate(any());
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    public void tearDown() {
        authentifizierungsmanager.clearCache();
        SecurityContextHolder.clearContext();
    }
}
