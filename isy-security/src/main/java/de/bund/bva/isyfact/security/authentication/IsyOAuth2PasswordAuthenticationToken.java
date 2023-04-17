package de.bund.bva.isyfact.security.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class IsyOAuth2PasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * Registration ID of the OAuth2 client to user for password grant authentication.
     */
    private final String registrationId;

    private final String bhknz;

    public IsyOAuth2PasswordAuthenticationToken(String username, String password, String registrationId, @Nullable String bhknz) {
        super(username, password);
        this.registrationId = registrationId;
        this.bhknz = bhknz;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getBhknz() {
        return bhknz;
    }

}
