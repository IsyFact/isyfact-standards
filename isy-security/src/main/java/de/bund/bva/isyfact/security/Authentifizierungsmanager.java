package de.bund.bva.isyfact.security;

import org.springframework.lang.Nullable;

/**
 * Interface to handle the authentication of technical users.
 */
public interface Authentifizierungsmanager {

    /**
     * Authenticate using preconfigred ClientRegistrations.
     * Supports ClientRegistrations with authorization-grant-type of:
     *
     * <li>client_credentials (Client Credentials)</li>
     * <li>password (Resource Owner Password Credentials)</li>
     *
     * @param registrationId identifier of the configured registration.
     */
    void authentifiziere(String registrationId);

    /**
     * SGW use case: client registration is only available at runtime.
     *
     * @param clientId       id of the confidential client
     * @param clientSecret   secret of the confidential client
     * @param issuerLocation url to the issuer IAM system
     */
    void authentifiziereClient(String clientId, String clientSecret, String issuerLocation);

    /**
     * Authenticate a user with credentials only available at runtime.
     *
     * @param registrationId ID of Client Registration with authorization-grant-type: password
     * @param username       User ID
     * @param password       User password
     * @param bhknz          (optional: can be null if not required by the IAM-Service).
     */
    void authentifiziereSystem(String registrationId, String username, String password, @Nullable String bhknz);
}
