package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.Ereignisschluessel;
import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;

/**
 * This exception is thrown if ...
 */
public class TaskDeactivatedException extends TaskException {

    public TaskDeactivatedException(String taskId) {
        super(Ereignisschluessel.TASK_DEAKTIVIERT, taskId);
    }

    public TaskDeactivatedException(String taskId, Throwable cause) {
        super(Ereignisschluessel.TASK_DEAKTIVIERT, cause, taskId);
    }

}
