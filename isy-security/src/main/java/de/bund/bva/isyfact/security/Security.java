package de.bund.bva.isyfact.security;

import java.util.Optional;
import java.util.Set;

/**
 * Entrypoint for interaction with the isy-security module.
 */
public interface Security {

    /**
     * Get a list of all roles stored in the system.
     *
     * @return All roles stored in the RollenRechte-mapping.
     */
    Set<String> getAlleRollen();

    /**
     * The {@link Authentifizierungsmanager} allows manual authentication of clients.
     *
     * @return Optional of {@link Authentifizierungsmanager} or {@code null if the system
     * acts as ressource-server and the {@link Authentifizierungsmanager} is not configured
     */
    Optional<Authentifizierungsmanager> getAuthentifizierungsManager();

    /**
     * The {@link Berechtigungsmanager} allows checking privileges in the currently authenticated security context.
     *
     * @return {@link Berechtigungsmanager}
     */
    Berechtigungsmanager getBerechtigungsManager();
}
