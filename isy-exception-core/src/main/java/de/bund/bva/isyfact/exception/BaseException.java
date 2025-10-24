package de.bund.bva.isyfact.exception;

import java.util.UUID;

import de.bund.bva.isyfact.exception.common.FehlertextUtil;

/**
 * Abstrakte (<i>checked</i>) Hauptexception, welche als Basis für die abstrakten fachlichen und technischen
 * Exceptions (<i>checked</i>) auf Anwendungsebene verwendet wird.
 * <p>
 * Die Applikationsspezifischen Exceptions sollen nicht von direkt von {@link BaseException} erben,
 * sondern von {@link BusinessException} (für fachliche checked Exceptions) oder
 * {@link TechnicalException} (für technische checked Exceptions).
 * <p>
 *
 */
public abstract class BaseException extends Exception {
    /**
     * Ausnahme-ID.
     */
    private String ausnahmeId;

    /**
     * Die eineindeutige ID, die den aufgetretenen Fehler referenziert (nicht die Fehlerart).
     */
    private String uniqueId;

    /**
     * Erstellt eine neue <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext und einem Werten
     * für die Variablenersetzung im Fehlertext.
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
     *            Variable Anzahl an Parameterwerten. Parameterwert f&uml;r die mögliche Variable in einer
     *            Fehler-Nachricht.
     */
    protected BaseException(String ausnahmeId, FehlertextProvider fehlertextProvider, String... parameter) {
        super(fehlertextProvider.getMessage(ausnahmeId, parameter));
        this.ausnahmeId = ausnahmeId;
        this.uniqueId = UUID.randomUUID().toString();
    }

    /**
     * Erstellt eine neue <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext, einem Werten für
     * die Variablenersetzung im Fehlertext und mit dem übergebenen Grund.
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
     *            Variable Anzahl an Parameterwerten. Parameterwert f&uml;r die mögliche Variable in einer
     *            Fehler-Nachricht.
     */
    protected BaseException(String ausnahmeId, Throwable throwable, FehlertextProvider fehlertextProvider,
        String... parameter) {
        super(fehlertextProvider.getMessage(ausnahmeId, parameter), throwable);
        this.ausnahmeId = ausnahmeId;
        setUniqueId(throwable);
    }

    /**
     * setzt die UniqueId aus der übergebenen Exception.
     *
     * @param throwable
     *            Die übergebene Exception.
     */
    private void setUniqueId(Throwable throwable) {
        if (throwable instanceof BaseException exception1) {
            this.uniqueId = exception1.getUniqueId();
        } else if (throwable instanceof TechnicalRuntimeException exception) {
            this.uniqueId = exception.getUniqueId();
        } else {
            this.uniqueId = UUID.randomUUID().toString();
        }
    }

    /**
     * Liefert die Ausnahme-ID zurück, welche gleichzeitig der Schlüssel für den Fehlertext ist.
     *
     * @return Ausnahme-ID String sofern gesetzt, ansonsten <code>null</code>.
     */
    public String getAusnahmeId() {
        return this.ausnahmeId;
    }

    /**
     * Liefert die eineindeutige ID zurück, welche beim Auftreten der Exception erzeugt wurde.
     *
     * @return Die Ausnahme-ID
     */
    public String getUniqueId() {
        return this.uniqueId;
    }

    /**
     * @return Den unformattierten Fehlertext
     */
    public String getFehlertext() {
        return super.getMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return FehlertextUtil.erweitereFehlertext(this.ausnahmeId, super.getMessage(), this.uniqueId);
    }
}
