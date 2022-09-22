package de.bund.bva.isyfact.ueberwachung.metrics;

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
     * Returns the number of calls counted in the last minute where no error occurred.
     */
    int getAnzahlAufrufeLetzteMinute();

    /**
     * Returns the average duration of the last N calls.
     */
    long getDurchschnittsDauerLetzteAufrufe();

    /**
     * Returns the number of calls in the last minute, in which an error occurred.
     */
    int getAnzahlFehlerLetzteMinute();

    /**
     * Returns the number of calls in the last minute, in which a business error occurred.
     */
    int getAnzahlFachlicheFehlerLetzteMinute();
}
