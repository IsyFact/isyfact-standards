package de.bund.bva.isyfact.persistence.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * Fehlertext-Provider für die isy-persistence.
 * 
 */
public class PersistenzFehlertextProvider implements FehlertextProvider {
    /**
     * Das ResourceBoundle mit den Fehlertexten der AufrufkontextAPI.
     */
    public static final ResourceBundle FEHLERTEXT_BUNDLE = ResourceBundle.getBundle(
        "resources/isy-persistence/nachrichten/fehler", Locale.GERMANY);

    /**
     * {@inheritDoc}
     */
    public String getMessage(String schluessel, String... parameter) {
        return MessageFormat.format(FEHLERTEXT_BUNDLE.getString(schluessel), (Object[]) parameter);
    }

}
