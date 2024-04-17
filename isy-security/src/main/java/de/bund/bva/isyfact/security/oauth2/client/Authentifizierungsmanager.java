package de.bund.bva.isyfact.security.oauth2.client;

import java.time.Duration;

import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;

/**
 * Provides methods for performing authentication via OAuth 2.0 clients outside of the context of a {@code HttpServletRequest},
 * for example in a scheduled/background thread and/or in the service-tier.
 * <p>
 * The preferred way is to use {@link #authentifiziere(String)} with the client registration ID of an
 * OAuth 2.0 Client Registration configured in the application properties.
 */
public interface Authentifizierungsmanager {

    /**
     * Attempts to authorize the client for the given {@code oauth2ClientRegistrationId} via its configured OAuth 2.0 Flow.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * The chosen OAuth 2.0 Flow will depend on the authorization grant type configured in the application properties.
     * The currently supported flows are:
     * <ul>
     *     <li>Client Credentials (grant type: client_credentials)</li>
     *     <li>Resource Owner Password Credentials (grant type: password)</li>
     * </ul>
     * Additional properties for the Resource Owner Password Credentials Flow will be resolved via the matching
     * registration in {@link IsyOAuth2ClientConfigurationProperties#registration}.
     *
     * @param oauth2ClientRegistrationId
     *         registration ID of the OAuth 2.0 Client to authorize
     * @throws AuthenticationException
     *         if authentication fails
     */
    void authentifiziere(String oauth2ClientRegistrationId) throws AuthenticationException;

    /**
     * Performs authentication for the given {@code oauth2ClientRegistrationId} as described in
     * {@link Authentifizierungsmanager#authentifiziere(String)} if the SecurityContext does not contain
     * an Authentication of type {@link AbstractOAuth2TokenAuthenticationToken} or the token received from the
     * SecurityContext is considered as expired with regard to the {@code expirationTimeOffset}.
     * The {@code expirationTimeOffset} provides a time frame before token expiry during which the token is considered as already expired.
     * <p>
     * For clients configured to use the Client Credentials Flow (grant type: client_credentials), authentication will only reliably
     * occur for an {@code expirationTimeOffset} less than 60 seconds, because Spring will by default skip authentication in the
     * {@link org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider ClientCredentialsOAuth2AuthorizedClientProvider}
     * if the access token provided by the corresponding {@link org.springframework.security.oauth2.client.OAuth2AuthorizedClient OAuth2AuthorizedClient}
     * does not expire within 60 seconds from current time.
     * When using this method for re-authentication with the Client Credentials Flow, this means that a time longer
     * than 60 seconds before the next call of this method could lead to token expiration.
     *
     * @param oauth2ClientRegistrationId
     *         registration ID of the OAuth 2.0 Client to authorize
     * @param expirationTimeOffset
     *         the time frame before expiry during which the token is considered as already expired
     * @throws AuthenticationException
     *         if authentication fails
     * @see Authentifizierungsmanager#authentifiziere(String)
     */
    void authentifiziere(String oauth2ClientRegistrationId, Duration expirationTimeOffset) throws AuthenticationException;

    /**
     * Attempts to create and authorize a client with the given credentials via the OAuth 2.0 Client Credentials Flow.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * This method is only intended to allow OAuth 2.0 Client Authentication in cases where the credentials
     * are obtained externally and cannot be configured in the application properties.
     * If an internal client registration configuration exists, it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @throws AuthenticationException
     *         if authentication fails
     * @see #authentifiziereClient(String, String, String, String)
     */
    void authentifiziereClient(String issuerLocation, String clientId, String clientSecret) throws AuthenticationException;

    /**
     * Attempts to create and authorize a client with the given credentials via the OAuth 2.0 Client Credentials Flow.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * This method is only intended to allow OAuth 2.0 Client Authentication in cases where the credentials
     * are obtained externally and cannot be configured in the application properties.
     * If an internal client registration configuration exists, it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @param bhknz
     *         the BHKNZ to send as part of the authorization request (optional)
     * @throws AuthenticationException
     *         if authentication fails
     * @see #authentifiziereClient(String, String, String)
     */
    void authentifiziereClient(String issuerLocation, String clientId, String clientSecret, @Nullable String bhknz)
            throws AuthenticationException;

    /**
     * Attempts to create and authorize a client with the given credentials via the OAuth 2.0 Resource Owner Password Credentials Flow.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * This method is only intended to allow OAuth 2.0 Client Authentication in cases where the credentials
     * are obtained externally and cannot be configured in the application properties.
     * If an internal client registration configuration exists, it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @param username
     *         the resource owner's username
     * @param password
     *         the resource owner's password
     * @throws AuthenticationException
     *         if authentication fails
     * @see #authentifiziereSystem(String, String, String, String, String, String)
     */
    void authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password)
            throws AuthenticationException;

    /**
     * Attempts to create and authorize a client with the given credentials via the OAuth 2.0 Resource Owner Password Credentials Flow.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * This method is only intended to allow OAuth 2.0 Client Authentication in cases where the credentials
     * are obtained externally and cannot be configured in the application properties.
     * If an internal client registration configuration exists, it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client of the intermediary to authorize the authentication request itself
     * @param username
     *         the resource owner's username
     * @param password
     *         the resource owner's password
     * @param bhknz
     *         the BHKNZ to send as part of the authorization request (optional)
     * @throws AuthenticationException
     *         if authentication fails
     * @see #authentifiziereSystem(String, String, String, String, String)
     */
    void authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password, @Nullable String bhknz)
            throws AuthenticationException;

}
