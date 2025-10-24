package de.bund.bva.isyfact.ueberwachung.metrics;

import java.time.Duration;

/**
 * Provides service call statistics (number of calls, average duration, number of errors, etc.).
 */
public interface ServiceStatistik {

    /**
     * An even number of arguments representing key/value pairs of tags; e.g.:
     * {@code ["anwendung", "de.bund.bva.isyfact.myapp", "servicestatistik", "myservice"]}.
     */
    String[] getTags();

    /**
     * Returns the number of calls counted where no error occurred.
     */
    default long getAnzahlAufrufe() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number of calls where a technical error occurred.
     */
    default long getAnzahlTechnicalExceptions() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number of calls where a business error occurred.
     */
    default long getAnzahlBusinessExceptions() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the average duration of the last N calls.
     */
    Duration getDurchschnittsDauerLetzteAufrufe();
}
