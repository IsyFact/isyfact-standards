package de.bund.bva.isyfact.security.core;

import java.util.Optional;
import java.util.Set;

import org.springframework.lang.Nullable;

import de.bund.bva.isyfact.security.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.Berechtigungsmanager;
import de.bund.bva.isyfact.security.Security;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

public class DefaultSecurity implements Security {

    private final RolePrivilegesMapper rolePrivilegesMapper;
    private final Authentifizierungsmanager authentifizierungsmanager;
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
