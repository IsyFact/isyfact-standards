package de.bund.bva.isyfact.security.oauth2.client;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordAuthenticationToken;

/**
 * Default implementation of the {@link Authentifizierungsmanager} that should suffice for most use cases.
 * <p>
 * It provides different ways to authorize OAuth 2.0 Clients (via the Client Credentials flow) and
 * System users (representing resource owners) via the Resource Owner Password Credentials flow.
 */
public class IsyOAuth2Authentifizierungsmanager implements Authentifizierungsmanager {

    /**
     * ProviderManager configured with supported {@link AuthenticationProvider}s.
     * It is intended behavior that the ProviderManager may not support all authentication methods made available by the methods of
     * this class, in which case an {@link AuthenticationException} is thrown.
     */
    private final ProviderManager providerManager;

    /**
     * Repository containing the client registrations.
     * Used to get the authorization grant type for a registration ID.
     * Might be {@code null} if there are no configured client registrations in the application.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    public IsyOAuth2Authentifizierungsmanager(ProviderManager providerManager,
                                              @Nullable ClientRegistrationRepository clientRegistrationRepository) {
        this.providerManager = providerManager;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void authentifiziere(String registrationId) throws AuthenticationException {
        ClientRegistration clientRegistration = null;
        if (clientRegistrationRepository != null) {
            clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        }
        Assert.notNull(clientRegistration, "Could not find ClientRegistration with id '" + registrationId + "'");

        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
            ClientCredentialsAuthenticationToken token = new ClientCredentialsAuthenticationToken(registrationId);
            authenticateAndChangeAuthenticatedPrincipal(token);
        } else if (AuthorizationGrantType.PASSWORD.equals(grantType)) {
            PasswordAuthenticationToken token = new PasswordAuthenticationToken(registrationId);
            authenticateAndChangeAuthenticatedPrincipal(token);
        } else {
            throw new IllegalArgumentException("The AuthorizationGrantType '" + grantType.getValue() + "' is not supported.");
        }
    }

    @Override
    public void authentifiziereClient(String clientId, String clientSecret, String issuerLocation) throws AuthenticationException {
        Assert.notNull(clientId, "clientId cannot be null");
        Assert.notNull(clientSecret, "clientSecret cannot be null");
        Assert.notNull(issuerLocation, "issuerLocation cannot be null");

        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(issuerLocation)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        ManualClientCredentialsAuthenticationToken token = new ManualClientCredentialsAuthenticationToken(
                clientRegistration);

        authenticateAndChangeAuthenticatedPrincipal(token);
    }

    @Override
    public void authentifiziereSystem(String registrationId, String username, String password, @Nullable String bhknz)
            throws AuthenticationException {
        Assert.notNull(registrationId, "registrationId cannot be null");
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");

        PasswordAuthenticationToken token = new PasswordAuthenticationToken(registrationId, username, password, bhknz);

        authenticateAndChangeAuthenticatedPrincipal(token);
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
