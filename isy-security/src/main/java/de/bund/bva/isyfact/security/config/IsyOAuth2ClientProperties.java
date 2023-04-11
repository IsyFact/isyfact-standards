package de.bund.bva.isyfact.security.config;

import java.util.HashMap;
import java.util.Map;

/**
 * IsyFact-specific properties to extend the Spring Client Registration.
 */
public class IsyOAuth2ClientProperties {

    /**
     * Additional (custom) Client Registration attributes.
     * The registrationId (String key of the Map) should match the registrationId a Spring ClientRegistration.
     */
    private final Map<String, IsyClientRegistration> registration = new HashMap<>();

    public Map<String, IsyClientRegistration> getRegistration() {
        return registration;
    }

    /**
     * Additional (custom) Client Registration attributes.
     */
    public static class IsyClientRegistration {

        /**
         * Client Registration username attribute.
         */
        private String username;

        /**
         * Client Registration password attribute.
         */
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
