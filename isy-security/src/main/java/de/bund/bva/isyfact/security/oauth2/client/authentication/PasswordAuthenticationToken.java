package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * AuthenticationToken holding parameters for Resource Owner Password Credentials Flow authentication.
 */
public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    /** Registration ID of the OAuth 2.0 client that is configured for password grant authentication. */
    private final String registrationId;

    /** The resource owner's username. */
    private final String username;

    /** The resource owner's password. */
    private final String password;

    /** The BHKNZ to send as part of the authentication request (optional). */
    private final String bhknz;

    public PasswordAuthenticationToken(String registrationId) {
        this(registrationId, "", "", null);
    }

    public PasswordAuthenticationToken(String registrationId, String username, String password, @Nullable String bhknz) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.registrationId = registrationId;
        this.username = username;
        this.password = password;
        this.bhknz = bhknz;
        setAuthenticated(false);
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

    @Nullable
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
