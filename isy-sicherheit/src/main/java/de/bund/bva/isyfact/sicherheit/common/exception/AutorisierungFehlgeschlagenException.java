package de.bund.bva.isyfact.sicherheit.common.exception;

/**
 * Diese Klasse kapselt alle Exceptions die beim Aufruf der Methode authorisiere des Interfaces Sicherheit
 * auftreten.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class AutorisierungFehlgeschlagenException extends SicherheitTechnicalRuntimeException {

    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5582929933896402663L;

    /**
     * Erstellt eine neue fachliche <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext.
     * Außerdem wird, wie im Default Konstruktor, <code>null</code> als Fehlertext verwendet.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     *
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     */
    public AutorisierungFehlgeschlagenException(String ausnahmeID) {
        super(ausnahmeID);
    }

    /**
     * Erstellt eine neue fachliche <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext.
     * Außerdem wird, wie im Default Konstruktor, <code>null</code> als Fehlertext verwendet.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     *
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     *            <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu ersetzen sind.
     */
    public AutorisierungFehlgeschlagenException(String ausnahmeID, String... parameter) {
        super(ausnahmeID, parameter);
    }

    /**
     * Erstellt eine neue fachliche <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext und dem
     * übergebenen Grund mit dem Fehlertext basierend auf <tt>(cause==null ? null : cause.toString())</tt>
     * (was normalerweise die Klasse und den Fehlertext von <tt>cause</tt> enthält).
     * <p>
     * Dieser Konstruktor ist sinnvoll für Fehler, die durch diese Exception gewrapped werden sollen, z.B.
     * {@link java.security.PrivilegedActionException}).
     *
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public AutorisierungFehlgeschlagenException(String ausnahmeID, Throwable throwable) {
        super(ausnahmeID, throwable);
    }

}
