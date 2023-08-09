package de.bund.bva.isyfact.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class IsyOAuth2PasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /** Registration ID of the OAuth2 client to user for password grant authentication. */
    private final String registrationId;

    public IsyOAuth2PasswordAuthenticationToken(String username, String password, String registrationId) {
        super(username, password);
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

}
