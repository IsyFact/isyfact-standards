package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Client Credentials Flow authentication.
 */
public class ManualClientCredentialsAuthenticationToken extends AbstractClientRegistrationAuthenticationToken {

    /** The BHKNZ to send as part of the authentication request (optional). */
    @Nullable
    private final String bhknz;

    public ManualClientCredentialsAuthenticationToken(ClientRegistration clientRegistration, @Nullable String bhknz) {
        super(AUTHENTIFIZIERUNGSMANAGER_PRINCIPAL, clientRegistration, bhknz);
        this.bhknz = bhknz;
        setAuthenticated(false);
    }

    @Nullable
    public String getBhknz() {
        return bhknz;
    }
}
