package de.bund.bva.isyfact.konfiguration.common.exception;

/**
 * Exception für Fehler, die beim Arbeiten mit Konfigurationsdateien auftreten.
 * 
 *
 */
public class KonfigurationDateiException extends KonfigurationException {

    /**
     * UID.
     */
    private static final long serialVersionUID = 7599335414052120366L;

    /**
     * Erstellt eine neue technische <i>unchecked</i> KonfigurationException mit einer Ausnahme-ID für
     * den Fehlertext, einem Werten für die Variablenersetzung im Fehlertext und mit dem übergebenen
     * Grund.
     * <p>
     * Anmerkung: Der Fehlertext von <code>cause</code> (dem Grund) ist <i>nicht</i> automatisch mit dem
     * übergebenen Fehlertext verbunden.
     * 
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param cause
     *            Der Grund. Throwable wird gespeichert hfür die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     * @param parameter
     *            Die variablen Parameterwerte. Parameterwerte für die möglichen Variablen in einer
     *            Fehler-Nachricht. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu
     *            ersetzen sind.            
     */
    public KonfigurationDateiException(String ausnahmeId, Throwable cause, String... parameter) {
        super(ausnahmeId, cause, parameter);
    }

    /**
     * Erstellt eine neue technische <i>unchecked</i> KonfigurationException mit einer Ausnahme-ID für
     * den Fehlertext und dem übergebenen Grund.
     * <p>
     * Anmerkung: Der Fehlertext von <code>cause</code> (dem Grund) ist <i>nicht</i> automatisch mit dem
     * übergebenen Fehlertext verbunden.
     * 
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public KonfigurationDateiException(String ausnahmeId, Throwable throwable) {
        super(ausnahmeId, throwable);
    }

    /**
     * Erstellt eine neue <i>unchecked</i> KonfigurationException mit einer Ausnahme-ID für den
     * Fehlertext.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     * 
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     */
    public KonfigurationDateiException(String ausnahmeId) {
        super(ausnahmeId);
    }

    /**
     * Erstellt eine neue <i>unchecked</i> KonfigurationException mit einer Fehler-ID für den Fehlertext
     * und einem Werten für die Variablenersetzung im Fehlertext. Au&szlig;serdem wird, wie im Default
     * Konstruktor, <code>null</code> als Fehlertext verwendet.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     * 
     * @param ausnahmeId
     *            Die Fehler-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu
     *            laden.
     * @param parameter
     *            Die variablen Parameterwerte. Parameterwerte f&uml;r die möglichen Variablen in einer
     *            Fehler-Nachricht. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu
     *            ersetzen sind.
     */
    public KonfigurationDateiException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

}
