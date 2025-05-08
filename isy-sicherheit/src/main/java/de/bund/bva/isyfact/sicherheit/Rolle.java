package de.bund.bva.isyfact.sicherheit;

/**
 * Dieses Interface definiert die Rollen die ein Benutzer haben kann. 
 * 
 * Diese Klasse erbt die Methode getId() von Berechtigungsdaten
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface Rolle extends Berechtigungsdaten {
    
    /**
     * Gibt den Namen einer Rolle zurueck. 
     * @return den Namen der Rolle
     */
    String getName();
}
