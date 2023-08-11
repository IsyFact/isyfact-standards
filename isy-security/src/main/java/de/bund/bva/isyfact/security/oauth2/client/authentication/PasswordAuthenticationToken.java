package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * AuthenticationToken holding parameters for Resource Owner Password Credentials Flow authentication.
 */
public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    /** Registration ID of the OAuth 2.0 client that is configured for password grant authentication. */
    private final String registrationId;

    public PasswordAuthenticationToken(String registrationId) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.registrationId = registrationId;
        setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return "Authentifizierungsmanager"; // TODO this should include the regId?
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
