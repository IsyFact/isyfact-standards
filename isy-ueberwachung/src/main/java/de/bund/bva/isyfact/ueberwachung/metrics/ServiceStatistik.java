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
    };

    /**
     * Returns the number of calls where a technical error occurred.
     */
    default long getAnzahlTechnicalExceptions() {
        throw new UnsupportedOperationException();
    };

    /**
     * Returns the number of calls where a business error occurred.
     */
    default long getAnzahlBusinessExceptions() {
        throw new UnsupportedOperationException();
    };

    /**
     * Returns the number of calls counted in the last minute where no error occurred.
     */
    int getAnzahlAufrufeLetzteMinute();

    /**
     * Returns the average duration of the last N calls.
     */
    Duration getDurchschnittsDauerLetzteAufrufe();

    /**
     * Returns the number of calls in the last minute, in which an error occurred.
     */
    int getAnzahlFehlerLetzteMinute();

    /**
     * Returns the number of calls in the last minute, in which a business error occurred.
     */
    int getAnzahlFachlicheFehlerLetzteMinute();
}
