package de.bund.bva.isyfact.security.authentication;

import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.DEFAULT_ROLES_CLAIM_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.example.IsySecurityTestApplication;
import de.bund.bva.isyfact.security.oauth2.client.authentication.IsyOAuth2PasswordAuthenticationProvider;

@ActiveProfiles("test-clients")
@SpringBootTest(classes = IsySecurityTestApplication.class)
// clear context so WebClient will fetch a fresh access token
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IsyOAuth2PasswordAuthenticationProviderTest extends AbstractOidcProviderTest {

    @Autowired
    private IsyOAuth2PasswordAuthenticationProvider passwordAuthenticationProvider;

    @BeforeAll
    public static void setup() {
        // client with authorization-grant-type=password
        embeddedOidcProvider.addUser("ressource-owner-password-credentials-test-client", "hypersecretpassword",
                "admin", "admin123", Optional.empty(), Collections.singleton("Rolle_A"));
        // client with authorization-grant-type=client_credentials
        embeddedOidcProvider.addClient("client-credentials-test-client", "supersecretpassword", Collections.singleton("Rolle_A"));
    }

    @Test
    public void shouldGetAuthTokenWithPasswordGrantClient() {
        Authentication authentication = passwordAuthenticationProvider.authenticate("admin", "admin123", "ropc-client");

        // security context is still empty
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

        assertInstanceOf(JwtAuthenticationToken.class, authentication);
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        assertEquals("admin", jwtAuth.getTokenAttributes().get(StandardClaimNames.PREFERRED_USERNAME));
        List<String> grantedAuthorityNames = jwtAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(grantedAuthorityNames).containsOnly("PRIV_Recht_A");
        assertThat((List<String>) jwtAuth.getTokenAttributes().get(DEFAULT_ROLES_CLAIM_NAME)).containsOnly("Rolle_A");
    }

    @Test
    public void shouldThrowAuthExceptionWithInvalidCredentials() {
        assertThrows(ClientAuthorizationException.class,
                () -> passwordAuthenticationProvider.authenticate("admin", "wrong", "ropc-client"));
    }

    @Test
    public void shouldThrowErrorWithWrongClient() {
        ClientAuthorizationException exception = assertThrows(ClientAuthorizationException.class,
                () -> passwordAuthenticationProvider.authenticate("admin", "admin123", "testclient"));

        assertEquals(OAuth2ErrorCodes.INVALID_GRANT, exception.getError().getErrorCode());
        assertEquals("testclient", exception.getClientRegistrationId());
    }

    @Test
    public void shouldReturnFalseWhenPassingUnsupportedAuthentication() {
        Authentication authRequest = new UsernamePasswordAuthenticationToken("admin", "admin123");

        assertFalse(passwordAuthenticationProvider.supports(authRequest.getClass()));
    }

}
