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
public interface TaskRunner extends Runnable {

    Task getTask();

    TaskKonfiguration getTaskKonfiguration();
}
