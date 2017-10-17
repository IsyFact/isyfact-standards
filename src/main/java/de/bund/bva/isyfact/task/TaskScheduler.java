package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.task.model.TaskRunner;

/**
 * Der TaskScheduler bietet die Möglichkeit, dass Aufgaben (Tasks) zu bestimmten Zeitpunkten ausgeführt werden können.
 */
public interface TaskScheduler {

    /**
     * Startet alle über Spring konfigurierte Tasks.
     */
    void starteKonfigurierteTasks();

    /**
     * Fügt einen Task manuell hinzu.
     *
     * @param taskRunner Der {@link TaskRunner} mit dem Task.
     */
    void addTask(TaskRunner taskRunner);

    /**
     * Startet alle Tasks...?
     */
    void start();

    /**
     * Diese Methode blockiert das Beenden des Programms für eine festgelegte Anzahl an Sekunden.
     * Die Methode wird genutzt, damit die Tasks noch weiterlaufen.
     *
     * @param sekunden
     * @throws InterruptedException
     */
    void stopNachTimeout(long sekunden) throws InterruptedException;


    void warteAufTerminierung(long sekunden) throws InterruptedException;
}
