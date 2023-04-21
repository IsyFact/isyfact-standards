package de.bund.bva.isyfact.security;

import java.util.Optional;
import java.util.Set;

/**
 * Entrypoint for interaction with the isy-security module.
 */
public interface Security {

    /**
     * Get a list of all roles stored in the system.
     * @return All roles stored in the RollenRechte-mapping.
     */
    Set<String> getAlleRollen();

    /**
     *
     * @return {@link Authentifizierungsmanager}
     */
    Optional<Authentifizierungsmanager> getAuthentifizierungsManager();

    /**
     *
     * @return {@link Berechtigungsmanager}
     */
    Berechtigungsmanager getBerechtigungsManager();
}
