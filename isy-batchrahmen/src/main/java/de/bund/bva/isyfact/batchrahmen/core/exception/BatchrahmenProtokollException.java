

package de.bund.bva.isyfact.batchrahmen.core.exception;

import java.io.Serial;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;

/**
 * Exception für Fehler bei der Verarbeitung des Batchergebnis-Protokolls.
 *
 */
public class BatchrahmenProtokollException extends BatchrahmenException {

    /** Versions-ID. */
    @Serial
    private static final long serialVersionUID = -8998498430670506383L;

    /**
     * Ergzeugt einen Fehler.
     * @param ausnahmeId
     *            Die Id der Ausnahme, wird zum laden des Fehlertexts verdendet.
     * @param parameter
     *            Parameter für die Fehlernachricht.
     */
    public BatchrahmenProtokollException(String ausnahmeId, String... parameter) {
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
    public BatchrahmenProtokollException(String ausnahmeId, Throwable cause, String... parameter) {
        super(ausnahmeId, cause, parameter);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchReturnCode getReturnCode() {
        return BatchReturnCode.FEHLER_ABBRUCH;
    }    
}

