package de.bund.bva.isyfact.security.core;

import java.util.Optional;
import java.util.Set;

import org.springframework.lang.Nullable;

import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

/**
 * Implementation of the {@link Security} interface.
 * <p>
 * Due to it being mainly intended as a way to ease migration from the {@code Sicherheit} interface in IsyFact 1 and 2
 * and having a very defined functionality, applications should not try to provide a different implementation
 */
public final class IsyOAuth2Security implements Security {

    /**
     * Mapper from roles to privileges.
     */
    private final RolePrivilegesMapper rolePrivilegesMapper;

    /**
     * Reference to the optional {@link Authentifizierungsmanager} bean.
     */
    private final Optional<Authentifizierungsmanager> authentifizierungsmanager;

    /**
     * Reference to the {@link Berechtigungsmanager} bean.
     */
    private final Berechtigungsmanager berechtigungsmanager;

    public IsyOAuth2Security(
            RolePrivilegesMapper rolePrivilegesMapper,
            Berechtigungsmanager berechtigungsmanager,
            @Nullable Authentifizierungsmanager authentifizierungsmanager
    ) {
        this.rolePrivilegesMapper = rolePrivilegesMapper;
        this.authentifizierungsmanager = Optional.ofNullable(authentifizierungsmanager);
        this.berechtigungsmanager = berechtigungsmanager;
    }

    @Override
    public Set<String> getAlleRollen() {
        return rolePrivilegesMapper.getRolePrivilegesMap().keySet();
    }

    @Override
    public Optional<Authentifizierungsmanager> getAuthentifizierungsmanager() {
        return authentifizierungsmanager;
    }

    @Override
    public Berechtigungsmanager getBerechtigungsmanager() {
        return berechtigungsmanager;
    }

}
