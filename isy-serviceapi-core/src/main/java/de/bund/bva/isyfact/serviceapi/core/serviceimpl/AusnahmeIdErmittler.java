package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

/**
 * Ermittelt zu einer aufgetretenen Exception eine passende Ausnahme-ID, die den Fehlertext identifiziert.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public interface AusnahmeIdErmittler {

    /**
     * Ermittelt zu einer aufgetretenen Exception eine passende Ausnahme-ID, die den Fehlertext identifiziert.
     * 
     * @param e
     *            die aufgetretene Exception
     * @return die Ausnahme-ID, nicht null
     */
    String ermittleAusnahmeId(Throwable e);

}
