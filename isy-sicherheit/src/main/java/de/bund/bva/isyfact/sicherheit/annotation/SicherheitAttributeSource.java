package de.bund.bva.isyfact.sicherheit.annotation;

import java.lang.reflect.Method;

/**
 * Diese Schnittstelle bietet Operationen zum Ermitteln der benötigten Rechte zu einer Service-Methode.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface SicherheitAttributeSource {

    /**
     * Ermittelt die benötigten Rechte zu einer Service-Methode.
     * 
     * @param method
     *            die gerufene Service-Methode
     * @param targetClass
     *            die aufgerufene Zielklasse
     * @return die benötigten Rechte
     */
    public String[] getBenoetigeRechte(Method method, Class<?> targetClass);

}
