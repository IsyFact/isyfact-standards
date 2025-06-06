package de.bund.bva.isyfact.aufrufkontext.common.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * Diese Klasse stellt Methoden zum Auslesen von Fehlertexten auf Basis von AusnahmeIDs und Parametern, zur
 * Verfügung.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public class AufrufKontextFehlertextProvider implements FehlertextProvider {
    /**
     * Das ResourceBoundle mit den Fehlertexten der AufrufkontextAPI.
     */
    public static final ResourceBundle FEHLERTEXT_BUNDLE =
        ResourceBundle.getBundle("resources/isy-aufrufkontext/nachrichten/aufrufkontextfehler", Locale.GERMANY);

    /**
     * liest Nachricht aus und ersetzt die Platzhalter durch die übergebenen Parameter.
     * 
     * @param schluessel
     *            der Schlüssel des Fehlertextes
     * @return die Nachricht
     */
    public static String getMessage(String schluessel) {
        return FEHLERTEXT_BUNDLE.getString(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(String schluessel, String... parameter) {
        return MessageFormat.format(getMessage(schluessel), (Object[]) parameter);
    }
    
}
