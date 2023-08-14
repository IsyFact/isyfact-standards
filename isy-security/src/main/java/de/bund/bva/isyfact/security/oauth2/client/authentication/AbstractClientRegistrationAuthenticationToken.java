package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

public class AbstractClientRegistrationAuthenticationToken extends AbstractIsyAuthenticationToken {

    /** Client Registration of the OAuth 2.0 client. */
    private final ClientRegistration clientRegistration;

    public AbstractClientRegistrationAuthenticationToken(String principal, ClientRegistration clientRegistration, @Nullable String bhknz) {
        super(principal, bhknz);
        this.clientRegistration = clientRegistration;
        setAuthenticated(false);
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }
}
