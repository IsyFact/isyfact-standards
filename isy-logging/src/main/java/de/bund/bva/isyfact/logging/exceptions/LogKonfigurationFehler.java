package de.bund.bva.isyfact.logging.exceptions;

import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Fehler in der Konfiguration des Loggings.
 * 
 */
public class LogKonfigurationFehler extends LoggingTechnicalRuntimeException {

    /** Eindeutige UID. */
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
    public LogKonfigurationFehler(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

}
