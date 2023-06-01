package de.bund.bva.isyfact.security.oauth2.client.authentication;

import java.util.Collections;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * AuthenticationToken holding Parameters for resource-owner-password-credentials flow authentication.
 */
public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * Registration ID of the OAuth2 client to user for password grant authentication.
     */
    private final String registrationId;

    /**
     * Username for resource-owner-password-credentials flow.
     */
    private final String username;

    /**
     * Password for resource-owner-password-credentials flow.
     */
    private final String password;

    /**
     * BHKNZ for optional two-factor authentication.
     */
    private final String bhknz;

    public PasswordAuthenticationToken(String registrationId) {
        super(Collections.emptyList());
        this.registrationId = registrationId;
        this.username = "";
        this.password = "";
        this.bhknz = null;
    }

    public PasswordAuthenticationToken(String registrationId, String username, String password, @Nullable String bhknz) {
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
