package de.bund.bva.isyfact.exception;

/**
 * Dieses Interface beschreibt Methoden zum Auslesen von Fehlertexten auf Basis von AusnahmeIDs und
 * Parametern, sofern vorhanden.
 * 
 */
public interface FehlertextProvider {


    /**
     * liest Nachricht aus und ersetzt die Platzhalter durch die übergebenen Parameter.
     * 
     * @param schluessel
     *            der Schlüssel des Fehlertextes
     * @param parameter
     *            der Wert für die zu ersetzenden Platzhalter
     * @return die Nachricht
     */
    public String getMessage(String schluessel, String... parameter);
}
