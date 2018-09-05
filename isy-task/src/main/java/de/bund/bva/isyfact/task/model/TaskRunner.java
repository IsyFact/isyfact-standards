package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;


/**
 * Ein TaskRunner übernimmt die eigentliche Ausführung des Tasks. Er enthält den Task selber und die zur
 * Ausführung benötigte Konfiguration.
 *
 */
public interface TaskRunner extends Runnable {

    /**
     * Gibt den mit diesem {@link TaskRunner} verlinkten Task zurück.
     *
     * @return der Task dieses TaskRunners
     */
    Task getTask();

    /**
     * Gibt die Konfiguration des mit diesem {@link TaskRunner} verlinkten Task zurück.
     *
     * @return die Task-Konfiguration
     */
    TaskKonfiguration getTaskKonfiguration();
}
