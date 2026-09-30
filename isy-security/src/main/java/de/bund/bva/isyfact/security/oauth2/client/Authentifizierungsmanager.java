package de.bund.bva.isyfact.security.oauth2.client;

import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import de.bund.bva.isyfact.security.config.AdditionalCredentials;
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
     * Attempts to authorize the client for the given {@code oauth2ClientRegistrationId} via its configured OAuth 2.0 Flow
     * using the provided additional credentials.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * The chosen OAuth 2.0 Flow will depend on the authorization grant type configured in the application properties.
     * The currently supported flows are:
     * <ul>
     *     <li>Client Credentials (grant type: client_credentials)</li>
     *     <li>Resource Owner Password Credentials (grant type: password)</li>
     * </ul>
     *
     * @param oauth2ClientRegistrationId
     *         registration ID of the OAuth 2.0 Client to authorize
     * @param credentials
     *         additional credentials to use for authentication, such as username, password, and/or bhknz
     * @throws AuthenticationException
     *         if authentication fails
     * @see AdditionalCredentials
     */
    void authentifiziere(String oauth2ClientRegistrationId, AdditionalCredentials credentials) throws AuthenticationException;

    /**
     * Attempts to authorize a client using the provided {@link ClientRegistration} object via the OAuth 2.0 Client Credentials Flow.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * This method allows authentication with a manually created {@link ClientRegistration} when no registration ID
     * is configured.
     * <p>
     * This method only supports the Client Credentials flow (grant type: client_credentials). For authentication using
     * the Resource Owner Password Credentials flow, use
     * {@link #authentifiziere(ClientRegistration, AdditionalCredentials) authentifiziere with AdditionalCredentials} instead.
     * The registration ID from {@link ClientRegistration} must be unique.
     *
     * @param clientRegistration
     *         the client registration containing all necessary information for authentication
     * @throws AuthenticationException
     *         if authentication fails
     */
    void authentifiziere(ClientRegistration clientRegistration) throws AuthenticationException;

    /**
     * Attempts to authorize a client using the provided {@link ClientRegistration} object and additional credentials
     * via the configured OAuth 2.0 Flow. After successful authentication the authenticated principal in the
     * {@link SecurityContext} will be updated.
     * <p>
     * This method allows authentication with a manually created {@link ClientRegistration} when no registration ID
     * is configured.
     * <p>
     * The chosen OAuth 2.0 Flow will depend on the authorization grant type in the provided client registration.
     * The currently supported flows are:
     * <ul>
     *     <li>Client Credentials (grant type: client_credentials)</li>
     *     <li>Resource Owner Password Credentials (grant type: password)</li>
     * </ul>
     * The registration ID from {@link ClientRegistration} must be unique.
     *
     * @param clientRegistration
     *         the client registration containing all necessary information for authentication
     * @param credentials
     *         additional credentials to use for authentication, such as username, password, and/or bhknz
     * @throws AuthenticationException
     *         if authentication fails
     * @see AdditionalCredentials
     */
    void authentifiziere(ClientRegistration clientRegistration, AdditionalCredentials credentials) throws AuthenticationException;

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
    @Deprecated
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
    @Deprecated
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
     */
    @Deprecated
    void authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password)
            throws AuthenticationException;
}
