package de.bund.bva.isyfact.security.oauth2.client;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualPasswordAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualPasswordAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordAuthenticationToken;

@SpringBootTest
public class AuthentifizierungsmanagerWithoutClientsConfiguredTest {

    @MockBean
    private ManualClientCredentialsAuthenticationProvider manualClientCredentialsAuthenticationProvider;

    @MockBean
    private ManualPasswordAuthenticationProvider manualPasswordAuthenticationProvider;

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

        when(manualPasswordAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(manualPasswordAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testHasOnlyManualAuthenticationProvider() {
        assertThat(isyOAuth2AuthenticationProviderManager.getProviders())
                .containsOnly(manualClientCredentialsAuthenticationProvider, manualPasswordAuthenticationProvider);
    }

    @Test
    public void testAuthWithRegistrationIdThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziere("testclient"));

        assertThat(exception).hasMessageContaining("testclient");
    }

    @Test
    public void testAuthClient() {
        authentifizierungsmanager.authentifiziereClient("http://test/", "testid", "testsecret", "123456");

        ArgumentCaptor<ManualClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ManualClientCredentialsAuthenticationToken.class);
        verify(manualClientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("http://test/", tokenCaptor.getValue().getIssuerLocation());
        assertEquals("testid", tokenCaptor.getValue().getClientId());
        assertEquals("testsecret", tokenCaptor.getValue().getClientSecret());
        assertEquals("123456", tokenCaptor.getValue().getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthSystem() {
        authentifizierungsmanager.authentifiziereSystem("http://test/", "testid", "testsecret", "testuser", "pw", "123456");

        ArgumentCaptor<ManualPasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ManualPasswordAuthenticationToken.class);
        verify(manualPasswordAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("http://test/", tokenCaptor.getValue().getIssuerLocation());
        assertEquals("testid", tokenCaptor.getValue().getClientId());
        assertEquals("testsecret", tokenCaptor.getValue().getClientSecret());
        assertEquals("testuser", tokenCaptor.getValue().getUsername());
        assertEquals("pw", tokenCaptor.getValue().getPassword());
        assertEquals("123456", tokenCaptor.getValue().getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

}
