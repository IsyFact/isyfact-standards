package de.bund.bva.isyfact.logging.exceptions;

import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Exception zum Kennzeichnen das ein fehlerhafter Logeintrag erstellt werden sollte. Bspw. auf Grund
 * fehlender Informationen.
 * 
 */
public class FehlerhafterLogeintrag extends LoggingTechnicalRuntimeException {

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
    public FehlerhafterLogeintrag(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

    /**
     * Konstruktor der Klasse.
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
    public FehlerhafterLogeintrag(String ausnahmeId, Throwable throwable, String... parameter) {
        super(ausnahmeId, throwable, parameter);
    }
}
