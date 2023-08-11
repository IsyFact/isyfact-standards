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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import de.bund.bva.isyfact.security.autoconfigure.IsyOAuth2ClientAutoConfiguration;
import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfigurationTest;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordAuthenticationToken;

/**
 * Tests the Authentifizierungsmanager with all available authentication providers.
 * Because this test defines mock beans it skips the actual bean creation in {@link IsyOAuth2ClientAutoConfiguration} which would
 * only create them if OAuth 2.0 clients are configured. The {@link IsySecurityAutoConfigurationTest} still makes sure that the
 * correct beans are created depending on the application configuration.
 */
@SpringBootTest
public class AuthentifizierungsmanagerTest {

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private ClientCredentialsAuthenticationProvider clientCredentialsAuthenticationProvider;

    @MockBean
    private PasswordAuthenticationProvider passwordAuthenticationProvider;

    @MockBean
    private ManualClientCredentialsAuthenticationProvider manualClientCredentialsAuthenticationProvider;

    @Autowired
    private Authentifizierungsmanager authentifizierungsmanager;

    @Autowired
    private ProviderManager isyOAuth2AuthenticationProviderManager;

    private JwtAuthenticationToken mockJwt;

    @BeforeEach
    public void configureMocks() {
        // clear authenticated principal
        SecurityContextHolder.getContext().setAuthentication(null);

        ClientRegistration ccClient = ClientRegistration.withRegistrationId("mock-cc")
                .clientId("dummy").clientSecret("dummy").tokenUri("dummy")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).build();
        ClientRegistration ropcClient = ClientRegistration.withRegistrationId("mock-ropc")
                .clientId("dummy").clientSecret("dummy").tokenUri("dummy")
                .authorizationGrantType(AuthorizationGrantType.PASSWORD).build();
        ClientRegistration unsupportedClient = ClientRegistration.withRegistrationId("mock-unsupported")
                .clientId("dummy").clientSecret("dummy").tokenUri("dummy")
                .authorizationGrantType(new AuthorizationGrantType("unsupported")).build();
        when(clientRegistrationRepository.findByRegistrationId("mock-cc")).thenReturn(ccClient);
        when(clientRegistrationRepository.findByRegistrationId("mock-ropc")).thenReturn(ropcClient);
        when(clientRegistrationRepository.findByRegistrationId("mock-unsupported")).thenReturn(unsupportedClient);
        when(clientRegistrationRepository.findByRegistrationId("mock-unknown")).thenReturn(null);

        mockJwt = mock(JwtAuthenticationToken.class);

        when(passwordAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(passwordAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(clientCredentialsAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(clientCredentialsAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(manualClientCredentialsAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(manualClientCredentialsAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testHasAllProviders() {
        assertThat(isyOAuth2AuthenticationProviderManager.getProviders()).containsExactlyInAnyOrder(
                clientCredentialsAuthenticationProvider, passwordAuthenticationProvider, manualClientCredentialsAuthenticationProvider);
    }

    @Test
    public void testAuthClientWithRegistrationId() {
        authentifizierungsmanager.authentifiziere("mock-cc");

        ArgumentCaptor<ClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ClientCredentialsAuthenticationToken.class);
        verify(clientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("mock-cc", tokenCaptor.getValue().getRegistrationId());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthClientWithCredentials() {
        authentifizierungsmanager.authentifiziereClient("testid", "testsecret", "http://test/");

        ArgumentCaptor<ManualClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ManualClientCredentialsAuthenticationToken.class);
        verify(manualClientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("testid", tokenCaptor.getValue().getClientId());
        assertEquals("testsecret", tokenCaptor.getValue().getClientSecret());
        assertEquals("http://test/", tokenCaptor.getValue().getIssuerLocation());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthSystemWithRegistrationId() {
        authentifizierungsmanager.authentifiziere("mock-ropc");

        ArgumentCaptor<PasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordAuthenticationToken.class);
        verify(passwordAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("mock-ropc", tokenCaptor.getValue().getRegistrationId());
        assertEquals("", tokenCaptor.getValue().getUsername());
        assertEquals("", tokenCaptor.getValue().getPassword());
        assertNull(tokenCaptor.getValue().getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthSystemWithRegistrationIdAndCredentials() {
        authentifizierungsmanager.authentifiziereSystem("mock-ropc", "testuser", "testpw", "123456");

        ArgumentCaptor<PasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordAuthenticationToken.class);
        verify(passwordAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("mock-ropc", tokenCaptor.getValue().getRegistrationId());
        assertEquals("testuser", tokenCaptor.getValue().getUsername());
        assertEquals("testpw", tokenCaptor.getValue().getPassword());
        assertEquals("123456", tokenCaptor.getValue().getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthViaRegistrationIdFailsIfUnknown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziere("mock-unknown"));

        assertThat(exception).hasMessageContaining("mock-unknown", "not find");
    }

    @Test
    public void testAuthRegistrationIdFailsIfUnsupportedGrantType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziere("mock-unsupported"));

        assertThat(exception).hasMessageContainingAll("unsupported", "not supported");
    }

}
