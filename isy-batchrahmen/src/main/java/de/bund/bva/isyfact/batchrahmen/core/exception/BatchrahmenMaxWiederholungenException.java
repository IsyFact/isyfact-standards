package de.bund.bva.isyfact.batchrahmen.core.exception;

/**
 * Exception thrown when the maximum number of restarts for a failed batch has been reached.
 */
public class BatchrahmenMaxWiederholungenException extends BatchrahmenAbbruchException {

    /** Versions-ID. */
    private static final long serialVersionUID = 7394815620483917L;

    public BatchrahmenMaxWiederholungenException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }
}
