package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;

public class TaskKonfigurationInvalidException extends TaskRuntimeException {

    public TaskKonfigurationInvalidException(String taskId, String nachricht) {
        super(FehlerSchluessel.TASK_KONFIGURATION_UNGUELTIG, taskId, nachricht);
    }

}
