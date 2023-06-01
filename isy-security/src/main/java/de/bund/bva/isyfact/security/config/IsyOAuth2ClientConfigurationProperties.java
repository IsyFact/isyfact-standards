package de.bund.bva.isyfact.security.config;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * IsyFact-specific properties to extend the Spring Client Registration.
 */
@Validated
public class IsyOAuth2ClientConfigurationProperties implements InitializingBean {

    /**
     * Used internally for validation only.
     */
    private final OAuth2ClientProperties oAuth2ClientProperties;

    /**
     * Configurable header name for 2FA.
     */
    private String bhknzHeaderName = "x-client-cert-bhknz";

    /**
     * Organisational Unit used in conjunction with bhknz to form the value for bhknzHeaderName.
     */
    private String defaultCertificateOu;

    public IsyOAuth2ClientConfigurationProperties(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    /**
     * Additional (custom) Client Registration attributes.
     * The registrationId (String key of the Map) should match the registrationId a Spring ClientRegistration.
     */
    private final Map<String, IsyClientRegistration> registration = new HashMap<>();

    public Map<String, IsyClientRegistration> getRegistration() {
        return registration;
    }

    public String getBhknzHeaderName() {
        return bhknzHeaderName;
    }

    public void setBhknzHeaderName(String bhknzHeaderName) {
        this.bhknzHeaderName = bhknzHeaderName;
    }

    public String getDefaultCertificateOu() {
        return defaultCertificateOu;
    }

    public void setDefaultCertificateOu(String defaultCertificateOu) {
        this.defaultCertificateOu = defaultCertificateOu;
    }

    @Override
    public void afterPropertiesSet() {
        getRegistration().keySet().forEach(key -> {
            if (!oAuth2ClientProperties.getRegistration().containsKey(key)) {
                throw new IllegalStateException(String.format("A Spring ClientRegistration with the same ID must be registered, ID: %s", key));
            }
        });

        getRegistration().values().stream().filter(value -> value.getBhknz() != null).findAny().ifPresent(e -> {
            if (!StringUtils.hasText(getDefaultCertificateOu())) {
                throw new IllegalStateException("Default certificate OU must not be empty when any BHKNZ is set.");
            }
        });
    }

    /**
     * Additional (custom) Client Registration attributes.
     */
    @Validated
    public static class IsyClientRegistration {

        /**
         * Client Registration username attribute.
         */
        @NotBlank
        private String username;

        /**
         * Client Registration password attribute.
         */
        @NotBlank
        private String password;

        /**
         * Client Registration bhknz attribute.
         */
        private String bhknz;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getBhknz() {
            return bhknz;
        }

        public void setBhknz(String bhknz) {
            this.bhknz = bhknz;
        }
    }
}
