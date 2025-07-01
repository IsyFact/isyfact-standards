package de.bund.bva.isyfact.exception;

/**
 * Abstrakte (<i>checked</i>) Hauptexception, welche als Basis für die abstrakten <b>technischen</b>
 * Exceptions (<i>checked</i>) auf Anwendungsebene verwendet wird.
 * <p>
 *
 */
public abstract class TechnicalException extends BaseException {

    /**
     * Erstellt eine neue technische <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext und
     * einem Werten für die Variablenersetzung im Fehlertext.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     *
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param fehlertextProvider
     *            Die FehlertextProvider-Implementierung, welche verwendet wird, um eine Fehlertext zu laden.
     * @param parameter
     *            Variable Anzahl an Parameterwerten. Parameterwert für die mögliche Variable in einer
     *            Fehler-Nachricht.
     */
    protected TechnicalException(String ausnahmeId, FehlertextProvider fehlertextProvider,
        String... parameter) {

        super(ausnahmeId, fehlertextProvider, parameter);
    }

    /**
     * Erstellt eine neue technische <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext, einem
     * Werten für die Variablenersetzung im Fehlertext und mit dem übergebenen Grund.
     * <p>
     * Anmerkung: Der Fehlertext von <code>cause</code> (dem Grund) ist <i>nicht</i> automatisch mit dem
     * übergebenen Fehlertext verbunden.
     *
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schlüssel der den Fehlertext identifiziert.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     * @param fehlertextProvider
     *            Die FehlertextProvider-Implementierung, welche verwendet wird, um eine Fehlertext zu laden.
     * @param parameter
     *            Variable Anzahl an Parameterwerten. Parameterwert für die mögliche Variable in einer
     *            Fehler-Nachricht.
     */
    protected TechnicalException(String ausnahmeId, Throwable throwable,
        FehlertextProvider fehlertextProvider, String... parameter) {

        super(ausnahmeId, throwable, fehlertextProvider, parameter);
    }

}
