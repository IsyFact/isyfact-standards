package de.bund.bva.isyfact.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class IsyOAuth2ClientCredentialsAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * OAuth2 client registration.
     */
    private String registrationId;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param registrationId Id of OAuth2 client registration.
     */
    public IsyOAuth2ClientCredentialsAuthenticationToken(String registrationId) {
        super(Collections.emptyList());
        this.registrationId = registrationId;
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
