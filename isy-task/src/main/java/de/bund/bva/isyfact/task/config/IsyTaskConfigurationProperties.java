package de.bund.bva.isyfact.task.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import org.springframework.validation.annotation.Validated;

@Validated
public class IsyTaskConfigurationProperties {

    private final Map<String, TaskConfig> tasks = new HashMap<>();

    private final Default standard = new Default();

    private final Watchdog watchdog = new Watchdog();

    public Map<String, TaskConfig> getTasks() {
        return tasks;
    }

    public Default getDefault() {
        return standard;
    }

    public Watchdog getWatchdog() {
        return watchdog;
    }

    public static class TaskConfig {

        private String benutzer;

        private String passwort;

        private String bhkz;

        private String host;

        private TaskKonfiguration.Ausfuehrungsplan ausfuehrung;

        private String zeitpunkt;

        private Duration initialDelay = Duration.ZERO;

        private Duration fixedRate;

        private Duration fixedDelay;

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

        public TaskKonfiguration.Ausfuehrungsplan getAusfuehrung() {
            return ausfuehrung;
        }

        public void setAusfuehrung(TaskKonfiguration.Ausfuehrungsplan ausfuehrung) {
            this.ausfuehrung = ausfuehrung;
        }

        public String getZeitpunkt() {
            return zeitpunkt;
        }

        public void setZeitpunkt(String zeitpunkt) {
            this.zeitpunkt = zeitpunkt;
        }

        public Duration getInitialDelay() {
            return initialDelay;
        }

        public void setInitialDelay(Duration initialDelay) {
            this.initialDelay = initialDelay;
        }

        public Duration getFixedRate() {
            return fixedRate;
        }

        public void setFixedRate(Duration fixedRate) {
            this.fixedRate = fixedRate;
        }

        public Duration getFixedDelay() {
            return fixedDelay;
        }

        public void setFixedDelay(Duration fixedDelay) {
            this.fixedDelay = fixedDelay;
        }
    }

    public static class Default {
        int amountOfThreads = 100;

        private String dateTimePattern = "dd.MM.yyyy HH:mm:ss.SSS";

        private String benutzer;

        private String passwort;

        private String bhkz;

        private String host;

        @Min(1)
        public int getAmountOfThreads() {
            return amountOfThreads;
        }

        public void setAmountOfThreads(int amountOfThreads) {
            this.amountOfThreads = amountOfThreads;
        }

        @NotBlank
        public String getDateTimePattern() {
            return dateTimePattern;
        }

        public void setDateTimePattern(String dateTimePattern) {
            this.dateTimePattern = dateTimePattern;
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

    public static class Watchdog {
        private Duration restartInterval = Duration.ofSeconds(1);

        public Duration getRestartInterval() {
            return restartInterval;
        }

        public void setRestartInterval(Duration restartInterval) {
            this.restartInterval = restartInterval;
        }
    }
}
