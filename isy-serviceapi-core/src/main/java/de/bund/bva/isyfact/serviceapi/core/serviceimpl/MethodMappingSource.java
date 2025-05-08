package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Method;

/**
 * Diese Schnittstelle bietet Operationen zum Ermitteln einer Zielmethode zu einer aufgerufenen Methode.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public interface MethodMappingSource {

    /**
     * Ermittelt eine Zielmethode zu einer aufgerufenen Methode.
     * 
     * @param calledMethod
     *            die aufgerufene Methode
     * @param targetClass
     *            die Zielklasse, die die Zielmethode implementiert
     * 
     * @return die aufzurufende Methode der target-Bean
     */
    Method getTargetMethod(Method calledMethod, Class<?> targetClass);

    /**
     * Prüft, ob ein Parameter übersprungen, d.h. nicht an die Zielmethode weitergegeben werden soll.
     * 
     * @param parameterType
     *            der Parametertyp
     * 
     * @return ob der Parameter übersprungen werden soll
     */
    boolean skipParameter(Class<?> parameterType);

}
