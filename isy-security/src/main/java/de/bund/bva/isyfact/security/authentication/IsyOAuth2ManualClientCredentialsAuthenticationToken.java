package de.bund.bva.isyfact.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Collections;

public class IsyOAuth2ManualClientCredentialsAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * OAuth2 client registration.
     */
    private ClientRegistration clientRegistration;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param clientRegistration OAuth2 client registration.
     */
    public IsyOAuth2ManualClientCredentialsAuthenticationToken(ClientRegistration clientRegistration) {
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
