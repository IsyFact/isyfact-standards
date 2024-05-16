package de.bund.bva.isyfact.sicherheit.common.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class provides methods for reading error messages based on exception IDs and parameters.
 */
public class SicherheitFehlertextProvider implements FehlertextProvider {

    private final ResourceBundle resourceBundle;

    public SicherheitFehlertextProvider() {
        this(ResourceBundle.getBundle("resources/isy-sicherheit/nachrichten/sicherheitfehler", Locale.GERMANY));
    }

    public SicherheitFehlertextProvider(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * Reads the message and replaces the placeholders with the provided parameters.
     *
     * @param schluessel the key of the error message
     * @return the message
     */
    public static String getMessage(String schluessel) {
        if (schluessel == null) {
            throw new IllegalArgumentException("Key is null");
        }
        return ResourceBundle.getBundle("resources/isy-sicherheit/nachrichten/sicherheitfehler", Locale.GERMANY).getString(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(String schluessel, String... parameter) {
        if (schluessel == null) {
            throw new IllegalArgumentException("Key is null");
        }
        return MessageFormat.format(resourceBundle.getString(schluessel), (Object[]) parameter);
    }
}
