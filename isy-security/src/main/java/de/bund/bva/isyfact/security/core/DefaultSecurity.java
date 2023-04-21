package de.bund.bva.isyfact.security.core;

import java.util.Optional;
import java.util.Set;

import org.springframework.lang.Nullable;

import de.bund.bva.isyfact.security.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.Berechtigungsmanager;
import de.bund.bva.isyfact.security.Security;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

/**
 * Default implementation of the {@link Security} interface provided as bean by AutoConfiguration.
 */
public class DefaultSecurity implements Security {

    /**
     * Mapper from roles to privileges.
     */
    private final RolePrivilegesMapper rolePrivilegesMapper;

    /**
     * Reference to the {@link Authentifizierungsmanager} bean.
     * <p>
     * Can be {@code  null} when the application is used as a resource-server.
     */
    private final Authentifizierungsmanager authentifizierungsmanager;

    /**
     * Reference to the {@link Berechtigungsmanager} bean.
     */
    private final Berechtigungsmanager berechtigungsmanager;

    public DefaultSecurity(
            RolePrivilegesMapper rolePrivilegesMapper,
            Berechtigungsmanager berechtigungsmanager,
            @Nullable Authentifizierungsmanager authentifizierungsmanager
    ) {
        this.rolePrivilegesMapper = rolePrivilegesMapper;
        this.authentifizierungsmanager = authentifizierungsmanager;
        this.berechtigungsmanager = berechtigungsmanager;
    }

    @Override
    public Set<String> getAlleRollen() {
        return rolePrivilegesMapper.getRolePrivilegesMap().keySet();
    }

    @Override
    public Optional<Authentifizierungsmanager> getAuthentifizierungsManager() {
        return Optional.ofNullable(authentifizierungsmanager);
    }

    @Override
    public Berechtigungsmanager getBerechtigungsManager() {
        return berechtigungsmanager;
    }
}
