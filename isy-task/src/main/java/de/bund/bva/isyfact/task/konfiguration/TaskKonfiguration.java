package de.bund.bva.isyfact.task.konfiguration;

import de.bund.bva.isyfact.task.sicherheit.Authenticator;

/**
 * Kapselt die zur Ausführung eines Tasks benötigte Konfiguration.
 */
public class TaskKonfiguration {

    private String taskId;

    private String hostname;

    private Authenticator authenticator;

    private Boolean deaktiviert;

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getHostname() {
        return hostname;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public Boolean isDeaktiviert() {
        return deaktiviert;
    }

    public void setDeaktiviert(Boolean deaktiviert) {
        this.deaktiviert = deaktiviert;
    }
}
