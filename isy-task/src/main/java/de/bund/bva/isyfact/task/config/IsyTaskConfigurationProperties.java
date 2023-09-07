package de.bund.bva.isyfact.task.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.annotation.Validated;

@Validated
public class IsyTaskConfigurationProperties {

    /** Tasks Map. **/
    private Map<String, TaskConfig> tasks = new HashMap<>();

    /** Default task configuration. **/
    private final Default standard = new Default();

    public Map<String, TaskConfig> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, TaskConfig> tasks) {
        this.tasks = tasks;
    }

    public Default getDefault() {
        return standard;
    }

    public static class TaskConfig {

        /** OAuth 2.0 Client Registration ID to use for authentication. */
        private String oauth2ClientRegistrationId;

        /** Host name. **/
        private String host;

        /** Task activation flag. **/
        private Boolean deaktiviert = false;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Boolean isDeaktiviert() {
            return deaktiviert;
        }
        public void setDeaktiviert(Boolean deaktiviert) {
            this.deaktiviert = deaktiviert;
        }

        public String getOauth2ClientRegistrationId() {
            return oauth2ClientRegistrationId;
        }

        public void setOauth2ClientRegistrationId(String oauth2ClientRegistrationId) {
            this.oauth2ClientRegistrationId = oauth2ClientRegistrationId;
        }
    }

    public static class Default {

        /** Default OAuth 2.0 Client Registration ID to use for authentication. */
        private String oauth2ClientRegistrationId;

        /** Default host name. **/
        private String host = ".*";

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getOauth2ClientRegistrationId() {
            return oauth2ClientRegistrationId;
        }

        public void setOauth2ClientRegistrationId(String oauth2ClientRegistrationId) {
            this.oauth2ClientRegistrationId = oauth2ClientRegistrationId;
        }
    }

}
