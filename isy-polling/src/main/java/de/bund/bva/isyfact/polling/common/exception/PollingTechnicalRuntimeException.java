package de.bund.bva.isyfact.polling.common.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Abstrakte technische <i>unchecked</i> Hauptexception. Alle technischen, <i>unchecked</i> Exceptions 
 * der Biliothek isy-polling müssen von dieser Klasse abgeleitet werden.
 * 
 */
public abstract class PollingTechnicalRuntimeException extends TechnicalRuntimeException {

    /** SerialVersionUID. **/
    private static final long serialVersionUID = 0L;

    /**
     * Zum Zugriff auf die Fehlertexte.
     */
    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new FehlertextProviderImpl();

    /**
     * Erstellt eine neue <i>unchecked</i> Exception mit einer Ausnahme-ID für den Fehlertext.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     */
    public PollingTechnicalRuntimeException(String ausnahmeID) {
        super(ausnahmeID, FEHLERTEXT_PROVIDER);
    }

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
    public PollingTechnicalRuntimeException(String ausnahmeID, String... parameter) {
        super(ausnahmeID, FEHLERTEXT_PROVIDER, parameter);
    }

    /**
     * Erstellt eine neue technische <i>unchecked</i> Exception mit einer Ausnahme-ID für den Fehlertext und
     * dem übergebenen Grund.
     * <p>
     * Anmerkung: Der Fehlertext von <code>cause</code> (dem Grund) ist <i>nicht</i> automatisch mit dem
     * übergebenen Fehlertext verbunden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public PollingTechnicalRuntimeException(String ausnahmeID, Throwable throwable) {
        super(ausnahmeID, throwable, FEHLERTEXT_PROVIDER);
    }

    /**
     * Erstellt eine neue technische <i>unchecked</i> Exception mit einer Ausnahme-ID für den Fehlertext,
     * einem Array mit Werten für die Variablenersetzung im Fehlertext und mit dem übergebenen Grund.
     * <p>
     * Anmerkung: Der Fehlertext von <code>cause</code> (dem Grund) ist <i>nicht</i> automatisch mit dem
     * übergebenen Fehlertext verbunden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     *            <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu ersetzen sind.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public PollingTechnicalRuntimeException(String ausnahmeID, Throwable throwable,
        String... parameter) {
        super(ausnahmeID, throwable, FEHLERTEXT_PROVIDER, parameter);
    }
}
