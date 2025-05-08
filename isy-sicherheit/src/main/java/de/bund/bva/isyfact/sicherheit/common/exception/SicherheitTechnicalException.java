package de.bund.bva.isyfact.sicherheit.common.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.BaseException;

/**
 * Abstrakte technische <i>checked</i> Hauptexception. Alle technischen, <i>checked</i> Exceptions im
 * Service-Gateway müssen von dieser Klasse abgeleitet werden.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public abstract class SicherheitTechnicalException extends BaseException {

    /**
     * Zum Zugriff auf die Fehlertexte.
     */
    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new SicherheitFehlertextProvider();

    /** Die UID. */
    private static final long serialVersionUID = 5018039454021358342L;

    /**
     * Erstellt eine neue <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext und einem Array
     * mit Werten für die Variablenersetzung im Fehlertext.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel der den Fehlertext identifiziert.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     *            <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu ersetzen sind.
     */
    public SicherheitTechnicalException(String ausnahmeID, String... parameter) {
        super(ausnahmeID, FEHLERTEXT_PROVIDER, parameter);
    }

    /**
     * Erstellt eine neue <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext, einem Array mit
     * Werten für die Variablenersetzung im Fehlertext und mit dem übergebenen Grund.
     * <p>
     * Anmerkung: Der Fehlertext von <code>cause</code> (dem Grund) ist <i>nicht</i> automatisch mit dem
     * übergebenen Fehlertext verbunden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel der den Fehlertext identifiziert.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     *            <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu ersetzen sind.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public SicherheitTechnicalException(String ausnahmeID, Throwable throwable, String... parameter) {
        super(ausnahmeID, throwable, FEHLERTEXT_PROVIDER, parameter);
    }

}
