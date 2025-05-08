package de.bund.bva.isyfact.sicherheit;

/**
 * Dieses Interface definiert den Typ Berechtigungsdaten zu dem Rollen und Rechte gehören. Es ist nicht
 * geplant, dass es Implementierungen dieses Interfaces gibt, sondern nur Implementierungen von Subinterfaces.
 * 
 * @see Recht
 * @see Rolle
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface Berechtigungsdaten {

    /**
     * Gibt die Id (Name/Bezeichner) der jeweiligen Instanz zurück. Jede Instanz wird über Ihre Id
     * identifiziert.
     * 
     * @return Die Id der Instanz
     */
    String getId();
}
