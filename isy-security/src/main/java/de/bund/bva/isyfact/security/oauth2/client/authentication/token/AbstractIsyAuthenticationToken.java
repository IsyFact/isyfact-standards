package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Base class for tokens used by the {@link de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager}.
 */
public abstract class AbstractIsyAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * Principal that indicates the {@link de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager}
     * as the source for Client Credentials authentication.
     */
    protected static final String AUTHENTIFIZIERUNGSMANAGER_PRINCIPAL = "Authentifizierungsmanager";

    /**
     * The principal is used along with the registrationId to cache {@link org.springframework.security.oauth2.client.OAuth2AuthorizedClient}s
     * in the {@link org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager}.
     * In case of Client Credentials authentication it should be set to {@link #AUTHENTIFIZIERUNGSMANAGER_PRINCIPAL}.
     * In case of Password authentication it should be set to the username of the resource owner to be able to
     * differentiate clients for different resource owners.
     */
    private final String principal;

    /** The BHKNZ to send as part of the authentication request (optional). */
    @Nullable
    private final String bhknz;

    protected AbstractIsyAuthenticationToken(String principal, @Nullable String bhknz) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = principal;
        this.bhknz = bhknz;
        setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Nullable
    public String getBhknz() {
        return bhknz;
    }

    @Nullable
    public abstract byte[] generateCacheKey(String hashAlgorithm, byte[] salt);
}
