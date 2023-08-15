package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Client Credentials Flow authentication.
 */
public class ClientCredentialsClientRegistrationAuthenticationToken extends AbstractClientRegistrationAuthenticationToken {

    public ClientCredentialsClientRegistrationAuthenticationToken(ClientRegistration clientRegistration, @Nullable String bhknz) {
        super(AUTHENTIFIZIERUNGSMANAGER_PRINCIPAL, clientRegistration, bhknz);
        setAuthenticated(false);
    }
}
