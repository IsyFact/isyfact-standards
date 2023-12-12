package de.bund.bva.isyfact.security.core;

import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;

import de.bund.bva.isyfact.security.oauth2.util.IsySecurityTokenUtil;

/**
 * Class for checking privileges in the currently authenticated principal manually if the capabilities provided
 * by Spring Security Method Security are not sufficient.
 */
public interface Berechtigungsmanager {

    /**
     * Returns the privileges that have been granted to the currently authenticated principal.
     *
     * @return the set of granted privileges
     */
    Set<String> getRechte();

    /**
     * Returns whether the given privilege has been granted to the currently authenticated principal.
     *
     * @param recht the privilege that should be checked
     * @return {@code true} if the privilege has been granted, otherwise {@code false}
     */
    boolean hatRecht(String recht);

    /**
     * Checks that the given privilege has been granted to the currently authenticated principal.
     *
     * @param recht the privilege that should be checked
     * @throws AccessDeniedException if the privilege has not been granted
     */
    void pruefeRecht(String recht) throws AccessDeniedException;

    /**
     * Returns all roles that have been assigned to the currently authenticated principal.
     *
     * @return the set of all roles
     */
    Set<String> getRollen();

    /**
     * Retrieves an attribute of the access token if the currently authenticated principal is an OAuth 2.0 token.
     *
     * @param key the key to retrieve given attribute
     * @return the attribute in the access token for the given key, or {@code null}
     * @deprecated in favor of {@link IsySecurityTokenUtil#getTokenAttribute(String)}. This method will be removed in a future release.
     */
    @Deprecated
    @Nullable
    Object getTokenAttribute(String key);

}
