package de.bund.bva.isyfact.security.oauth2.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.autoconfigure.IsyOAuth2ClientAutoConfiguration;
import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfigurationTest;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsAuthorizedClientAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsClientRegistrationAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsRegistrationIdAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.PasswordClientRegistrationAuthenticationToken;

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
    private ClientCredentialsAuthorizedClientAuthenticationProvider clientCredentialsAuthorizedClientAuthenticationProvider;

    @MockBean
    private PasswordClientRegistrationAuthenticationProvider passwordClientRegistrationAuthenticationProvider;

    @MockBean
    private ClientCredentialsClientRegistrationAuthenticationProvider clientCredentialsClientRegistrationAuthenticationProvider;

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

        when(clientCredentialsAuthorizedClientAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(clientCredentialsAuthorizedClientAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(passwordClientRegistrationAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(passwordClientRegistrationAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);

        when(clientCredentialsClientRegistrationAuthenticationProvider.supports(any())).thenCallRealMethod();
        when(clientCredentialsClientRegistrationAuthenticationProvider.authenticate(any(Authentication.class))).thenReturn(mockJwt);
    }

    @Test
    public void testHasAllProviders() {
        assertThat(isyOAuth2AuthenticationProviderManager.getProviders()).containsExactlyInAnyOrder(
                clientCredentialsAuthorizedClientAuthenticationProvider, passwordClientRegistrationAuthenticationProvider,
                clientCredentialsClientRegistrationAuthenticationProvider
        );
    }

    @Test
    public void testAuthWithClientRegistrationCC() {
        authentifizierungsmanager.authentifiziere("cc-client");

        ArgumentCaptor<ClientCredentialsRegistrationIdAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ClientCredentialsRegistrationIdAuthenticationToken.class);
        verify(clientCredentialsAuthorizedClientAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("cc-client", tokenCaptor.getValue().getRegistrationId());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthWithClientRegistrationCCAndBhknz() {
        authentifizierungsmanager.authentifiziere("cc-client-with-bhknz");

        ArgumentCaptor<ClientCredentialsRegistrationIdAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ClientCredentialsRegistrationIdAuthenticationToken.class);
        verify(clientCredentialsAuthorizedClientAuthenticationProvider).authenticate(tokenCaptor.capture());
        assertEquals("cc-client-with-bhknz", tokenCaptor.getValue().getRegistrationId());
        assertEquals("123456", tokenCaptor.getValue().getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthWithClientRegistrationROPC() {
        authentifizierungsmanager.authentifiziere("ropc-client");

        ArgumentCaptor<PasswordClientRegistrationAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordClientRegistrationAuthenticationToken.class);
        verify(passwordClientRegistrationAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordClientRegistrationAuthenticationToken value = tokenCaptor.getValue();
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

        ArgumentCaptor<PasswordClientRegistrationAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordClientRegistrationAuthenticationToken.class);
        verify(passwordClientRegistrationAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordClientRegistrationAuthenticationToken value = tokenCaptor.getValue();
        assertEquals("ropc-client-with-bhknz", value.getClientRegistration().getRegistrationId());
        assertEquals(AuthorizationGrantType.PASSWORD, value.getClientRegistration().getAuthorizationGrantType());
        assertEquals("resource-owner-password-credentials-test-client", value.getClientRegistration().getClientId());
        assertEquals("hypersecretpassword", value.getClientRegistration().getClientSecret());
        assertEquals("testuser-with-bhknz", value.getUsername());
        assertEquals("pw1234", value.getPassword());
        assertEquals("123456", value.getBhknz());

        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthRefreshesIfTokenIsNotOAuth2() {
        Authentication notOAuth2Authentication = new TestingAuthenticationToken("principal", "credentials");
        SecurityContextHolder.getContext().setAuthentication(notOAuth2Authentication);

        authentifizierungsmanager.authentifiziere("cc-client", Duration.ofSeconds(60));

        // refreshed because existing authentication got replaced with the mockJwt
        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthRefreshesIfTokenIsExpired() {
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.MINUTES);
        Jwt token = Mockito.mock(Jwt.class);
        when(token.getExpiresAt()).thenReturn(expiresAt);

        // TODO: Problem: neues Token ist nicht das gleiche wie das Gemockte.
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(token));

        System.out.println("Before: " + SecurityContextHolder.getContext().getAuthentication());

        //
        authentifizierungsmanager.authentifiziere("cc-client", Duration.ofSeconds(60));

        System.out.println("After: " + SecurityContextHolder.getContext().getAuthentication());

        // refreshed because existing authentication got replaced with the mockJwt
        assertEquals(mockJwt, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthDoesNotRefreshIfTokenIsOAuth2AndNotExpired() {
        Duration tokenExpiryDelta = Duration.ofSeconds(60);

        Instant expiresAt = Instant.now().plus(tokenExpiryDelta).plusSeconds(1);
        Jwt token = Mockito.mock(Jwt.class);
        when(token.getExpiresAt()).thenReturn(expiresAt);
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        authentifizierungsmanager.authentifiziere("cc-client", tokenExpiryDelta);

        // not refreshed because authentication stays the same
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAuthClient() {
        authentifizierungsmanager.authentifiziereClient(getIssuer(), "testid", "testsecret");

        ArgumentCaptor<ClientCredentialsClientRegistrationAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(ClientCredentialsClientRegistrationAuthenticationToken.class);
        verify(clientCredentialsClientRegistrationAuthenticationProvider).authenticate(tokenCaptor.capture());
        ClientCredentialsClientRegistrationAuthenticationToken value = tokenCaptor.getValue();
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
    public void testAuthClientThrowsErrorWithInvalidIssuerLocation() {
        String invalidIssuerLocation = "http://localhost:9095/auth/realms/invalid";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authentifizierungsmanager.authentifiziereClient(invalidIssuerLocation, "testid", "testsecret"));

        assertThat(exception.getMessage()).contains(invalidIssuerLocation);
    }

    @Test
    public void testAuthSystem() {
        authentifizierungsmanager.authentifiziereSystem(getIssuer(), "testid", "testsecret", "testuser", "testpw");

        ArgumentCaptor<PasswordClientRegistrationAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordClientRegistrationAuthenticationToken.class);
        verify(passwordClientRegistrationAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordClientRegistrationAuthenticationToken value = tokenCaptor.getValue();
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

        ArgumentCaptor<PasswordClientRegistrationAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(PasswordClientRegistrationAuthenticationToken.class);
        verify(passwordClientRegistrationAuthenticationProvider).authenticate(tokenCaptor.capture());
        PasswordClientRegistrationAuthenticationToken value = tokenCaptor.getValue();
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
