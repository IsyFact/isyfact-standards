package de.bund.bva.isyfact.security.authorization;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class Berechtigungsmanager {

    private String rolesClaimName;

    public Berechtigungsmanager(String rolesClaimName) {
        this.rolesClaimName = rolesClaimName;
    }

    public Set<String> getRollen() {
        return new HashSet<>((Collection<String>) getTokenAttribute(rolesClaimName));
    }

    public Set<String> getRechte() {
        Collection<? extends GrantedAuthority> privileges = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return privileges.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public boolean hatRecht(String recht) {
        return getRechte().contains(recht);
    }

    public void pruefeRecht(String recht) {
        if (!hatRecht(recht)) {
            throw new AccessDeniedException(format("Berechtigung %s nicht vorhanden!", recht));
        }
    }

    public Object getTokenAttribute(String key) {
        return ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
                .getTokenAttributes().get(key);
    }
}
