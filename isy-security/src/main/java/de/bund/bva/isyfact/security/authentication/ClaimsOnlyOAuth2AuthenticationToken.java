package de.bund.bva.isyfact.security.authentication;

import java.util.Map;

import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

/**
 * An implementation of an {@link AbstractOAuth2TokenAuthenticationToken} that only contains token attributes (claims)
 * and is by definition never authenticated.
 */
@Transient
public final class ClaimsOnlyOAuth2AuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<ClaimsOnlyOAuth2Token> {

    /** The principal name which is the subject claim of the token. */
    private final String principal;

    /**
     * Constructs a {@code ClaimsOnlyOAuth2AuthenticationToken} using the provided token.
     *
     * @param oauth2Token
     *         the {@link ClaimsOnlyOAuth2Token} to use
     */
    public ClaimsOnlyOAuth2AuthenticationToken(ClaimsOnlyOAuth2Token oauth2Token) {
        super(oauth2Token);
        principal = oauth2Token.getSubject();
    }

    /**
     * The principal name which is the subject claim of the token.
     *
     * @return the principal name
     */
    @Override
    public String getPrincipal() {
        return principal;
    }

    /**
     * Always returns {@code false} because this token does not contain any granted authorities.
     *
     * @return {@code false}
     */
    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return getToken().getClaims();
    }
}
