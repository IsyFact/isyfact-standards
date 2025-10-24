package de.bund.bva.isyfact.logging.exceptions;

import java.io.Serial;

import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Fehler bei der Serialisierung von Objekten im Rahmen des Loggings.
 */
public class SerialisierungException extends LoggingTechnicalRuntimeException {

    /** Eindeutige UID der Klasse. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Konstruktor der Klasse.
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
    public SerialisierungException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
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
    public SerialisierungException(String ausnahmeId, Throwable throwable, String... parameter) {
        super(ausnahmeId, throwable, parameter);
    }

}
