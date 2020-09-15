package de.bund.bva.isyfact.ueberwachung.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;

/**
 * Enthält Properties für den NachbarsystemCheck, sowie
 * alle Nachbarsysteme, die mittels des Nachbarsystemchecks überprüft werden.
 */
public class NachbarsystemConfigurationProperties {

    /** Alle konfigurierten Nachbarsysteme. */
    private final Map<String, Nachbarsystem> nachbarsysteme = new HashMap<>();

    /** Properties für die Durchführung des Checks. */
    private final NachbarsystemCheckProperties nachbarsystemCheck = new NachbarsystemCheckProperties();

    public Map<String, Nachbarsystem> getNachbarsysteme() {
        return nachbarsysteme;
    }

    public NachbarsystemCheckProperties getNachbarsystemCheck() {
        return nachbarsystemCheck;
    }

    /** Properties für die Durchführung des Checks. */
    public static class NachbarsystemCheckProperties {

        /** Zeit bis zum Timeout pro Anfrage. Default Value: 3 Sekunden */
        private Duration timeout = Duration.ofSeconds(3);

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }
    }
}
