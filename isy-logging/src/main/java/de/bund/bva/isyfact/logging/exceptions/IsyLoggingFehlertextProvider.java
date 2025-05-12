package de.bund.bva.isyfact.logging.exceptions;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * FehlertextProvider für IsyLogging, um die bibliotheksspezifischen Fehlermeldungen aufzulösen.
 * 
 */
public class IsyLoggingFehlertextProvider implements FehlertextProvider {

    /** Pfad zu den Fehlertexten. */
    private static final String NACHRICHTEN_RESOURCE = "resources/isylogging/nachrichten/fehler";

    /**
     * Das ResourceBoundle mit den Fehlertexten der Proxy-API.
     */
    private static final ResourceBundle FEHLERTEXT_BUNDLE = ResourceBundle.getBundle(
            NACHRICHTEN_RESOURCE, Locale.GERMANY);

    /**
     * liest Nachricht aus.
     * 
     * @param schluessel
     *            der Schl&uuml;ssel des Fehlertextes
     * @return die Nachricht
     */
    private static String getMessage(String schluessel) {
        return FEHLERTEXT_BUNDLE.getString(schluessel);
    }

    /**
     * liest Nachricht aus und ersetzt die Platzhalter durch die &uuml;bergebenen Parameter.
     * 
     * @param schluessel
     *            der Schl&uuml;ssel des Fehlertextes
     * @param parameter
     *            der Wert f&uuml;r die zu ersetzenden Platzhalter
     * @return die Nachricht
     */
    public String getMessage(String schluessel, String... parameter) {
        if (parameter != null && parameter.length > 0) {
            return MessageFormat.format(getMessage(schluessel), (Object[]) parameter);
        } else {
            return getMessage(schluessel);
        }
    }
}
