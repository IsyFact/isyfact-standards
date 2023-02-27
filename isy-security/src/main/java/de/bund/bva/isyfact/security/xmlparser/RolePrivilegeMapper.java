package de.bund.bva.isyfact.security.xmlparser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RolePrivilegeMapper {

    private Logger log = LoggerFactory.getLogger(RolePrivilegeMapper.class);
    private RolePrivileges rolePrivileges;

    public RolePrivilegeMapper(String roleMappingXmlFilePath) {
        this.rolePrivileges = getRolePrivileges(roleMappingXmlFilePath);

    }

    public List<String> getPrivilegesByRoles(List<String> roles) {
        return roles.stream()
                .map(this::getPrivilegesByRole)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<String> getAllPrivileges() {
        return Arrays.stream(this.rolePrivileges.getPrivileges())
                .map(Privilege::getPrivilegeId)
                .collect(Collectors.toList());
    }

    public Map<String, List<String>> getRolePrivilegesMap() {
        Map<String, List<String>> result = new HashMap<>();
        for (Role r : this.rolePrivileges.getRoles()) {
            List<String> privilegesForRole = Arrays.stream(r.getPrivileges())
                    .map(Privilege::getPrivilegeId)
                    .collect(Collectors.toList());
            result.put(r.getRoleId(), privilegesForRole);
        }
        return result;
    }

    public String getAnwendungsId() {
        return this.rolePrivileges.getAppId();
    }

    @Override
    public String toString() {
        return "AnwendungsId:" + this.rolePrivileges.getAppId() + "\n" +
                "RollenRechteMapping:" + getRolePrivilegesMap().toString();
    }

    private RolePrivileges getRolePrivileges(String roleMappingXmlFilePath) {
        XmlMapper mapper = new XmlMapper();
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            File f = new File(Objects.requireNonNull(loader.getResource(roleMappingXmlFilePath)).getFile());
            return mapper.readValue(f, RolePrivileges.class);
        } catch (IOException e) {
            log.error("Error loading XML file", e);
            return null;
        }
    }

    private List<String> getPrivilegesByRole(String userRole) {
        Role[] roleArray = rolePrivileges.getRoles();
        if (roleArray == null || roleArray.length == 0) return Collections.emptyList();
        return Arrays.stream(roleArray)
                .filter(role -> role.getRoleId().equals(userRole))
                .map(Role::getPrivileges)
                .flatMap(Arrays::stream)
                .map(Privilege::getPrivilegeId)
                .collect(Collectors.toList());
    }

}
