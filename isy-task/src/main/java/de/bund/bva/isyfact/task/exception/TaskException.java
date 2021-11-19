package de.bund.bva.isyfact.task.exception;

import de.bund.bva.pliscommon.exception.FehlertextProvider;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;

public class TaskException extends PlisTechnicalException {

    /** Serial version UID. */
    private static final long serialVersionUID = 13L;

    /** Error message provider for task exceptions. */
    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new TaskFehlertextProvider();

    public TaskException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, FEHLERTEXT_PROVIDER, parameter);
    }

    public TaskException(String ausnahmeId, Throwable cause, String... parameter) {
        super(ausnahmeId, cause, FEHLERTEXT_PROVIDER, parameter);
    }

}
