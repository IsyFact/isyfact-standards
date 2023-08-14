package de.bund.bva.isyfact.security.oauth2.client;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordAuthenticationToken;

@SpringBootTest
public class AuthentifizierungsmanagerWithoutClientsConfiguredTest extends AbstractOidcProviderTest {

    @MockBean
    private ManualClientCredentialsAuthenticationProvider manualClientCredentialsAuthenticationProvider;

    @MockBean
    private PasswordAuthenticationProvider passwordAuthenticationProvider;

    @Autowired
    private Authentifizierungsmanager authentifizierungsmanager;

    @Autowired
    private ProviderManager isyOAuth2AuthenticationProviderManager;

    private JwtAuthenticationToken mockJwt;

    @BeforeEach
    public void configureMocks() {
        // clear authenticated principal
        SecurityContextHolder.getContext().setAuthentication(null);

        mockJwt = mock(JwtAuthenticationToken.class);

        when(manualClientCredentialsAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(manualClientCredentialsAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(passwordAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(passwordAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testHasOnlyManualAuthenticationProvider() {
        assertThat(isyOAuth2AuthenticationProviderManager.getProviders())
                .containsOnly(manualClientCredentialsAuthenticationProvider, passwordAuthenticationProvider);
    }

    @Test
    public void testAuthWithRegistrationIdThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziere("testclient"));

        assertThat(exception).hasMessageContaining("testclient");
    }

    @Test
    public void testAuthClient() {
        authentifizierungsmanager.authentifiziereClient(getIssuer(), "testid", "testsecret", "123456");

        ArgumentCaptor<ManualClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ManualClientCredentialsAuthenticationToken.class);
        verify(manualClientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        ManualClientCredentialsAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthSystem() {
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid", "testsecret", "testuser", "pw", "123456");

        ArgumentCaptor<PasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordAuthenticationToken.class);
        verify(passwordAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertEquals("testuser", value.getUsername());
        assertEquals("pw", value.getPassword());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

}
