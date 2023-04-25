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

        /** Registration-ID for authentication. **/
        private String registrationId;

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

        public String getRegistrationId() { return registrationId; }

        public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    }

    public static class Default {

        /** Registration-ID for authentication. **/
        private String registrationId;

        /** Default host name. **/
        private String host = ".*";

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getRegistrationId() { return registrationId; }

        public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    }

}
