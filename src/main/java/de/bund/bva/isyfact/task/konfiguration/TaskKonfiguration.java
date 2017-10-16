package de.bund.bva.isyfact.task.konfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.format.InFormat;
import de.bund.bva.isyfact.task.handler.HostHandler;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.isyfact.task.security.SecurityAuthenticatorFactory;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationException;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

public class TaskKonfiguration {

    public enum Ausfuehrungsplan {ONCE, FIXED_RATE, FIXED_DELAY}

    private final Konfiguration konfiguration;

    private final HostHandler hostHandler;

    private final SecurityAuthenticatorFactory securityAuthenticatorFactory;

    public TaskKonfiguration(Konfiguration konfiguration, HostHandler hostHandler,
        SecurityAuthenticatorFactory securityAuthenticatorFactory) {
        this.konfiguration = konfiguration;
        this.hostHandler = hostHandler;
        this.securityAuthenticatorFactory = securityAuthenticatorFactory;
    }

    public synchronized Ausfuehrungsplan getAusfuehrungsplan(String taskId) {
        String ausfuehrungsplan = konfiguration.getAsString(PRAEFIX + taskId + AUSFUEHRUNGSPLAN);
        return ausfuehrungsplan == null ? null : Ausfuehrungsplan.valueOf(ausfuehrungsplan);
    }

    public synchronized LocalDateTime getExecutionDateTime(String taskId) {
        try {
            String executionDateTime = konfiguration.getAsString(PRAEFIX + taskId + ZEITPUNKT);
            DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern(KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
            return LocalDateTime.parse(executionDateTime, dateTimeFormatter);
        } catch (KonfigurationException e) {
            return null;
        }
    }

    public synchronized Duration getInitialDelay(String taskId) {
        return InFormat.parseToDuration(konfiguration.getAsString(PRAEFIX + taskId + INITIAL_DELAY, "0s"));
    }

    public synchronized Duration getFixedDelay(String taskId) {
        return InFormat.parseToDuration(konfiguration.getAsString(PRAEFIX + taskId + FIXED_DELAY, "0s"));
    }

    public synchronized Duration getFixedRate(String taskId) {
        return InFormat.parseToDuration(konfiguration.getAsString(PRAEFIX + taskId + FIXED_RATE, "0s"));
    }

    public synchronized HostHandler getHostHandler() {
        return hostHandler;
    }

    public synchronized SecurityAuthenticator getSecurityAuthenticator(String taskId) {
        return securityAuthenticatorFactory.getSecurityAuthenticator(taskId);
    }
}
