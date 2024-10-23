package de.bund.bva.isyfact.security.authentication;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.oauth2.core.oidc.StandardClaimNames.SUB;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import de.bund.bva.isyfact.security.oauth2.util.IsySecurityTokenClaimNames;

public class ClaimsOnlyOAuth2TokenTest {

    @Test
    public void subjectPutInProperClaim() {
        String sub = "test_subject";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub).build();

        assertEquals(sub, token.getSubject());
        assertEquals(claims(entry(SUB, sub)), token.getClaims());
    }

    @Test
    public void loginPutInProperClaim() {
        String sub = "test_subject";
        String login = "test_login";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub).login(login).build();

        assertEquals(sub, token.getSubject());
        assertEquals(claims(entry(SUB, sub), entry(IsySecurityTokenClaimNames.LOGIN, login)), token.getClaims());
    }

    @Test
    public void bhknzPutInProperClaim() {
        String sub = "test_subject";
        String bhknz = "900600";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub).bhknz(bhknz).build();

        assertEquals(sub, token.getSubject());
        assertEquals(claims(entry(SUB, sub), entry(IsySecurityTokenClaimNames.BHKNZ, bhknz)), token.getClaims());
    }

    @Test
    public void userIdPutInProperClaim() {
        String sub = "test_subject";
        String userId = "test_user_id";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub).userId(userId).build();

        assertEquals(sub, token.getSubject());
        assertEquals(claims(entry(SUB, sub), entry(IsySecurityTokenClaimNames.USER_ID, userId)), token.getClaims());
    }

    @Test
    public void displayNamePutInProperClaim() {
        String sub = "test_subject";
        String displayName = "Martha Mustermann";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub).displayName(displayName).build();

        assertEquals(sub, token.getSubject());
        assertEquals(claims(entry(SUB, sub), entry(IsySecurityTokenClaimNames.DISPLAY_NAME, displayName)), token.getClaims());
    }

    @Test
    public void presetClaimCanBeOverwrittenWithNull() {
        String sub = "test_subject";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub)
                .login(null).userId(null).bhknz(null).displayName(null)
                .build();

        assertEquals(claims(
                entry(SUB, sub), entry(IsySecurityTokenClaimNames.LOGIN, null), entry(IsySecurityTokenClaimNames.USER_ID, null),
                entry(IsySecurityTokenClaimNames.BHKNZ, null), entry(IsySecurityTokenClaimNames.DISPLAY_NAME, null)
        ), token.getClaims());
    }

    @Test
    public void setsNamedClaims() {
        String sub = "test_subject";
        String claimName = "my_custom_claim";
        String claimValue = "test_value_for_custom_claim";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub).claim(claimName, claimValue).build();

        assertEquals(sub, token.getSubject());
        assertEquals(claims(entry(SUB, sub), entry(claimName, claimValue)), token.getClaims());
    }

    @Test
    public void getTokenValueThrowsException() {
        assertThrows(RuntimeException.class,
                () -> ClaimsOnlyOAuth2Token.withSubject("test_subject").build().getTokenValue());
    }

    @Test
    public void asAuthenticationCreatesProperObject() {
        String sub = "test_subject";
        ClaimsOnlyOAuth2Token token = ClaimsOnlyOAuth2Token.withSubject(sub)
                .displayName("Martha Mustermann")
                .build();

        AbstractOAuth2TokenAuthenticationToken<?> authentication = token.asAuthentication();

        assertFalse(authentication.isAuthenticated());
        assertEquals(sub, authentication.getName());
        assertEquals(sub, authentication.getPrincipal());
        assertEquals(token, authentication.getCredentials());
        assertEquals(token, authentication.getToken());
        assertEquals(token.getClaims(), authentication.getTokenAttributes());
    }

    @SafeVarargs
    private static Map<String, String> claims(Map.Entry<String, String>... entries) {
        Map<String, String> map = new HashMap<>();
        map.put(IsySecurityTokenClaimNames.LOGIN, "");
        map.put(IsySecurityTokenClaimNames.USER_ID, "");
        map.put(IsySecurityTokenClaimNames.BHKNZ, "");
        map.put(IsySecurityTokenClaimNames.DISPLAY_NAME, "");
        for (Map.Entry<String, String> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
