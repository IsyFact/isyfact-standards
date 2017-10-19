package de.bund.bva.isyfact.task.konfiguration;

import java.time.Duration;
import java.time.LocalDateTime;

import de.bund.bva.isyfact.task.sicherheit.Authenticator;

public class TaskKonfiguration {

    public enum Ausfuehrungsplan {ONCE, FIXED_RATE, FIXED_DELAY}

    private String taskId;

    private Ausfuehrungsplan ausfuehrungsplan;

    private LocalDateTime executionDateTime;

    private Duration initialDelay;

    private Duration fixedRate;

    private Duration fixedDelay;

    private String hostname;

    private Authenticator authenticator;

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setAusfuehrungsplan(Ausfuehrungsplan ausfuehrungsplan) {
        this.ausfuehrungsplan = ausfuehrungsplan;
    }

    public void setExecutionDateTime(LocalDateTime executionDateTime) {
        this.executionDateTime = executionDateTime;
    }

    public void setInitialDelay(Duration initialDelay) {
        this.initialDelay = initialDelay;
    }

    public void setFixedRate(Duration fixedRate) {
        this.fixedRate = fixedRate;
    }

    public void setFixedDelay(Duration fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public String getTaskId() {
        return taskId;
    }

    public Ausfuehrungsplan getAusfuehrungsplan() {
        return ausfuehrungsplan;
    }

    public LocalDateTime getExecutionDateTime() {
        return executionDateTime;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public Duration getFixedRate() {
        return fixedRate;
    }

    public Duration getFixedDelay() {
        return fixedDelay;
    }

    public String getHostname() {
        return hostname;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }
}
