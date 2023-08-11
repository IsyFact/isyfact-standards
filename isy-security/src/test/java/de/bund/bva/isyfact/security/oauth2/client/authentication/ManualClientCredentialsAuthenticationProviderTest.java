package de.bund.bva.isyfact.security.oauth2.client.authentication;

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
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;

@ActiveProfiles("test-clients")
@SpringBootTest
public class ManualClientCredentialsAuthenticationProviderTest extends AbstractOidcProviderTest {

    @Autowired
    private ManualClientCredentialsAuthenticationProvider manualAuthenticationProvider;

    @BeforeAll
    public static void setup() {
        registerTestClients();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void shouldGetAuthToken() {
        Authentication authentication = manualAuthenticationProvider.authenticate(new ManualClientCredentialsAuthenticationToken(
                "http://localhost:9095/auth/realms/testrealm", "client-credentials-test-client", "supersecretpassword", null));

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
    public void shouldThrowAuthExceptionWithInvalidCredentials() {
        assertThrows(ClientAuthorizationException.class,
                () -> manualAuthenticationProvider.authenticate(new ManualClientCredentialsAuthenticationToken(
                        "http://localhost:9095/auth/realms/testrealm", "client-credentials-test-client", "invalidpassword", null)));
    }

    @Test
    public void shouldThrowErrorWithInvalidIssuerLocation() {
        String invalidIssuerLocation = "http://localhost:9095/auth/realms/invalid";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manualAuthenticationProvider.authenticate(new ManualClientCredentialsAuthenticationToken(
                        invalidIssuerLocation, "client-credentials-test-client", "supersecretpassword", null)));

        assertThat(exception.getMessage()).contains(invalidIssuerLocation);
    }

    @Test
    public void shouldReturnNullWhenPassingUnsupportedAuthentication() {
        Authentication authRequest = new UsernamePasswordAuthenticationToken("testuser", "pw1234");
        Authentication authentication = manualAuthenticationProvider.authenticate(authRequest);

        assertNull(authentication);
    }

}
