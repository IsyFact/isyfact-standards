package de.bund.bva.isyfact.security.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

public class RolePrivilegesMapper {

    /**
     * Logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(RolePrivilegesMapper.class);

    private String applicationId = "";

    private Map<String, Set<String>> rolePrivilegesMap = new HashMap<>();

    public RolePrivilegesMapper(Resource roleMappingXmlResource) {
        if (roleMappingXmlResource.exists()) {
            RolePrivileges rolePrivileges = getRolePrivileges(roleMappingXmlResource);
            rolePrivilegesMap = mapAndValidateRolePrivileges(rolePrivileges);

            applicationId = rolePrivileges.getApplicationId();
            if (applicationId == null || applicationId.isEmpty()) {
                throw new RolePrivilegesMappingException("Es ist keine AnwendungsId gesetzt");
            }
        }
    }

    public Set<String> getPrivilegesByRoles(Collection<String> roles) {
        return roles.stream()
                .map(rolePrivilegesMap::get)
                .filter(Objects::nonNull)
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

    private RolePrivileges getRolePrivileges(Resource roleMappingXmlResource) {
        XmlMapper mapper = new XmlMapper();
        try (InputStream roleMappingFile = roleMappingXmlResource.getInputStream()) {
            return mapper.readValue(roleMappingFile, RolePrivileges.class);
        } catch (IOException e) {
            throw new RolePrivilegesMappingException("Error loading role privileges mapping file: " + roleMappingXmlResource.getFilename(), e);
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
