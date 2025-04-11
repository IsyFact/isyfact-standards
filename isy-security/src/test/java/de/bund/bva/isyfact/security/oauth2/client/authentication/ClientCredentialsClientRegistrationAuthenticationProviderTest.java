package de.bund.bva.isyfact.security.oauth2.client.authentication;

import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.DEFAULT_ROLES_CLAIM_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsClientRegistrationAuthenticationToken;

@ActiveProfiles("test-clients")
@SpringBootTest
public class ClientCredentialsClientRegistrationAuthenticationProviderTest extends AbstractOidcProviderTest {

    @Autowired
    private ClientCredentialsClientRegistrationAuthenticationProvider authenticationProvider;

    @BeforeAll
    public static void setup() {
        registerTestClients();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void shouldGetAuthToken() {
        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation("http://localhost:9095/auth/realms/testrealm")
                .clientId("client-credentials-test-client")
                .clientSecret("supersecretpassword")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        Authentication authentication = authenticationProvider.authenticate(new ClientCredentialsClientRegistrationAuthenticationToken(
                clientRegistration, null));

        // security context is still empty
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

        assertInstanceOf(JwtAuthenticationToken.class, authentication);
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        assertEquals("service-account-client-credentials-test-client",
                jwtAuth.getTokenAttributes().get(StandardClaimNames.PREFERRED_USERNAME));
        List<String> grantedAuthorityNames = jwtAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(grantedAuthorityNames).containsOnly("PRIV_Recht_A");
        assertThat((List<String>) jwtAuth.getTokenAttributes().get(DEFAULT_ROLES_CLAIM_NAME)).containsOnly("Rolle_A");
    }

    @Test
    public void shouldGetAuthTokenTokenUri() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("dummy-unused")
                .tokenUri("http://localhost:9095/auth/realms/testrealm/protocol/openid-connect/token")
                .jwkSetUri("http://localhost:9095/auth/realms/testrealm/protocol/openid-connect/certs")
                .clientId("client-credentials-test-client")
                .clientSecret("supersecretpassword")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        Authentication authentication = authenticationProvider.authenticate(new ClientCredentialsClientRegistrationAuthenticationToken(
                clientRegistration, null));

        // security context is still empty
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        assertInstanceOf(JwtAuthenticationToken.class, authentication);
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        assertEquals("service-account-client-credentials-test-client",
                jwtAuth.getTokenAttributes().get(StandardClaimNames.PREFERRED_USERNAME));
        List<String> grantedAuthorityNames = jwtAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(grantedAuthorityNames).containsOnly("PRIV_Recht_A");
        assertThat((List<String>) jwtAuth.getTokenAttributes().get(DEFAULT_ROLES_CLAIM_NAME)).containsOnly("Rolle_A");
    }

    @Test
    public void shouldThrowAuthExceptionWithInvalidCredentials() {
        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation("http://localhost:9095/auth/realms/testrealm")
                .clientId("client-credentials-test-client")
                .clientSecret("invalidpassword")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        assertThrows(ClientAuthorizationException.class,
                () -> authenticationProvider.authenticate(new ClientCredentialsClientRegistrationAuthenticationToken(
                        clientRegistration, null)));
    }

    @Test
    public void shouldReturnNullWhenPassingUnsupportedAuthentication() {
        Authentication authRequest = new UsernamePasswordAuthenticationToken("testuser", "pw1234");
        Authentication authentication = authenticationProvider.authenticate(authRequest);

        assertNull(authentication);
    }

}
