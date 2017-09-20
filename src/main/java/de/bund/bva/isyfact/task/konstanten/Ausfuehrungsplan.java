package de.bund.bva.isyfact.task.konstanten;

/**
 * Arten von Plänen zur Ausführung von Tasks, z.B. alle 5 Minuten oder jeden Tag um 4 Uhr morgens.
 */
public enum Ausfuehrungsplan {
    /**
     * Wiederholte Ausführung mit festgelegte Zeit zwischen dem Beginn zweier Ausführungen.
     * Beispiel: alle 4 Minuten.
     */
    FIXED_RATE,

    /**
     * Wiederholte Ausführung mit festgelegter Zeit zwischen zweier Ausführungen.
     * Beispiel: 4 Minuten zwischen Ende der jetzigen und Beginn der nächsten Ausführung.
     */
    FIXED_DELAY,

    /**
     * Wiederholte Ausführung zu einem definierten Zeitpunkt jeden Tag oder an bestimmten Tagen.
     * Beispiel: jeden Tag um 4 Uhr morgens, jeden Montag um 13 Uhr.
     */
    FIXED_TIME,

    /**
     * Einmalige Ausführung zu einem definierten Zeitpunkt.
     * Beispiel: 12.12.2022 um 14 Uhr, 10 Minuten nach Start der Anwendung.
     */
    ONCE
}
