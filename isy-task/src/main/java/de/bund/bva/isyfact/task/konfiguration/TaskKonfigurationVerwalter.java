package de.bund.bva.isyfact.task.konfiguration;

import static de.bund.bva.isyfact.task.konstanten.HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

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
     * @param configurationProperties {@link IsyTaskConfigurationProperties} aus der die Konfiuration für die Tasks ausgelesen wird.
     * @param authenticatorFactory    {@link AuthenticatorFactory} zur Erzeugung von
     *                                {@link Authenticator}-Instanzen für die Tasks.
     */
    public TaskKonfigurationVerwalter(IsyTaskConfigurationProperties configurationProperties, AuthenticatorFactory authenticatorFactory) {
        this.configurationProperties = configurationProperties;
        this.authenticatorFactory = authenticatorFactory;
    }

    /**
     * Gibt eine {@link TaskKonfiguration} für einen Task zurück. Die Task-Konfiguration wird mit
     * {@link TaskKonfigurationVerwalter#pruefeTaskKonfiguration(TaskKonfiguration)} auf Konsistenz geprüft.
     *
     * @param taskId die ID des Tasks.
     * @return die {@link TaskKonfiguration} für den Task.
     * @throws TaskKonfigurationInvalidException wenn die Task-Konfiguration ungültige Werte enthält
     */
    public synchronized TaskKonfiguration getTaskKonfiguration(String taskId) {
        Objects.requireNonNull(taskId);

        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setTaskId(taskId);
        taskKonfiguration.setAuthenticator(authenticatorFactory.getAuthenticator(taskId));
        taskKonfiguration.setHostname(getHostname(taskId));
        taskKonfiguration.setDeaktiviert(isDeactivated(taskId));

        pruefeTaskKonfiguration(taskKonfiguration);

        return taskKonfiguration;
    }

    /**
     * Überprüft eine {@link TaskKonfiguration} auf Konsistenz. Ist die Konfiguration nicht valide, wird eine
     * {@link TaskKonfigurationInvalidException} geworfen.
     *
     * @param taskKonfiguration die {@link TaskKonfiguration}, die geprüft werden soll
     * @throws TaskKonfigurationInvalidException wenn die Task-Konfiguration ungültige Werte enthält
     */
    public synchronized void pruefeTaskKonfiguration(TaskKonfiguration taskKonfiguration) {
        if (taskKonfiguration.getTaskId() == null || taskKonfiguration.getAuthenticator() == null
            || taskKonfiguration.getHostname() == null) {
            throw new TaskKonfigurationInvalidException(
                "Task-ID, Authenticator oder Hostname ist null");
        }

        try {
            Pattern.compile(taskKonfiguration.getHostname());
        } catch (PatternSyntaxException pse) {
            throw new TaskKonfigurationInvalidException(
                "Hostname ist keine gültige Regex");
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

    private Boolean isDeactivated(String taskId){

        Boolean deaktiviert = configurationProperties.getTasks().get(taskId).isDeaktiviert();

        return (deaktiviert)?deaktiviert:false;
    }
}
