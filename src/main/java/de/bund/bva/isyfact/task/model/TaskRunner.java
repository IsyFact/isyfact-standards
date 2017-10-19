package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;

/**
 * Ein Objekt des Typs TaskRunner entspricht einer auszuführenden Aufgabe.
 * Jede Aufgabe ruft fachliche Operationen auf.
 * Das bedeutet, dass sich alle aufgerufenen fachlichen Operationen eindeutig einer bestimmten Ausführung einer Aufgabe zuordnen lassen.
 * Ein TaskData wird von der konkreten Implementierung eines auszuführenden Tasks abgeleitet.
 * Jeder auszuführende TaskRunner setzt seine id, seinen Namen und den Zeitpunkt der Ausführung.
 * Die Aufgaben, die erledigt werden sollen, werden in einer call- bzw. run-Methode
 * einer CallableOperation oder RepeatableOperation implementiert.
 */

/**
 * Ein TaskRunner entspricht einer auszuführenden Aufgabe.
 *
 *
 * Ein Objekt des Typs TaskRunner entspricht einer auszuführenden Aufgabe. Jede Aufgabe ruft fachliche Operationen auf.
 * Das bedeutet, dass sich alle aufgerufenen fachlichen Operationen eindeutig
 * einer bestimmten Ausführung einer Aufgabe zuordnen lassen.
 * Jeder auszuführende TaskRunner setzt seine id, seinen Namen und den Zeitpunkt der Ausführung.
 * Die Aufgaben, die erledigt werden sollen, werden in einer call- bzw. run-Methode einer CallableOperation implementiert.
 *
 * Alle aufgerufenen fachlichen Operationen lassen sich eindeutig einer bestimmten Aufgabe zuordnen.
 * Wenn ein TaskRunner erfolgreich durchlaufen wurde, wird dies notiert.
 * Gleichzeitig merkt sich der TaskRunner den Endzeitpunkt des Durchlaufs.
 */
public interface TaskRunner extends Runnable {

    Task getTask();

    TaskKonfiguration getTaskKonfiguration();
}
