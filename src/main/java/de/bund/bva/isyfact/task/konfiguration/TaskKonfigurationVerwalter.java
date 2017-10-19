package de.bund.bva.isyfact.task.konfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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

/**
 * Zentrale Instanz zum Einlesen der Konfiguration von Tasks in Form einer {@link TaskKonfiguration}.
 */
public class TaskKonfigurationVerwalter {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskKonfigurationVerwalter.class);

    private final Konfiguration konfiguration;

    private final AuthenticatorFactory authenticatorFactory;

    /**
     * Erstellt eine neue Instanz eines {@link TaskKonfigurationVerwalter}.
     *
     * @param konfiguration        {@link Konfiguration} aus der die Konfiuration für die Tasks ausgelesen wird.
     * @param authenticatorFactory {@link AuthenticatorFactory} zur Erzeugung von
     *                             {@link Authenticator}-Instanzen für die Tasks.
     */
    public TaskKonfigurationVerwalter(Konfiguration konfiguration, AuthenticatorFactory authenticatorFactory) {
        this.konfiguration = konfiguration;
        this.authenticatorFactory = authenticatorFactory;
    }

    /**
     * Gibt eine {@link TaskKonfiguration} für einen Task zurück.
     *
     * @param taskId die ID des Tasks.
     * @return die {@link TaskKonfiguration} für den Task.
     */
    public synchronized TaskKonfiguration getTaskKonfiguration(String taskId) {
        Objects.requireNonNull(taskId);

        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setTaskId(taskId);
        taskKonfiguration.setAuthenticator(authenticatorFactory.getAuthenticator(taskId));
        taskKonfiguration.setHostname(getHostname(taskId));
        taskKonfiguration.setAusfuehrungsplan(getAusfuehrungsplan(taskId));
        taskKonfiguration.setExecutionDateTime(getExecutionDateTime(taskId));
        taskKonfiguration.setInitialDelay(getInitialDelay(taskId));
        taskKonfiguration.setFixedRate(getFixedRate(taskId));
        taskKonfiguration.setFixedDelay(getFixedDelay(taskId));

        return taskKonfiguration;
    }

    /**
     * Überprüft eine {@link TaskKonfiguration} auf Konsistenz.
     *
     * @param taskKonfiguration die {@link TaskKonfiguration}, die geprüft werden soll
     * @return true, wenn die {@link TaskKonfiguration} konsistent ist, sonst false
     */
    public synchronized boolean isKonfigurationValide(TaskKonfiguration taskKonfiguration) {
        if (taskKonfiguration.getTaskId() == null || taskKonfiguration.getAuthenticator() == null
            || taskKonfiguration.getHostname() == null || taskKonfiguration.getAusfuehrungsplan() == null) {
            return false;
        }

        if (taskKonfiguration.getAusfuehrungsplan().equals(TaskKonfiguration.Ausfuehrungsplan.ONCE)) {
            if (wederExecutionDateTimeNochInitialDelayGesetzt(taskKonfiguration)) {
                return false;
            } else if (executionDateTimeUndInitialDelayGesetzt(taskKonfiguration)) {
                return false;
            }
        } else if (taskKonfiguration.getAusfuehrungsplan().equals(TaskKonfiguration.Ausfuehrungsplan.FIXED_RATE)) {
            if (taskKonfiguration.getFixedRate() == null) {
                return false;
            } else if (fixedRateUndFixedDelayGesetzt(taskKonfiguration)) {
                return false;
            }
        } else if (taskKonfiguration.getAusfuehrungsplan().equals(TaskKonfiguration.Ausfuehrungsplan.FIXED_DELAY)) {
            if (taskKonfiguration.getFixedDelay() == null) {
                return false;
            } else if (fixedRateUndFixedDelayGesetzt(taskKonfiguration)) {
                return false;
            }
        }

        return true;
    }

    private boolean fixedRateUndFixedDelayGesetzt(TaskKonfiguration taskKonfiguration) {
        return taskKonfiguration.getFixedDelay() != null && taskKonfiguration.getFixedRate() != null;
    }

    private boolean executionDateTimeUndInitialDelayGesetzt(TaskKonfiguration taskKonfiguration) {
        return taskKonfiguration.getExecutionDateTime() != null && taskKonfiguration.getInitialDelay() != null;
    }

    private boolean wederExecutionDateTimeNochInitialDelayGesetzt(TaskKonfiguration taskKonfiguration) {
        return taskKonfiguration.getExecutionDateTime() == null && taskKonfiguration.getInitialDelay() == null;
    }

    private TaskKonfiguration.Ausfuehrungsplan getAusfuehrungsplan(String taskId) {
        String ausfuehrungsplan = konfiguration.getAsString(PRAEFIX + taskId + AUSFUEHRUNGSPLAN);
        return ausfuehrungsplan == null ? null : TaskKonfiguration.Ausfuehrungsplan.valueOf(ausfuehrungsplan);
    }

    private LocalDateTime getExecutionDateTime(String taskId) {
        try {
            String executionDateTime = konfiguration.getAsString(PRAEFIX + taskId + ZEITPUNKT);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
            return LocalDateTime.parse(executionDateTime, dateTimeFormatter);
        } catch (KonfigurationException e) {
            return null;
        }
    }

    private Duration getInitialDelay(String taskId) {
        return InFormat.parseToDuration(konfiguration.getAsString(PRAEFIX + taskId + INITIAL_DELAY, "0s"));
    }

    private Duration getFixedDelay(String taskId) {
        return InFormat.parseToDuration(konfiguration.getAsString(PRAEFIX + taskId + FIXED_DELAY, "0s"));
    }

    private Duration getFixedRate(String taskId) {
        return InFormat.parseToDuration(konfiguration.getAsString(PRAEFIX + taskId + FIXED_RATE, "0s"));
    }

    private String getHostname(String taskId) {
        try {
            return konfiguration.getAsString(PRAEFIX + taskId + HOST);
        } catch (KonfigurationException e) {
            String nachricht = MessageSourceHolder.getMessage(VERWENDE_STANDARD_KONFIGURATION, "hostname");
            LOG.info(LogKategorie.JOURNAL, VERWENDE_STANDARD_KONFIGURATION, nachricht);
        }

        return konfiguration.getAsString(KonfigurationSchluessel.STANDARD_HOST);
    }
}
