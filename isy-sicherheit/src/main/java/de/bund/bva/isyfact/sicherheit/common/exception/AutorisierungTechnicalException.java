package de.bund.bva.isyfact.sicherheit.common.exception;

/**
 * Exceptions dieser Klasse werden geworfen wenn ein technischer Fehler beim Autorisieren auftritt.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class AutorisierungTechnicalException extends SicherheitTechnicalRuntimeException {

    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5240353914116814801L;

    /**
     * Erstellt eine neue technische <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext 
     * und dem übergebenen Grund mit dem Fehlertext basierend auf 
     * <tt>(cause==null ? null : cause.toString())</tt>
     * (was normalerweise die Klasse und den Fehlertext von <tt>cause</tt> enthält).
     * <p>
     * Dieser Konstruktor ist sinnvoll für Fehler, die durch diese Exception gewrapped werden sollen,
     * z.B. {@link java.security.PrivilegedActionException}).
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     */
    public AutorisierungTechnicalException(String ausnahmeID, Throwable throwable, String... parameter) {
        super(ausnahmeID, throwable, parameter);
    }

    /**
     * Erzeugt die Berechtigungs-Exception ueber Ausnahme-ID und Parameter.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     */
    public AutorisierungTechnicalException(String ausnahmeID, String... parameter) {
        super(ausnahmeID, parameter);
    }

}
