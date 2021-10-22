package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

public class TaskRuntimeException extends TechnicalRuntimeException {

    /** Serial version UID. */
    private static final long serialVersionUID = -3L;

    /** Error message provider for task exceptions. */
    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new TaskFehlertextProvider();

    public TaskRuntimeException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, FEHLERTEXT_PROVIDER, parameter);
    }

    public TaskRuntimeException(String ausnahmeId, Throwable cause, String... parameter) {
        super(ausnahmeId, cause, FEHLERTEXT_PROVIDER, parameter);
    }

}
