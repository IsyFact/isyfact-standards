package de.bund.bva.isyfact.task.konfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.format.InFormat;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationException;
import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;

import static de.bund.bva.isyfact.task.konstanten.HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

public class TaskKonfiguration {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskKonfiguration.class);

    public enum Ausfuehrungsplan {ONCE, FIXED_RATE, FIXED_DELAY}

    private final Konfiguration konfiguration;

    private final AuthenticatorFactory authenticatorFactory;

    public TaskKonfiguration(Konfiguration konfiguration, AuthenticatorFactory authenticatorFactory) {
        this.konfiguration = konfiguration;
        this.authenticatorFactory = authenticatorFactory;
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

    public synchronized String getHostname(String taskId) {
        try {
            return konfiguration.getAsString(PRAEFIX + taskId + HOST);
        } catch (KonfigurationException e) {
            String nachricht = MessageSourceHolder.getMessage(VERWENDE_STANDARD_KONFIGURATION, "hostname");
            LOG.info(LogKategorie.JOURNAL, VERWENDE_STANDARD_KONFIGURATION, nachricht);
        }

        return konfiguration.getAsString(KonfigurationSchluessel.STANDARD_HOST);
    }

    public synchronized Authenticator getSecurityAuthenticator(String taskId) {
        return authenticatorFactory.getSecurityAuthenticator(taskId);
    }
}
