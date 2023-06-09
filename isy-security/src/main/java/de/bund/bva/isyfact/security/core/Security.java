package de.bund.bva.isyfact.security.core;

import java.util.Optional;
import java.util.Set;

import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

/**
 * Provides access to the Authentifizierungsmanager, Berechtigungsmanager and all roles known to the application.
 * Mainly intended as a way to ease migration from the {@code Sicherheit} interface in IsyFact 1 and 2.
 * <p>
 * It is preferable to inject the {@link Authentifizierungsmanager} or {@link Berechtigungsmanager} directly if
 * {@link #getAlleRollen()} is not used.
 */
public interface Security {

    /**
     * Returns the {@link Authentifizierungsmanager}. Consider injecting it directly instead of accessing it via Security.
     * <p>
     * Since the Authentifizierungsmanager depends on registered OAuth 2.0 Clients the {@code Optional} will be empty
     * if no clients have been registered in the application properties.
     *
     * @return Optional of {@link Authentifizierungsmanager} or {@code null} if the system
     *         acts as ressource-server and the {@link Authentifizierungsmanager} is not configured
     */
    Optional<Authentifizierungsmanager> getAuthentifizierungsmanager();

    /**
     * Returns the {@link Berechtigungsmanager}. Consider injecting it directly instead of accessing it via Security.
     *
     * @return the {@link Berechtigungsmanager}
     */
    Berechtigungsmanager getBerechtigungsmanager();

    /**
     * Returns the set of all roles that are known to the application.
     *
     * @return set of known roles
     */
    Set<String> getAlleRollen();

}
