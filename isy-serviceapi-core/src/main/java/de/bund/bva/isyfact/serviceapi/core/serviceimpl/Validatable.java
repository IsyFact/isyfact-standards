package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

/**
 * Schnittstelle für Implementierungsteile, die ihre statische Konfiguration validieren können.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public interface Validatable {

    /**
     * Überprüft die Konsistenz der Konfiguration.
     * 
     * @param calledInterface
     *            die gerufene Schnittstelle
     * @param target
     *            die zu rufende Target-Bean
     */
    void validateConfiguration(Class<?> calledInterface, Object target);

}
