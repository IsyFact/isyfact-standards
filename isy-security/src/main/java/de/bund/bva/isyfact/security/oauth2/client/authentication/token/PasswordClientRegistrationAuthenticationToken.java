package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Resource Owner Password Credentials Flow authentication.
 */
public class PasswordClientRegistrationAuthenticationToken extends AbstractClientRegistrationAuthenticationToken {

    /** The resource owner's username. */
    private final String username;

    /** The resource owner's password. */
    private final String password;

    public PasswordClientRegistrationAuthenticationToken(ClientRegistration clientRegistration, String username, String password, @Nullable String bhknz) {
        super(username, clientRegistration, bhknz);
        this.username = username;
        this.password = password;
        setAuthenticated(false);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
