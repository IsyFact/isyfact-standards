package de.bund.bva.isyfact.security.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.util.Assert;

/**
 * Default implementation of the {@link Berechtigungsmanager} that should suffice for most use cases.
 * <p>
 * It provides access to the privileges of the currently authenticated principal, as well as the roles stored in the {@link #rolesClaimName}
 * if the principal is an OAuth 2.0 token.
 */
public class IsyOAuth2Berechtigungsmanager implements Berechtigungsmanager {

    /** The JWT claim name that contains the roles. */
    private final String rolesClaimName;

    public IsyOAuth2Berechtigungsmanager(String rolesClaimName) {
        this.rolesClaimName = rolesClaimName;
    }

    public Set<String> getRollen() {
        Object tokenRoles = getTokenAttribute(rolesClaimName);
        if (tokenRoles instanceof Collection) {
            return new HashSet<>((Collection<String>) tokenRoles);
        } else {
            return Collections.emptySet();
        }
    }

    public Set<String> getRechte() {
        Collection<? extends GrantedAuthority> privileges = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return privileges.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public boolean hatRecht(String recht) {
        Assert.notNull(recht, "recht cannot be null");
        return getRechte().contains(recht);
    }

    public void pruefeRecht(String recht) throws AccessDeniedException {
        Assert.notNull(recht, "recht cannot be null");
        if (!hatRecht(recht)) {
            throw new AccessDeniedException(String.format("Berechtigung %s nicht vorhanden!", recht));
        }
    }

    public Object getTokenAttribute(String key) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuthentication instanceof AbstractOAuth2TokenAuthenticationToken) {
            return ((AbstractOAuth2TokenAuthenticationToken<?>) currentAuthentication).getTokenAttributes().get(key);
        } else {
            return null;
        }
    }

}
