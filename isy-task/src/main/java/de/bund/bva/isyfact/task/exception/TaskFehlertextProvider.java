package de.bund.bva.isyfact.task.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * Fehlertext-Provider for isy-task.
 */
public class TaskFehlertextProvider implements FehlertextProvider {

    /**
     * The ResourceBundle containing the error messages for isy-task.
     */
    public static final ResourceBundle FEHLERTEXT_BUNDLE = ResourceBundle.getBundle(
            "resources/isy-task/nachrichten/fehler", Locale.GERMANY);

    @Override
    public String getMessage(String schluessel, String... parameter) {
        return MessageFormat.format(FEHLERTEXT_BUNDLE.getString(schluessel), (Object[]) parameter);
    }

}
