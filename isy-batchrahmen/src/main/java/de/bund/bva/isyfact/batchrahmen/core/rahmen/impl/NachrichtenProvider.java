package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Liefert Fehlertexte aus einem statischen Resourcebundle.
 * 
 * 
 */
public class NachrichtenProvider {

    /**
     * Das ResourceBoundle mit den Fehlertexten der Proxy-API.
     */
    private static final ResourceBundle FEHLERTEXT_BUNDLE = ResourceBundle.getBundle(
        "resources/isy-batchrahmen/nachrichten/batchrahmen", Locale.GERMANY);

    /**
     * Liefert die Nachricht mit dem Schlüssel.
     * @param schluessel
     *            Nachrichten-Schlüssel.
     * @return Die Nachricht.
     */
    public static String getMessage(String schluessel) {
        return FEHLERTEXT_BUNDLE.getString(schluessel);
    }

    /**
     * Liefert die Nachricht mit dem Schlüssel. Darin werden die angegebenen Parameter ersetzt.
     * @param schluessel
     *            Nachrichten-Schlüssel.
     * @param parameter
     *            Parameter für die Nachricht.
     * @return Die Nachricht.
     */
    public static String getMessage(String schluessel, String... parameter) {
        return MessageFormat.format(getMessage(schluessel), (Object[]) parameter);
    }

}
