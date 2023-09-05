package de.bund.bva.isyfact.security.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Provides additional (custom) properties to clients defined in {@link OAuth2ClientProperties}. Native support might be added
 * to Spring Security in the <a href="https://github.com/spring-projects/spring-security/issues/9669">future</a>.
 */
@Validated
public class IsyOAuth2ClientConfigurationProperties implements InitializingBean {

    /** Spring's OAuth 2.0 client properties for validating registration IDs. */
    @Nullable
    private final OAuth2ClientProperties springOAuth2ClientProperties;

    /**
     * Additional properties for Spring OAuth 2.0 client registrations.
     * The registrationId (String key of the Map) must match the registrationId in the corresponding Spring OAuth 2.0 client properties.
     */
    private final Map<String, AdditionalRegistrationProperties> registration = new HashMap<>();

    /** Configurable header name for 2FA. */
    private String bhknzHeaderName = "x-client-cert-bhknz";

    /** Organisational Unit used in conjunction with bhknz to form the value for bhknzHeaderName. */
    private String defaultCertificateOu;

    public IsyOAuth2ClientConfigurationProperties(@Nullable OAuth2ClientProperties springOAuth2ClientProperties) {
        this.springOAuth2ClientProperties = springOAuth2ClientProperties;
    }

    public Map<String, AdditionalRegistrationProperties> getRegistration() {
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
        if (springOAuth2ClientProperties != null) {
            // verify that registration IDs match those in Spring if there are configured OAuth 2.0 Client Registrations
            getRegistration().keySet().forEach(key -> {
                if (!springOAuth2ClientProperties.getRegistration().containsKey(key)) {
                    throw new IllegalStateException(
                            String.format("A Spring ClientRegistration with the same ID must be registered, ID: %s", key));
                }
            });
        }

        getRegistration().values().stream().filter(value -> value.getBhknz() != null).findAny().ifPresent(e -> {
            if (!StringUtils.hasText(getDefaultCertificateOu())) {
                throw new IllegalStateException("Default certificate OU must not be empty when any BHKNZ is set.");
            }
        });
    }

    /**
     * Additional (custom) properties for a single client registration.
     */
    @Validated
    public static class AdditionalRegistrationProperties {

        /** The resource owner's username (optional). */
        @Nullable
        private String username;

        /** The resource owner's password (optional). */
        @Nullable
        private String password;

        /** The BHKNZ to send as part of the authentication request (optional). */
        @Nullable
        private String bhknz;

        @Nullable
        public String getUsername() {
            return username;
        }

        public void setUsername(@Nullable String username) {
            this.username = username;
        }

        @Nullable
        public String getPassword() {
            return password;
        }

        public void setPassword(@Nullable String password) {
            this.password = password;
        }

        @Nullable
        public String getBhknz() {
            return bhknz;
        }

        public void setBhknz(@Nullable String bhknz) {
            this.bhknz = bhknz;
        }

    }

}
