package de.bund.bva.isyfact.security;

import org.springframework.lang.Nullable;

/**
 * Interface to handle the authentication of technical users.
 */
public interface Authentifizierungsmanager {


    // TODO: check for grant type
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
     * sgw Sonderfall client registration steht zur Laufzeit erst fest
     *
     * @param clientId       id of the confidential client
     * @param clientSecret   secret of the confidential client
     * @param issuerLocation url to the issuer IAM system
     */
    void authentifiziereClient(String clientId, String clientSecret, String issuerLocation);

    void authentifiziereSystem(String registrationId, String username, String password, @Nullable String bhknz);
}
