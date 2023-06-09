package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Client Credentials Flow authentication.
 */
public class ManualClientCredentialsAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /** Issuer location to use for manual client credentials grant authentication. */
    private final String issuerLocation;

    public ManualClientCredentialsAuthenticationToken(String clientId, String clientSecret, String issuerLocation) {
        super(clientId, clientSecret);
        this.issuerLocation = issuerLocation;
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

}
