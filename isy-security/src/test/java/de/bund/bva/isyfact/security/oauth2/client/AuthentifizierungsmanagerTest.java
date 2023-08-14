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
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
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
@ActiveProfiles("test-clients")
@SpringBootTest
public class AuthentifizierungsmanagerTest extends AbstractOidcProviderTest {

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

        mockJwt = mock(JwtAuthenticationToken.class);

        when(clientCredentialsAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(clientCredentialsAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(passwordAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(passwordAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(manualClientCredentialsAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(manualClientCredentialsAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testHasAllProviders() {
        assertThat(isyOAuth2AuthenticationProviderManager.getProviders()).containsExactlyInAnyOrder(
                clientCredentialsAuthenticationProvider, passwordAuthenticationProvider,
                manualClientCredentialsAuthenticationProvider
        );
    }

    @Test
    public void testAuthWithClientRegistrationCC() {
        authentifizierungsmanager.authentifiziere("cc-client");

        ArgumentCaptor<ClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ClientCredentialsAuthenticationToken.class);
        verify(clientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("cc-client", tokenCaptor.getValue().getRegistrationId());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthWithClientRegistrationCCAndBhknz() {
        authentifizierungsmanager.authentifiziere("cc-client-with-bhknz");

        ArgumentCaptor<ClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ClientCredentialsAuthenticationToken.class);
        verify(clientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("cc-client-with-bhknz", tokenCaptor.getValue().getRegistrationId());
        assertEquals("123456", tokenCaptor.getValue().getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthWithClientRegistrationROPC() {
        authentifizierungsmanager.authentifiziere("ropc-client");

        ArgumentCaptor<PasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordAuthenticationToken.class);
        verify(passwordAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordAuthenticationToken value = tokenCaptor.getValue();
        assertEquals("ropc-client", value.getClientRegistration().getRegistrationId());
        assertEquals(AuthorizationGrantType.PASSWORD, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("resource-owner-password-credentials-test-client", value.getClientRegistration().getClientId());
        assertEquals("hypersecretpassword", value.getClientRegistration().getClientSecret());
        assertEquals("testuser", value.getUsername());
        assertEquals("pw1234", value.getPassword());
        assertNull(value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthWithClientRegistrationROPCAndBhknz() {
        authentifizierungsmanager.authentifiziere("ropc-client-with-bhknz");

        ArgumentCaptor<PasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordAuthenticationToken.class);
        verify(passwordAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordAuthenticationToken value = tokenCaptor.getValue();
        assertEquals("ropc-client-with-bhknz", value.getClientRegistration().getRegistrationId());
        assertEquals(AuthorizationGrantType.PASSWORD, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("resource-owner-password-credentials-test-client", value.getClientRegistration().getClientId());
        assertEquals("hypersecretpassword", value.getClientRegistration().getClientSecret());
        assertEquals("testuser", value.getUsername());
        assertEquals("pw1234", value.getPassword());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthClient() {
        authentifizierungsmanager.authentifiziereClient(getIssuer(), "testid", "testsecret");

        ArgumentCaptor<ManualClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ManualClientCredentialsAuthenticationToken.class);
        verify(manualClientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        ManualClientCredentialsAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals(AuthorizationGrantType.CLIENT_CREDENTIALS, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertNull(value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthClientWithBhknz() {
        authentifizierungsmanager.authentifiziereClient(getIssuer(), "testid", "testsecret", "123456");

        ArgumentCaptor<ManualClientCredentialsAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ManualClientCredentialsAuthenticationToken.class);
        verify(manualClientCredentialsAuthenticationProvider).authenticate(tokenCaptor.capture());
        ManualClientCredentialsAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals(AuthorizationGrantType.CLIENT_CREDENTIALS, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthClientThrowsErrorWithInvalidIssuerLocation() {
        String invalidIssuerLocation = "http://localhost:9095/auth/realms/invalid";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziereClient(invalidIssuerLocation, "testid", "testsecret"));

        assertThat(exception.getMessage()).contains(invalidIssuerLocation);
    }

    @Test
    public void testAuthSystem() {
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid", "testsecret", "testuser", "testpw");

        ArgumentCaptor<PasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordAuthenticationToken.class);
        verify(passwordAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals(AuthorizationGrantType.PASSWORD, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertEquals("testuser", value.getUsername());
        assertEquals("testpw", value.getPassword());
        assertNull(value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthSystemWithBhknz() {
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid", "testsecret", "testuser", "testpw", "123456");

        ArgumentCaptor<PasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordAuthenticationToken.class);
        verify(passwordAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordAuthenticationToken value = tokenCaptor.getValue();
        assertEquals(getIssuer(), value.getClientRegistration().getProviderDetails().getIssuerUri());
        assertEquals(AuthorizationGrantType.PASSWORD, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("testid", value.getClientRegistration().getClientId());
        assertEquals("testsecret", value.getClientRegistration().getClientSecret());
        assertEquals("testuser", value.getUsername());
        assertEquals("testpw", value.getPassword());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthSystemThrowsErrorWithInvalidIssuerLocation() {
        String invalidIssuerLocation = "http://localhost:9095/auth/realms/invalid";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziereSystem(invalidIssuerLocation, "testid", "testsecret", "testuser", "testpw"));

        assertThat(exception.getMessage()).contains(invalidIssuerLocation);
    }

    @Test
    public void testAuthViaRegistrationIdFailsIfUnknown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziere("unknown-client"));

        assertThat(exception).hasMessageContaining("unknown-client", "not find");
    }

    @Test
    public void testAuthRegistrationIdFailsIfUnsupportedGrantType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziere("unsupported-client"));

        assertThat(exception).hasMessageContainingAll("unsupported-grant-type", "not supported");
    }

}
