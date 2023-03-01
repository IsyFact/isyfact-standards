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

        /** User name. **/
        private String benutzer;

        /** Password. **/
        private String passwort;

        /** Behoerdenkennzeichen. **/
        private String bhkz;

        /** Host name. **/
        private String host;

        /** Task activation flag. **/
        private Boolean deaktiviert = false;

        public String getBenutzer() {
            return benutzer;
        }

        public void setBenutzer(String benutzer) {
            this.benutzer = benutzer;
        }

        public String getPasswort() {
            return passwort;
        }

        public void setPasswort(String passwort) {
            this.passwort = passwort;
        }

        public String getBhkz() {
            return bhkz;
        }

        public void setBhkz(String bhkz) {
            this.bhkz = bhkz;
        }

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
    }

    public static class Default {

        /** Default User name. **/
        private String benutzer;

        /** Default Password. **/
        private String passwort;

        /** Default Behoerdenkennzeichen. **/
        private String bhkz;


        /** Default host name. **/
        private String host = ".*";

        public String getBenutzer() {
            return benutzer;
        }

        public void setBenutzer(String benutzer) {
            this.benutzer = benutzer;
        }

        public String getPasswort() {
            return passwort;
        }

        public void setPasswort(String passwort) {
            this.passwort = passwort;
        }

        public String getBhkz() {
            return bhkz;
        }

        public void setBhkz(String bhkz) {
            this.bhkz = bhkz;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }

}
