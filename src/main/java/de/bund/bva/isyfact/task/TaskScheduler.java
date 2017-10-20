package de.bund.bva.isyfact.task;

import java.util.List;

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
     * Startet Tasks, die vorher per {@link TaskScheduler#addTask(TaskRunner)} hinzugefügt wurden.
     */
    void start();

    /**
     * Diese Methode fährt den Task-Scheduler herunter und wartet bis alle Tasks beendet sind oder die
     * angegebene Zeit abgelaufen ist.
     *
     * @param sekunden die Zeit in Sekunden, die maximal gewartet werden soll.
     * @return true, wenn alle Tasks vor Ablauf der Wartezeit beendet wurden, sonst false
     * @throws InterruptedException wenn das Warten unterbrochen wurde
     */
    boolean shutdownMitTimeout(long sekunden) throws InterruptedException;

    /**
     * Gibt eine Liste der zur Ausführung geplanten und laufenden Tasks zurück.
     *
     * @return die Liste mit geplanten und laufenden Tasks.
     */
    List<TaskRunner> getLaufendeTasks();
}
