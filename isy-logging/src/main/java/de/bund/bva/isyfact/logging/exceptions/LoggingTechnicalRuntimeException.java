package de.bund.bva.isyfact.logging.exceptions;

import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Oberklasse aller TechnicalRuntimeExceptions der Bibliothek.
 * 
 */
public class LoggingTechnicalRuntimeException extends TechnicalRuntimeException {

    /** Eindeutige UID. */
    private static final long serialVersionUID = 1L;

    /** Fehlertextprovider zum Auslesen von Fehlertexten. */
    private static final IsyLoggingFehlertextProvider FEHLERTEXT_PROVIDER = new IsyLoggingFehlertextProvider();

    /**
     * Konstruktor der Klasse zum Erstellen einer Exception mit den übergebenen Parametern.
     * 
     * @see TechnicalRuntimeException
     * 
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schl&uuml;ssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht f&uuml;r die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param parameter
     *            Variable Anzahl an Parameterwerten. Parameter f&uml;r die m&ouml;glichen Variablen in einer
     *            Fehler-Nachricht.
     */
    public LoggingTechnicalRuntimeException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, FEHLERTEXT_PROVIDER, parameter);
    }

    /**
     * Konstruktor der Klasse zum Erstellen einer Exception mit den übergebenen Parametern.
     * 
     * @see TechnicalRuntimeException
     * 
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schl&uuml;ssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht f&uuml;r die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert hf&uuml;r die sp&auml;tere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     * @param parameter
     *            Variable Anzahl an Parameterwerten. Parameter f&uml;r die m&ouml;glichen Variablen in einer
     *            Fehler-Nachricht.
     */
    public LoggingTechnicalRuntimeException(String ausnahmeId, Throwable throwable, String... parameter) {
        super(ausnahmeId, throwable, FEHLERTEXT_PROVIDER, parameter);
    }

}
