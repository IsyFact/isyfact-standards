package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * AuthenticationToken holding parameters for Resource Owner Password Credentials Flow authentication.
 */
public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    /** Client Registration of the OAuth 2.0 client that is configured for password grant authentication. */
    private final ClientRegistration clientRegistration;

    public PasswordAuthenticationToken(ClientRegistration clientRegistration) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.clientRegistration = clientRegistration;
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

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }
}
