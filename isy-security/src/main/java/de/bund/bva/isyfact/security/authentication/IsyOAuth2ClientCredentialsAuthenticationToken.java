package de.bund.bva.isyfact.security.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

public class IsyOAuth2ClientCredentialsAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * OAuth2 client registration.
     */
    private ClientRegistration clientRegistration;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param clientRegistration OAuth2 client registration.
     */
    public IsyOAuth2ClientCredentialsAuthenticationToken(ClientRegistration clientRegistration) {
        super(Collections.emptyList());
        this.clientRegistration = clientRegistration;
    }

    @Override
    public Object getPrincipal() {
        return this.clientRegistration.getClientId();
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }
}
