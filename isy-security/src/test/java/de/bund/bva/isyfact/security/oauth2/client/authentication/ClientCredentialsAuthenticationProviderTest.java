package de.bund.bva.isyfact.security.oauth2.client.authentication;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.resetAllRequests;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.DEFAULT_ROLES_CLAIM_NAME;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;

@ActiveProfiles("test-clients")
@SpringBootTest
public class ClientCredentialsAuthenticationProviderTest extends AbstractOidcProviderTest {

    @Autowired
    private ClientCredentialsAuthenticationProvider clientCredentialsAuthenticationProvider;

    @BeforeAll
    public static void setup() {
        registerTestClients();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void shouldGetAuthTokenAndCacheAuthorizedClient() {
        Authentication authentication = clientCredentialsAuthenticationProvider.authenticate(
                new ClientCredentialsAuthenticationToken("cc-client", null));

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

        // a single request was performed to fetch the token
        verify(1, postRequestedFor(urlEqualTo(ISSUER_PATH + "/protocol/openid-connect/token")));
        resetAllRequests();

        // reauth and verify that no new token was fetched because the authorized client was reused
        clientCredentialsAuthenticationProvider.authenticate(new ClientCredentialsAuthenticationToken("cc-client", null));
        verify(0, postRequestedFor(urlEqualTo(ISSUER_PATH + "/protocol/openid-connect/token")));
    }

    @Test
    public void shouldThrowAuthExceptionWithInvalidCredentials() {
        assertThrows(ClientAuthorizationException.class,
                () -> clientCredentialsAuthenticationProvider.authenticate(
                        new ClientCredentialsAuthenticationToken("cc-client-invalid", null)));
    }

    @Test
    public void shouldThrowErrorWithWrongClient() {
        ClientAuthorizationException exception = assertThrows(ClientAuthorizationException.class,
                () -> clientCredentialsAuthenticationProvider.authenticate(
                        new ClientCredentialsAuthenticationToken("ropc-client", null)));

        assertEquals(OAuth2ErrorCodes.INVALID_GRANT, exception.getError().getErrorCode());
        assertEquals("ropc-client", exception.getClientRegistrationId());
    }

    @Test
    public void shouldReturnNullWhenPassingUnsupportedAuthentication() {
        Authentication authRequest = new UsernamePasswordAuthenticationToken("testuser", "pw1234");
        Authentication authentication = clientCredentialsAuthenticationProvider.authenticate(authRequest);

        assertNull(authentication);
    }

}
