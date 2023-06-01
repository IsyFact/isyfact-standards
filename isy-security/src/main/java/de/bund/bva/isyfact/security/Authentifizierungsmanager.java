package de.bund.bva.isyfact.security;

import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

/**
 * Interface for performing manual authentication via OAuth 2.0 clients outside of the context of a {@code HttpServletRequest},
 * e.g. in a scheduled/background thread and/or in the service-tier.
 */
public interface Authentifizierungsmanager {

    /**
     * Attempts to authorize the client for the given {@code registrationId} via its configured OAuth 2.0 Flow.
     * After successful authentication the authenticated principal in the {@link org.springframework.security.core.context.SecurityContext}
     * will be updated.
     * The chosen OAuth 2.0 Flow will depend on the authorization grant type configured in the application properties.
     * The currently supported flows are:
     * <ul>
     *     <li>Client Credentials Flow (grant type: client_credentials)</li>
     *     <li>Resource Owner Password Credentials (grant type: password)</li>
     * </ul>
     *
     * @param registrationId
     *         ID of the OAuth 2.0 Client to authorize
     * @throws AuthenticationException
     *         if authentication fails
     */
    void authentifiziere(String registrationId) throws AuthenticationException;

    /**
     * Attempts to create and authorize a client with the given credentials via the OAuth 2.0 Client Credentials Flow.
     * After successful authentication the authenticated principal in the {@link org.springframework.security.core.context.SecurityContext}
     * will be updated.
     * <p>
     * This method is only intended to ease migration to the Client Credentials Flow.
     * If possible it is preferred to use {@link #authentifiziere(String)} instead.
     *
     * @param clientId
     *         Client ID of the OAuth 2.0 Client to authorize
     * @param clientSecret
     *         Client secret of the OAuth 2.0 Client to authorize
     * @param issuerLocation
     *         Issuer used to query the discovery endpoints and set the token endpoint for authentication
     * @throws AuthenticationException
     *         if authentication fails
     */
    void authentifiziereClient(String clientId, String clientSecret, String issuerLocation) throws AuthenticationException;

    /**
     * Attempts to authorize the client for the given {@code registrationId} and additional credentials
     * via the OAuth 2.0 Resource Owner Password Credentials Flow.
     * After successful authentication the authenticated principal in the {@link org.springframework.security.core.context.SecurityContext}
     * will be updated.
     *
     * @param registrationId
     *         ID of the OAuth 2.0 Client to authorize
     * @param username
     *         the resource owner's username
     * @param password
     *         the resource owner's password
     * @param bhknz
     *         the BHKNZ to send as part of the authorization request (optional)
     * @throws AuthenticationException
     *         if authentication fails
     */
    void authentifiziereSystem(String registrationId, String username, String password, @Nullable String bhknz) throws
            AuthenticationException;

}
