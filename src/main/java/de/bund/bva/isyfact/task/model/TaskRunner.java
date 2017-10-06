package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.handler.AusfuehrungsplanHandler.Ausfuehrungsplan;
import de.bund.bva.isyfact.task.jmx.TaskMonitor;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Ein Objekt des Typs TaskRunner entspricht einer auszuführenden Aufgabe.
 * Jede Aufgabe ruft fachliche Operationen auf.
 * Das bedeutet, dass sich alle aufgerufenen fachlichen Operationen eindeutig einer bestimmten Ausführung einer Aufgabe zuordnen lassen.
 * Ein TaskData wird von der konkreten Implementierung eines auszuführenden Tasks abgeleitet.
 * Jeder auszuführende TaskRunner setzt seine id, seinen Namen und den Zeitpunkt der Ausführung.
 * Die Aufgaben, die erledigt werden sollen, werden in einer call- bzw. run-Methode
 * einer CallableOperation oder RepeatableOperation implementiert.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface TaskRunner extends Runnable {

    String getId();

    Task getTask();

    Ausfuehrungsplan getAusfuehrungsplan();

    Duration getInitialDelay();

    Duration getFixedRate();

    Duration getFixedDelay();

    LocalDateTime getExecutionDateTime();

}
