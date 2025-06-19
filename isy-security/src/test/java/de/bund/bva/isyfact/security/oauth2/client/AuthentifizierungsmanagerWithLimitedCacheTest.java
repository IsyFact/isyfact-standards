package de.bund.bva.isyfact.security.oauth2.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.TestPropertySource;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordClientRegistrationAuthenticationProvider;

/**
 * Tests the caching of the Authentifizierungsmanager with more authentication attempts than max. cached elements.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "isy.security.cache.ttl=300",
        "isy.security.cache.maxelements=2"
})
public class AuthentifizierungsmanagerWithLimitedCacheTest extends AbstractOidcProviderTest {

    @MockBean
    private PasswordClientRegistrationAuthenticationProvider passwordClientRegistrationAuthenticationProvider;

    @Autowired
    private Authentifizierungsmanager authentifizierungsmanager;

    private JwtAuthenticationToken mockJwt;

    @BeforeEach
    public void configureMocks() {
        // clear authenticated principal
        SecurityContextHolder.getContext().setAuthentication(null);
        mockJwt = mock(JwtAuthenticationToken.class);

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
        SecurityContextHolder.clearContext();
    }
}
