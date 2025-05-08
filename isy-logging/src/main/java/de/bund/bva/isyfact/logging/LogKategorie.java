package de.bund.bva.isyfact.logging;



/**
 * Kategorie des Logeintrags. Kategorien werden beim Aufruf des Logging-Frameworks nur im Level INFO
 * verwendet.
 * 
 */
public enum LogKategorie {

    /** Informationen zu Systemzustand, Systemereignissen und durchgeführten Operationen. */
    JOURNAL,
    /**  Kennzahlen zum Systembetrieb und zur Systemnutzung. */
    METRIK,
    /** Informationen zum Laufzeitverhalten des Systems. */
    PROFILING,
    /** (Potentieller) Angriffsversuch. */
    SICHERHEIT;

}
