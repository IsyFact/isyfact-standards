package de.bund.bva.isyfact.security.config;

import java.util.Map;

import org.springframework.validation.annotation.Validated;


public class JWTConfigurationProperties {

    /** The tenants. */
    private Map<String, JwtServerProperties> tenants;

    public Map<String, JwtServerProperties> getTenants() {
        return tenants;
    }

    public void setTenants(Map<String, JwtServerProperties> tenants) {
        this.tenants = tenants;
    }

    @Validated
    public static class JwtServerProperties {

        /** The issuer URI. */
        private String issuerUri;

        /** The JWK set URI. */
        private String jwkSetUri;

        public String getIssuerUri() {
            return issuerUri;
        }

        public void setIssuerUri(String issuerUri) {
            this.issuerUri = issuerUri;
        }

        public String getJwkSetUri() {
            return jwkSetUri;
        }

        public void setJwkSetUri(String jwkSetUri) {
            this.jwkSetUri = jwkSetUri;
        }
    }
}
