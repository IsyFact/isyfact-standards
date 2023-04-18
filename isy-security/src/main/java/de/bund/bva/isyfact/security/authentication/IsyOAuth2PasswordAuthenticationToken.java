package de.bund.bva.isyfact.security.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class IsyOAuth2PasswordAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * Registration ID of the OAuth2 client to user for password grant authentication.
     */
    private final String registrationId;

    private final String username;

    private final String password;

    private final String bhknz;

    public IsyOAuth2PasswordAuthenticationToken(String registrationId) {
        super(Collections.emptyList());
        this.username = "";
        this.password = "";
        this.registrationId = registrationId;
        this.bhknz = null;
    }

    public IsyOAuth2PasswordAuthenticationToken(String username, String password, String registrationId, @Nullable String bhknz) {
        super(Collections.emptyList());
        this.registrationId = registrationId;
        this.username = username;
        this.password = password;
        this.bhknz = bhknz;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBhknz() {
        return bhknz;
    }

    @Override
    public Object getCredentials() {
        return getPassword();
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
