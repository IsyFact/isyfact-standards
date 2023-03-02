package de.bund.bva.isyfact.security.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

public class RolePrivilegesMapper {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(RolePrivilegesMapper.class);

    private final String applicationId;

    private final Map<String, Set<String>> rolePrivilegesMap;

    public RolePrivilegesMapper(String roleMappingXmlFilePath) {
        RolePrivileges rolePrivileges = getRolePrivileges(roleMappingXmlFilePath);
        rolePrivilegesMap = mapAndValidateRolePrivileges(rolePrivileges);
        applicationId = rolePrivileges.getApplicationId();
        if (applicationId == null || applicationId.isEmpty()) {
            throw new RolePrivilegesMappingException("Es ist keine AnwendungsId gesetzt");
        }
    }

    public Set<String> getPrivilegesByRoles(Collection<String> roles) {
        return roles.stream()
                .map(rolePrivilegesMap::get)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Set<String> getAllPrivileges() {
        return rolePrivilegesMap.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Map<String, Set<String>> getRolePrivilegesMap() {
        return rolePrivilegesMap;
    }

    public String getApplicationId() {
        return applicationId;
    }

    @Override
    public String toString() {
        return "AnwendungsId: " + applicationId + "\nRollenRechteMapping: " + getRolePrivilegesMap().toString();
    }

    private RolePrivileges getRolePrivileges(String roleMappingXmlFilePath) {
        XmlMapper mapper = new XmlMapper();
        try {
            InputStream roleMappingFile = getClass().getResourceAsStream(roleMappingXmlFilePath);
            return mapper.readValue(roleMappingFile, RolePrivileges.class);
        } catch (IOException e) {
            throw new RolePrivilegesMappingException("Error loading XML file: " + roleMappingXmlFilePath, e);
        }
    }

    private Map<String, Set<String>> mapAndValidateRolePrivileges(RolePrivileges rolePrivileges) {
        Set<String> definedPrivileges = rolePrivileges.getPrivileges().stream().map(privilege1 -> {
            String privilegeId = privilege1.getId();
            if (privilegeId == null || privilegeId.isEmpty()) {
                throw new RolePrivilegesMappingException("Recht ohne RechtId gefunden");
            }
            return privilegeId;
        }).collect(Collectors.toSet());

        Map<String, Set<String>> result = new HashMap<>();
        for (Role role : rolePrivileges.getRoles()) {
            String roleId = role.getId();
            if (roleId == null || roleId.isEmpty()) {
                throw new RolePrivilegesMappingException("ID der Rolle fehlt");
            }
            Set<String> privilegesForRole = new HashSet<>();
            for (Privilege privilege : role.getPrivileges()) {
                String privilegeId = privilege.getId();
                if (privilegeId == null || privilegeId.isEmpty()) {
                    throw new RolePrivilegesMappingException("Recht hat keine ID");
                }
                if (!definedPrivileges.contains(privilegeId)) {
                    throw new RolePrivilegesMappingException("Das Recht mit der ID \"" + privilegeId + "\" ist unbekannt");
                }
                privilegesForRole.add(privilegeId);
            }
            result.put(roleId, privilegesForRole);
        }
        if (result.keySet().isEmpty()) {
            throw new RolePrivilegesMappingException("Es wurde keine einzige Rolle gefunden");
        }
        return result;
    }

}
