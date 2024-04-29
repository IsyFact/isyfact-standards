package de.bund.bva.isyfact.polling.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public class IsyPollingProperties {

    private final Jmx jmx = new Jmx();

    private final Map<String, Cluster> cluster = new HashMap<>();

    public Jmx getJmx() {
        return jmx;
    }

    public Map<String, Cluster> getCluster() {
        return cluster;
    }


    public static class Jmx {

        private final Map<String, Verbindung> verbindungen = new HashMap<>();

        private String domain = "de.bund.bva.isyfact.polling";

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public Map<String, Verbindung> getVerbindungen() {
            return verbindungen;
        }


        public static class Verbindung {
            private String host;

            private int port;

            private String benutzer;

            private String passwort;

            @NotBlank
            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            @Min(0)
            @Max(65535)
            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }

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
        }
    }

    public static class Cluster {
        private final List<String> jmxverbindungen = new ArrayList<>();

        private String name;

        private int wartezeit;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWartezeit() {
            return wartezeit;
        }

        public void setWartezeit(int wartezeit) {
            this.wartezeit = wartezeit;
        }

        public List<String> getJmxverbindungen() {
            return jmxverbindungen;
        }

        static class VerbindungsId {
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
