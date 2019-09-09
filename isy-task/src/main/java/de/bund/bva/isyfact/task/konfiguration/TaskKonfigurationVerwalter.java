package de.bund.bva.isyfact.task.konfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

import static de.bund.bva.isyfact.task.konstanten.HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION;

/**
 * Zentrale Instanz zum Einlesen der Konfiguration von Tasks in Form einer {@link TaskKonfiguration}.
 */
public class TaskKonfigurationVerwalter {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskKonfigurationVerwalter.class);

    private final IsyTaskConfigurationProperties configurationProperties;

    private final AuthenticatorFactory authenticatorFactory;

    /**
     * Erstellt eine neue Instanz eines {@link TaskKonfigurationVerwalter}.
     *
     * @param configurationProperties
     *            {@link IsyTaskConfigurationProperties} aus der die Konfiuration für die Tasks ausgelesen
     *            wird.
     * @param authenticatorFactory
     *            {@link AuthenticatorFactory} zur Erzeugung von {@link Authenticator}-Instanzen für die
     *            Tasks.
     */
    public TaskKonfigurationVerwalter(IsyTaskConfigurationProperties configurationProperties,
        AuthenticatorFactory authenticatorFactory) {
        this.configurationProperties = configurationProperties;
        this.authenticatorFactory = authenticatorFactory;
    }

    /**
     * Gibt eine {@link TaskKonfiguration} für einen Task zurück. Die Task-Konfiguration wird mit
     * {@link TaskKonfigurationVerwalter#pruefeTaskKonfiguration(TaskKonfiguration)} auf Konsistenz geprüft.
     *
     * @param taskId
     *            die ID des Tasks.
     * @return die {@link TaskKonfiguration} für den Task.
     * @throws TaskKonfigurationInvalidException
     *             wenn die Task-Konfiguration ungültige Werte enthält
     */
    public synchronized TaskKonfiguration getTaskKonfiguration(String taskId) {
        Objects.requireNonNull(taskId);

        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setTaskId(taskId);
        taskKonfiguration.setAuthenticator(authenticatorFactory.getAuthenticator(taskId));
        taskKonfiguration.setHostname(getHostname(taskId));
        taskKonfiguration
            .setAusfuehrungsplan(configurationProperties.getTasks().get(taskId).getAusfuehrung());
        taskKonfiguration.setExecutionDateTime(getExecutionDateTime(taskId));
        taskKonfiguration.setInitialDelay(configurationProperties.getTasks().get(taskId).getInitialDelay());
        taskKonfiguration.setFixedRate(configurationProperties.getTasks().get(taskId).getFixedRate());
        taskKonfiguration.setFixedDelay(configurationProperties.getTasks().get(taskId).getFixedDelay());

        pruefeTaskKonfiguration(taskKonfiguration);

        return taskKonfiguration;
    }

    /**
     * Überprüft eine {@link TaskKonfiguration} auf Konsistenz. Ist die Konfiguration nicht valide, wird eine
     * {@link TaskKonfigurationInvalidException} geworfen.
     *
     * @param taskKonfiguration
     *            die {@link TaskKonfiguration}, die geprüft werden soll
     * @throws TaskKonfigurationInvalidException
     *             wenn die Task-Konfiguration ungültige Werte enthält
     */
    public synchronized void pruefeTaskKonfiguration(TaskKonfiguration taskKonfiguration) {
        if (taskKonfiguration.getTaskId() == null || taskKonfiguration.getAuthenticator() == null
            || taskKonfiguration.getHostname() == null || taskKonfiguration.getAusfuehrungsplan() == null) {
            throw new TaskKonfigurationInvalidException(
                "Task-ID, Authenticator, Hostname oder Ausführungsplan ist null");
        }

        if (taskKonfiguration.getAusfuehrungsplan().equals(TaskKonfiguration.Ausfuehrungsplan.ONCE)) {
            if (wederExecutionDateTimeNochInitialDelayGesetzt(taskKonfiguration)) {
                throw new TaskKonfigurationInvalidException(
                    "Weder ExecutionDateTime noch InitialDelay sind gesetzt");
            } else if (executionDateTimeUndInitialDelayGroesserNullGesetzt(taskKonfiguration)) {
                throw new TaskKonfigurationInvalidException(
                    "ExecutionDateTime zusammen mit InitialDelay gesetzt");
            }
        } else if (taskKonfiguration.getAusfuehrungsplan()
            .equals(TaskKonfiguration.Ausfuehrungsplan.FIXED_RATE)
            && taskKonfiguration.getFixedRate() == null) {
            throw new TaskKonfigurationInvalidException("FixedRate ist null");
        } else if (taskKonfiguration.getAusfuehrungsplan()
            .equals(TaskKonfiguration.Ausfuehrungsplan.FIXED_DELAY)
            && taskKonfiguration.getFixedDelay() == null) {
            throw new TaskKonfigurationInvalidException("FixedDelay ist null");
        }
    }

    private boolean executionDateTimeUndInitialDelayGroesserNullGesetzt(TaskKonfiguration taskKonfiguration) {
        return taskKonfiguration.getExecutionDateTime() != null && taskKonfiguration.getInitialDelay() != null
            && !taskKonfiguration.getInitialDelay().equals(Duration.ZERO);
    }

    private boolean wederExecutionDateTimeNochInitialDelayGesetzt(TaskKonfiguration taskKonfiguration) {
        return taskKonfiguration.getExecutionDateTime() == null
            && taskKonfiguration.getInitialDelay() == null;
    }

    private LocalDateTime getExecutionDateTime(String taskId) {
        String executionDateTime = configurationProperties.getTasks().get(taskId).getZeitpunkt();

        if (executionDateTime != null) {
            DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern(KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
            return LocalDateTime.parse(executionDateTime, dateTimeFormatter);
        } else {
            return null;
        }
    }

    private String getHostname(String taskId) {
        String host = configurationProperties.getTasks().get(taskId).getHost();

        if (host == null) {
            String nachricht = MessageSourceHolder.getMessage(VERWENDE_STANDARD_KONFIGURATION, "hostname");
            LOG.info(LogKategorie.JOURNAL, VERWENDE_STANDARD_KONFIGURATION, nachricht);
            host = configurationProperties.getDefault().getHost();
        }

        return host;
    }
}
