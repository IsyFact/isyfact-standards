package de.bund.bva.isyfact.security.config;

import org.springframework.validation.annotation.Validated;

@Validated
public class IsySecurityConfigurationProperties {

    /** Name of the JWT claim that contains the roles. */
    private String rolesClaimName = "roles";

    /** Path to the XML file containing the role to privileges mappings. */
    private String rolePrivilegesMappingFile = "/resources/sicherheit/rollenrechte.xml";

    public String getRolesClaimName() {
        return rolesClaimName;
    }

    public void setRolesClaimName(String rolesClaimName) {
        this.rolesClaimName = rolesClaimName;
    }

    public String getRolePrivilegesMappingFile() {
        return rolePrivilegesMappingFile;
    }

    public void setRolePrivilegesMappingFile(String rolePrivilegesMappingFile) {
        this.rolePrivilegesMappingFile = rolePrivilegesMappingFile;
    }

}
