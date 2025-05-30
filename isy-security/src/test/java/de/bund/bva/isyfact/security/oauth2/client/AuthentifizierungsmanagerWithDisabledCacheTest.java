package de.bund.bva.isyfact.security.oauth2.client;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
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
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsClientRegistrationAuthenticationProvider;

/**
 * Tests the Authentifizierungsmanager with disabled caching.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "isy.security.cache.ttl=0"
})
public class AuthentifizierungsmanagerWithDisabledCacheTest extends AbstractOidcProviderTest {

    @MockBean
    private ClientCredentialsClientRegistrationAuthenticationProvider clientCredentialsProvider;

    @Autowired
    private Authentifizierungsmanager authentifizierungsmanager;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.clearContext();
        JwtAuthenticationToken mockJwt = mock(JwtAuthenticationToken.class);

        when(clientCredentialsProvider.supports(any())).thenCallRealMethod();
        when(clientCredentialsProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testNoCachingWhenDisabled() {

        authentifizierungsmanager.authentifiziereClient(getIssuer(), "testid", "testsecret");
        verify(clientCredentialsProvider, times(1)).authenticate(any());
        SecurityContextHolder.clearContext();

        authentifizierungsmanager.authentifiziereClient(getIssuer(), "testid", "testsecret");
        verify(clientCredentialsProvider, times(2)).authenticate(any());
    }
}
