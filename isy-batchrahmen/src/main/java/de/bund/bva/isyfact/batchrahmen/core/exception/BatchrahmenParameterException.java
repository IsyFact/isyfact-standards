package de.bund.bva.isyfact.batchrahmen.core.exception;

import java.io.Serial;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;

/**
 * Diese Exception wird geworfen, wenn die Kommandozeilenparameter des
 * Batchaufrufs nicht korrekt waren.
 * 
 *
 */
public class BatchrahmenParameterException extends BatchrahmenException {
    /**
     * UID der Exception.
     */
    @Serial
    private static final long serialVersionUID = -4647145219367317868L;

    /**
     * Ergzeugt einen Fehler.
     * @param ausnahmeId
     *            Die Id der Ausnahme, wird zum laden des Fehlertexts verdendet.
     * @param parameter
     *            Parameter für die Fehlernachricht.
     */
    public BatchrahmenParameterException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }
    
    /**
     * Ergzeugt einen Fehler.
     * @param ausnahmeId
     *            Die Id der Ausnahme, wird zum laden des Fehlertexts verdendet.
     * @param cause
     *            Die Ursache des Fehlers.
     * @param parameter
     *            Parameter für die Fehlernachricht.
     */
    public BatchrahmenParameterException(String ausnahmeId, Throwable cause, String... parameter) {
        super(ausnahmeId, cause, parameter);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchReturnCode getReturnCode() {
        return BatchReturnCode.FEHLER_PARAMETER;
    }    
}
