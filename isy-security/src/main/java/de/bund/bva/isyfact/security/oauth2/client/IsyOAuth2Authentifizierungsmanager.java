package de.bund.bva.isyfact.security.oauth2.client;

import java.time.Duration;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties.AdditionalRegistrationProperties;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsClientRegistrationAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsRegistrationIdAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.PasswordClientRegistrationAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.util.IsySecurityTokenUtil;

/**
 * Default implementation of the {@link Authentifizierungsmanager} that should suffice for most use cases.
 * <p>
 * It provides different ways to authorize OAuth 2.0 Clients (via the Client Credentials flow) and
 * System users (representing resource owners) via the Resource Owner Password Credentials flow.
 * <p>
 * The primary way for authentication is {@link #authentifiziere(String)}, which depends on OAuth 2.0 Client Registrations
 * to be configured in the application properties.
 * The other {@code authentifiziereClient}/{@code authentifiziereSystem} methods construct the necessary
 * Client Registration with the provided credentials and issuer location and thus do not depend on any to be
 * configured in the application properties.
 */
public class IsyOAuth2Authentifizierungsmanager implements Authentifizierungsmanager {

    /**
     * ProviderManager configured with supported {@link AuthenticationProvider}s.
     * It is intended behavior that the ProviderManager may not support all authentication methods made available by the methods of
     * this class, in which case an {@link AuthenticationException} is thrown.
     *
     * @see IsyOAuth2Authentifizierungsmanager more details in the class doc
     */
    private final ProviderManager providerManager;

    /** Global isy-security Configuration properties. */
    private final IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps;

    /**
     * Repository containing the OAuth 2.0 client registrations.
     * Used to get the authorization grant type for a client registration ID.
     * Might be {@code null} if there are no configured client registrations in the application.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    public IsyOAuth2Authentifizierungsmanager(ProviderManager providerManager,
                                              IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps,
                                              @Nullable ClientRegistrationRepository clientRegistrationRepository) {
        this.providerManager = providerManager;
        this.isyOAuth2ClientProps = isyOAuth2ClientProps;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void authentifiziere(String oauth2ClientRegistrationId) throws AuthenticationException {
        Authentication unauthenticatedToken = getAuthenticationTokenForRegistrationId(oauth2ClientRegistrationId);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    @Override
    public void authentifiziere(String oauth2ClientRegistrationId, Duration expirationTimeOffset) throws AuthenticationException {
        if (!IsySecurityTokenUtil.hasOAuth2Token() || IsySecurityTokenUtil.hasTokenExpired(expirationTimeOffset)) {
            authentifiziere(oauth2ClientRegistrationId);
        }
    }

    @Override
    @Deprecated
    public void authentifiziereClient(String issuerLocation, String clientId, String clientSecret) throws AuthenticationException {
        authentifiziereClient(issuerLocation, clientId, clientSecret, null);
    }

    @Override
    @Deprecated
    public void authentifiziereClient(String issuerLocation, String clientId, String clientSecret, @Nullable String bhknz) throws AuthenticationException {
        Assert.notNull(issuerLocation, "issuerLocation cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");
        Assert.notNull(clientSecret, "clientSecret cannot be null");

        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(issuerLocation)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        Authentication unauthenticatedToken = new ClientCredentialsClientRegistrationAuthenticationToken(clientRegistration, bhknz);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    @Override
    @Deprecated
    public void authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password) throws AuthenticationException {
        authentifiziereSystem(issuerLocation, clientId, clientSecret, username, password, null);
    }

    @Override
    @Deprecated
    public void authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password, @Nullable String bhknz)
            throws AuthenticationException {
        Assert.notNull(issuerLocation, "issuerLocation cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");
        Assert.notNull(clientSecret, "clientSecret cannot be null");
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");

        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(issuerLocation)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .build();

        Authentication unauthenticatedToken = new PasswordClientRegistrationAuthenticationToken(clientRegistration, username, password, bhknz);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    /**
     * Creates an appropriate authentication token for the authorization grant type configured for the registration ID.
     *
     * @param oauth2ClientRegistrationId
     *         registration ID to create the token for
     * @return an unauthenticated token that can be passed to the provider manager
     */
    private Authentication getAuthenticationTokenForRegistrationId(String oauth2ClientRegistrationId) {
        ClientRegistration clientRegistration = null;
        if (clientRegistrationRepository != null) {
            clientRegistration = clientRegistrationRepository.findByRegistrationId(oauth2ClientRegistrationId);
        }
        Assert.notNull(clientRegistration, "Could not find ClientRegistration with id '" + oauth2ClientRegistrationId + "'");

        // load additional props for this registration ID, can be null
        AdditionalRegistrationProperties props = isyOAuth2ClientProps.getRegistration().get(clientRegistration.getRegistrationId());
        String bhknz = null;
        if (props != null) {
            // the BHKNZ is optional but can be set for CC or ROPC
            bhknz = props.getBhknz();
        }

        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
            return new ClientCredentialsRegistrationIdAuthenticationToken(oauth2ClientRegistrationId, bhknz);
        } else if (AuthorizationGrantType.PASSWORD.equals(grantType)) {
            // ROPC requires the username and password to be set in the additional properties
            if (props != null && props.getUsername() != null && props.getPassword() != null) {
                return new PasswordClientRegistrationAuthenticationToken(clientRegistration, props.getUsername(), props.getPassword(), bhknz);
            } else {
                throw new BadCredentialsException(
                        String.format("No configured credentials (username, password) found for client with registrationId: %s.",
                                clientRegistration.getRegistrationId()));
            }


        } else {
            throw new IllegalArgumentException("The AuthorizationGrantType '" + grantType.getValue() + "' is not supported.");
        }
    }

    /**
     * Tries to authorize the given request with one of the providers configured in the {@link #providerManager} and update
     * the authenticated principal.
     *
     * @param unauthenticatedToken
     *         the authentication request object
     * @throws AuthenticationException
     *         if no provider supports the authentication request or the authentication failed
     */
    private void authenticateAndChangeAuthenticatedPrincipal(Authentication unauthenticatedToken) throws AuthenticationException {
        Authentication authentication = providerManager.authenticate(unauthenticatedToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
