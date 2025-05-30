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

    /**
     * Properties for caching.
     */
    private CacheProperties cache = new CacheProperties();

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

    public CacheProperties getCache() {
        return cache;
    }

    public void setCache(CacheProperties cache) {
        this.cache = cache;
    }

    /**
     * Properties for caching.
     */
    public static class CacheProperties {

        /**
         * Time to live in seconds. 0 = caching is disabled.
         * Configured time to live must be shorter than validity of token.
         */
        private int ttl;

        /**
         * Max number of cached entries.
         */
        private int maxelements = 10000;

        public int getTtl() {
            return ttl;
        }

        public void setTtl(int ttl) {
            this.ttl = ttl;
        }

        public int getMaxelements() {
            return maxelements;
        }

        public void setMaxelements(int maxelements) {
            this.maxelements = maxelements;
        }
    }
}
