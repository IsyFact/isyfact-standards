package de.bund.bva.isyfact.security.oauth2.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsClientRegistrationAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.PasswordClientRegistrationAuthenticationToken;

@SpringBootTest
public class AuthentifizierungsmanagerWithoutClientsConfiguredTest extends AbstractOidcProviderTest {

    @MockBean
    private ClientCredentialsClientRegistrationAuthenticationProvider clientCredentialsClientRegistrationAuthenticationProvider;

    @MockBean
    private PasswordClientRegistrationAuthenticationProvider passwordClientRegistrationAuthenticationProvider;

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

        when(clientCredentialsClientRegistrationAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(clientCredentialsClientRegistrationAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(passwordClientRegistrationAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(passwordClientRegistrationAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testHasOnlyClientRegistrationAuthenticationProvider() {
        assertThat(isyOAuth2AuthenticationProviderManager.getProviders())
                .containsOnly(clientCredentialsClientRegistrationAuthenticationProvider, passwordClientRegistrationAuthenticationProvider);
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

        ArgumentCaptor<ClientCredentialsClientRegistrationAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ClientCredentialsClientRegistrationAuthenticationToken.class);
        verify(clientCredentialsClientRegistrationAuthenticationProvider).authenticate(tokenCaptor.capture());
        ClientCredentialsClientRegistrationAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals(AuthorizationGrantType.CLIENT_CREDENTIALS, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthSystem() {
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid", "testsecret", "testuser", "pw", "123456");

        ArgumentCaptor<PasswordClientRegistrationAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordClientRegistrationAuthenticationToken.class);
        verify(passwordClientRegistrationAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordClientRegistrationAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals(AuthorizationGrantType.PASSWORD, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertEquals("testuser", value.getUsername());
        assertEquals("pw", value.getPassword());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

}
