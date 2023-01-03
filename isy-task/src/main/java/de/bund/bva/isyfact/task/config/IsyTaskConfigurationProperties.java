package de.bund.bva.isyfact.task.config;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public class IsyTaskConfigurationProperties {

    private final Map<String, TaskConfig> tasks = new HashMap<>();

    private final Default standard = new Default();

    public Map<String, TaskConfig> getTasks() {
        return tasks;
    }

    public Default getDefault() {
        return standard;
    }

    public static class TaskConfig {

        private String benutzer;

        private String passwort;

        private String bhkz;

        private String host;

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

        private String benutzer;

        private String passwort;

        private String bhkz;

        private String host;

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
