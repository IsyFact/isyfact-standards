package de.bund.bva.isyfact.security.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Validated
public class IsySecurityConfigurationProperties {

    /** Name of the JWT claim that contains the roles. */
    private String rolesClaimName = "roles";

    /** Path to the XML file containing the role/privilege mappings. */
    private Resource rolePrivilegesMappingFile = new ClassPathResource("/resources/sicherheit/rollenrechte.xml");

    public String getRolesClaimName() {
        return rolesClaimName;
    }

    public void setRolesClaimName(String rolesClaimName) {
        this.rolesClaimName = rolesClaimName;
    }

    public Resource getRolePrivilegesMappingFile() {
        return rolePrivilegesMappingFile;
    }

    public void setRolePrivilegesMappingFile(Resource rolePrivilegesMappingFile) {
        this.rolePrivilegesMappingFile = rolePrivilegesMappingFile;
    }

}
