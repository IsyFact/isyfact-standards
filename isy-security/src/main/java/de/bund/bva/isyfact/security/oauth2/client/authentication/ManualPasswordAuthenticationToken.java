package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Client Credentials Flow authentication.
 */
public class ManualPasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /** Issuer location to use for manual client credentials grant authentication. */
    private final String issuerLocation;

    /** The resource owner's username. */
    private final String username;

    /** The resource owner's password. */
    private final String password;

    /** The BHKNZ to send as part of the authentication request (optional). */
    @Nullable
    private final String bhknz;

    public ManualPasswordAuthenticationToken(String issuerLocation, String clientId, String clientSecret, String username, String password, @Nullable String bhknz) {
        super(clientId, clientSecret);
        this.issuerLocation = issuerLocation;
        this.username = username;
        this.password = password;
        this.bhknz = bhknz;
        setAuthenticated(false);
    }

    public String getIssuerLocation() {
        return issuerLocation;
    }

    public String getClientId() {
        return getPrincipal().toString();
    }

    public String getClientSecret() {
        return getCredentials().toString();
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
}
