package de.bund.bva.isyfact.batchrahmen.core.exception;

import java.io.Serial;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;

/**
 * Exception thrown when the maximum number of restarts for a failed batch has been reached.
 */
public class BatchrahmenMaxWiederholungenException extends BatchrahmenException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BatchrahmenMaxWiederholungenException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

    @Override
    public BatchReturnCode getReturnCode() {
        return BatchReturnCode.FEHLER_ABBRUCH;
    }
}
