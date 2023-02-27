package de.bund.bva.isyfact.security.test;

import de.bund.bva.isyfact.security.PrivilegeVoter;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;

public class PrivilegeVoterTest {

    @Test
    public void testVote() {
        GrantedAuthority authority = new SimpleGrantedAuthority("PRIV_test");
        Authentication auth = new BearerTokenAuthentication(
                new DefaultOAuth2AuthenticatedPrincipal(Maps.newHashMap("test", "test"), null),
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "test", Instant.now(), Instant.now().plus(1L, ChronoUnit.SECONDS)),
                Arrays.asList(authority));
        PrivilegeVoter voter = new PrivilegeVoter();
        int result = voter.vote(auth, null, null);
        assert result == ACCESS_GRANTED;
    }

}
