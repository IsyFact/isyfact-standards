package de.bund.bva.isyfact.security.config;

import org.springframework.validation.annotation.Validated;

@Validated
public class IsySecurityConfigurationProperties {

    /** Name of the JWT claim that contains the roles. */
    private String rolesClaimName = "roles";

    public String getRolesClaimName() {
        return rolesClaimName;
    }

    public void setRolesClaimName(String rolesClaimName) {
        this.rolesClaimName = rolesClaimName;
    }

}
