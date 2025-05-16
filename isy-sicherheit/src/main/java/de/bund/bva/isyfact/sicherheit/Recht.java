package de.bund.bva.isyfact.sicherheit;

/**
 * Diese Interface definiert ein Recht das vorhanden sein kann. Die meisten Rechte hängen an dem vorhandsein
 * einer oder mehrere Rollen.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface Recht extends Berechtigungsdaten {

    /**
     * Gibt das Property eines Rechtes zurück wenn das Recht ein Property mit diesem Namen hat.
     * 
     * @param propertyName
     *            der Name des Properties
     * @return Das Property oder null
     */
    public Object getProperty(String propertyName);
}
