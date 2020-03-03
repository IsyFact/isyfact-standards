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
    private NachbarsystemCheckProperties nachbarsystemCheck = new NachbarsystemCheckProperties();

    public Map<String, Nachbarsystem> getNachbarsysteme() {
        return nachbarsysteme;
    }

    public NachbarsystemCheckProperties getNachbarsystemCheck() {
        return nachbarsystemCheck;
    }

    /** Properties für die Durchführung des Checks. */
    public static class NachbarsystemCheckProperties {
        /** Anzahl der Retries pro Check. Default-Value: 1 */
        private int anzahlRetries = 1;

        /** Zeit bis zum Timeout pro Anfrage. Default Value: 3 Sekunden */
        private Duration timeout = Duration.ofSeconds(3);

        public int getAnzahlRetries() {
            return anzahlRetries;
        }

        public void setAnzahlRetries(int anzahlRetries) {
            this.anzahlRetries = anzahlRetries;
        }

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }
    }
}
