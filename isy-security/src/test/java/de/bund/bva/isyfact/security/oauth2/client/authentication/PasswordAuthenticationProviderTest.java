package de.bund.bva.isyfact.security.oauth2.client.authentication;

import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.BHKNZ_CLAIM_NAME;
import static de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub.DEFAULT_ROLES_CLAIM_NAME;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
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
public class PasswordAuthenticationProviderTest extends AbstractOidcProviderTest {

    @Autowired
    private PasswordAuthenticationProvider passwordAuthenticationProvider;

    @BeforeAll
    public static void setup() {
        registerTestClients();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void shouldGetAuthTokenForUserWithoutBhknz() {
        Authentication authentication = passwordAuthenticationProvider.authenticate(
                new PasswordAuthenticationToken("ropc-client"));

        // security context is still empty
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

        assertInstanceOf(JwtAuthenticationToken.class, authentication);
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        assertEquals("testuser", jwtAuth.getTokenAttributes().get(StandardClaimNames.PREFERRED_USERNAME));
        List<String> grantedAuthorityNames = jwtAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(grantedAuthorityNames).containsOnly("PRIV_Recht_A");
        assertThat((List<String>) jwtAuth.getTokenAttributes().get(DEFAULT_ROLES_CLAIM_NAME)).containsOnly("Rolle_A");
        assertFalse(jwtAuth.getTokenAttributes().containsKey(BHKNZ_CLAIM_NAME));
    }

    @Test
    public void shouldGetAuthTokenForUserWithBhknz() {
        Authentication authentication = passwordAuthenticationProvider.authenticate(
                new PasswordAuthenticationToken("ropc-client-with-bhknz"));

        // security context is still empty
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

        assertInstanceOf(JwtAuthenticationToken.class, authentication);
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        assertEquals("testuser-with-bhknz", jwtAuth.getTokenAttributes().get(StandardClaimNames.PREFERRED_USERNAME));
        List<String> grantedAuthorityNames = jwtAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(grantedAuthorityNames).containsOnly("PRIV_Recht_B");
        assertThat((List<String>) jwtAuth.getTokenAttributes().get(DEFAULT_ROLES_CLAIM_NAME)).containsOnly("Rolle_B");
        assertEquals("123456", jwtAuth.getTokenAttributes().get(BHKNZ_CLAIM_NAME));
    }

    @Test
    public void shouldThrowAuthExceptionWithInvalidCredentials() {
        assertThrows(ClientAuthorizationException.class,
                () -> passwordAuthenticationProvider.authenticate(
                        new PasswordAuthenticationToken("ropc-client-invalid")));
    }

    @Test
    public void shouldThrowErrorWithWrongClient() {
        ClientAuthorizationException exception = assertThrows(ClientAuthorizationException.class,
                () -> passwordAuthenticationProvider.authenticate(
                        new PasswordAuthenticationToken("cc-client-invalid-with-resource-owner")));

        assertEquals(OAuth2ErrorCodes.INVALID_GRANT, exception.getError().getErrorCode());
        assertEquals("cc-client-invalid-with-resource-owner", exception.getClientRegistrationId());
    }

    @Test
    public void shouldThrowErrorWithMissingBhknz() {
        // user that requires a bhknz
        ClientAuthorizationException exception = assertThrows(ClientAuthorizationException.class,
                () -> passwordAuthenticationProvider.authenticate(
                        new PasswordAuthenticationToken("ropc-client-with-bhknz-without-bhknz")));

        assertEquals("invalid_token_response", exception.getError().getErrorCode());
        assertEquals("ropc-client-with-bhknz-without-bhknz", exception.getClientRegistrationId());
    }

    @Test
    public void shouldThrowErrorWithMissingAdditionalProperties() {
        // user without additional properties (username/password/bhknz) set
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> passwordAuthenticationProvider.authenticate(
                        new PasswordAuthenticationToken("ropc-client-without-resource-owner")));

        assertThat(exception.getMessage()).contains("ropc-client-without-resource-owner");
    }

    @Test
    public void shouldReturnNullWhenPassingUnsupportedAuthentication() {
        Authentication authRequest = new UsernamePasswordAuthenticationToken("testuser", "pw1234");
        Authentication authentication = passwordAuthenticationProvider.authenticate(authRequest);

        assertNull(authentication);
    }

}
