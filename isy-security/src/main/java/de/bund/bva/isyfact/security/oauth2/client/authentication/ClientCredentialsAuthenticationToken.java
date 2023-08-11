package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * AuthenticationToken holding the registration ID for Client Credentials Flow authentication.
 */
public class ClientCredentialsAuthenticationToken extends AbstractAuthenticationToken {

    /** Registration ID of the OAuth 2.0 client that is configured for client credentials grant authentication. */
    private final String registrationId;

    public ClientCredentialsAuthenticationToken(String registrationId) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.registrationId = registrationId;
        setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return "Authentifizierungsmanager";
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
