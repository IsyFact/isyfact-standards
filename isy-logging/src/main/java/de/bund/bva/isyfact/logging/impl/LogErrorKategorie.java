package de.bund.bva.isyfact.logging.impl;



/**
 * Internes Enum für Log-Kategorien des Log-Level 'Error'. Diese werden nur intern verwendet und können über
 * die Schnittstelle nicht angegeben werden.
 * 
 */
public enum LogErrorKategorie {

    /** Schwerwiegende Fehler, von denen sich die Anwendung nicht erholen kann und beendet werden muss. */
    FATAL,
    /**
     * Fehler, die zum Abbruch einer Operation geführt haben und behandelt (gefangen und nicht weitergereicht)
     * wurden.
     */
    ERROR;

}
