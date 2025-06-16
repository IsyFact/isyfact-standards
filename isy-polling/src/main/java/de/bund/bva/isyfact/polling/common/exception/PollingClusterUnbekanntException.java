package de.bund.bva.isyfact.polling.common.exception;

/**
 * Diese Exception signalisiert den Zugriff auf einen unbekannten Polling-Cluster.
 * 
 */
public class PollingClusterUnbekanntException extends PollingTechnicalRuntimeException {

    /** SerialVersionUID. **/
    private static final long serialVersionUID = 0L;

    /**
     * Erstellt eine neue technische <i>unchecked</i> Exception mit einer Ausnahme-ID für den Fehlertext und
     * einem Array mit Werten für die Variablenersetzung im Fehlertext.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu
     *            laden.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     *            <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu ersetzen sind.
     */
    public PollingClusterUnbekanntException(String ausnahmeID, String... parameter) {
        super(ausnahmeID, parameter);
    }   
}
