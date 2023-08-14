package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;

/**
 * AuthenticationToken holding the registration ID for Client Credentials Flow authentication.
 */
public class ClientCredentialsAuthenticationToken extends AbstractIsyAuthenticationToken {

    /** Registration ID of the OAuth 2.0 client that is configured for client credentials grant authentication. */
    private final String registrationId;

    public ClientCredentialsAuthenticationToken(String registrationId, @Nullable String bhknz) {
        super(AUTHENTIFIZIERUNGSMANAGER_PRINCIPAL, bhknz);
        this.registrationId = registrationId;
        setAuthenticated(false);
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
