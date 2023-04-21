package de.bund.bva.isyfact.security.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * AuthenticationToken holding a clientRegistrationId.
 */
public class IsyOAuth2ClientCredentialsAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * OAuth2 client registration.
     */
    private final String registrationId;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param registrationId ID of OAuth2 client registration.
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
