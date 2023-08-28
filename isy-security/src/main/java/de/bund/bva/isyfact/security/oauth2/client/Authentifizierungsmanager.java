package de.bund.bva.isyfact.security.oauth2.client;

import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;

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
     * Attempts to create and authorize a client with the given credentials via the OAuth 2.0 Client Credentials Flow.
     * After successful authentication the authenticated principal in the {@link SecurityContext} will be updated.
     * <p>
     * This method is only intended to allow OAuth 2.0 Client Authentication in cases where the credentials
     * are obtained externally and cannot be configured in the application properties.
     * If possible it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client to authorize
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client to authorize
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
     * If possible it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client to authorize
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client to authorize
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
     * If possible it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client to authorize
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client to authorize
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
     * If possible it is strongly preferred to use {@link #authentifiziere(String) authentication with a client registration ID} instead.
     *
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @param clientId
     *         Client ID of the OAuth 2.0 Client to authorize
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client to authorize
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
