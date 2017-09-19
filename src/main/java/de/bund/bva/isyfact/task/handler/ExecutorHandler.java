package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskData;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Der ExecutorHandler ist eine Werkzeugeklasse für die Erzeugung von ExecutorServices.
 * 
 * 
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface ExecutorHandler {

	/**
	 * Erzeugt einen ScheduledExecutorService
	 *
	 * @return ScheduledExecutorService
	 */
	ScheduledExecutorService createScheduledExecutorService(int n);

}
