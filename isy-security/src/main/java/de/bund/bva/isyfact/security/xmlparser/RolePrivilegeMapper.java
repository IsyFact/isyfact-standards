package de.bund.bva.isyfact.security.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

public class RolePrivilegeMapper {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(RolePrivilegeMapper.class);

    private final RolePrivileges rolePrivileges;

    public RolePrivilegeMapper(String roleMappingXmlFilePath) {
        rolePrivileges = getRolePrivileges(roleMappingXmlFilePath);
    }

    public List<String> getPrivilegesByRoles(Collection<String> roles) {
        return roles.stream()
                .map(this::getPrivilegesByRole)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<String> getAllPrivileges() {
        return rolePrivileges.getPrivileges().stream()
                .map(Privilege::getId)
                .collect(Collectors.toList());
    }

    public Map<String, List<String>> getRolePrivilegesMap() {
        Map<String, List<String>> result = new HashMap<>();
        for (Role r : rolePrivileges.getRoles()) {
            List<String> privilegesForRole = r.getPrivileges().stream()
                    .map(Privilege::getId)
                    .collect(Collectors.toList());
            result.put(r.getId(), privilegesForRole);
        }
        return result;
    }

    public String getAnwendungsId() {
        return rolePrivileges.getAppId();
    }

    @Override
    public String toString() {
        return "AnwendungsId:" + rolePrivileges.getAppId() + "\n" +
                "RollenRechteMapping:" + getRolePrivilegesMap().toString();
    }

    private RolePrivileges getRolePrivileges(String roleMappingXmlFilePath) {
        XmlMapper mapper = new XmlMapper();
        try {
            InputStream roleMappingFile = getClass().getResourceAsStream(roleMappingXmlFilePath);
            return mapper.readValue(roleMappingFile, RolePrivileges.class);
        } catch (IOException e) {
            LOG.error(null, "Error loading XML file", e);
            return null;
        }
    }

    private List<String> getPrivilegesByRole(String userRole) {
        return rolePrivileges.getRoles().stream()
                .filter(role -> role.getId().equals(userRole))
                .map(Role::getPrivileges)
                .flatMap(Collection::stream)
                .map(Privilege::getId)
                .collect(Collectors.toList());
    }

}
