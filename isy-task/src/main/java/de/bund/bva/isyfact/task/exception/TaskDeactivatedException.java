package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.Ereignisschluessel;

/**
 * This exception is thrown if the task is deactivated.
 */
public class TaskDeactivatedException extends TaskException {

    public TaskDeactivatedException(String taskId) {
        super(Ereignisschluessel.TASK_DEAKTIVIERT, taskId);
    }

    public TaskDeactivatedException(String taskId, Throwable cause) {
        super(Ereignisschluessel.TASK_DEAKTIVIERT, cause, taskId);
    }

}
