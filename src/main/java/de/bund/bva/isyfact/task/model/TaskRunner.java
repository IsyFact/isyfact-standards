package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;


/**
 * Ein TaskRunner übernimmt die eigentliche Ausführung des Tasks. Er enthält den Task selber und die zur
 * Ausführung benötigte Konfiguration.
 *
 */
public interface TaskRunner extends Runnable {

    /**
     * Gibt des mit diesem {@link TaskRunner} verlinkten Task zurück.
     *
     * @return der Task dieses TaskRunners
     */
    Task getTask();

    /**
     * @return
     */
    TaskKonfiguration getTaskKonfiguration();
}
