package de.bund.bva.isyfact.security.test;

import de.bund.bva.isyfact.security.AccessDecisionAdapter;
import de.bund.bva.isyfact.security.PrivilegeVoter;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.fail;

public class AccessDecisionAdapterTest {

    private AccessDecisionAdapter adapter;

    @BeforeEach
    public void init() {
        adapter = new AccessDecisionAdapter(new PrivilegeVoter());
    }

    @Test
    public void testAccessDecisionPositive() {
        assertTrue(adapter.check(() -> getDummyAuthentication("PRIV_Recht1"), null).isGranted());
    }

    @Test
    public void testAccessDecisionForeignPrivilege() {
        assertFalse(adapter.check(() -> getDummyAuthentication("ROLE_test"), null).isGranted());
    }

    @Test
    public void testAccessDecisionWrongPrivilege() {
        // TODO create a dummy authentication that doesn't have the right to access given resource
        assertFalse(adapter.check(() -> getDummyAuthentication("PRIV_Recht3"), null).isGranted());
        fail("test needs to be adjusted to simulate user with wrong role"); // TODO remove when test works properly
    }

    private Authentication getDummyAuthentication(String authorityName) {
        GrantedAuthority authority = new SimpleGrantedAuthority(authorityName);
        return new BearerTokenAuthentication(
                new DefaultOAuth2AuthenticatedPrincipal(Maps.newHashMap("test", "test"), null),
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "test", Instant.now(), Instant.now().plus(1L, ChronoUnit.SECONDS)),
                Collections.singletonList(authority));
    }

}
