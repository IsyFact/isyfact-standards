package de.bund.bva.isyfact.persistence.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Technischer Fehler in der isy-persistence.
 * 
 */
public class PersistenzException extends TechnicalRuntimeException {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new PersistenzFehlertextProvider();

    public PersistenzException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, FEHLERTEXT_PROVIDER, parameter);
    }

    public PersistenzException(String ausnahmeId, Throwable cause, String... parameter) {
        super(ausnahmeId, cause, FEHLERTEXT_PROVIDER, parameter);
    }

}
