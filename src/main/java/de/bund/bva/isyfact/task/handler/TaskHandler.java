package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.exception.CreateOperationInstanceException;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskData;

/**
 * Der TaskHandler ist eine Werkzeugeklasse für Tasks.
 * 
 * 
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface TaskHandler {

	/**
	 * Erzeugt einen neuen Task aus einem TaskData-Objekt
	 *
	 * @return Task
	 */
	Task createTask(TaskData taskData) throws CreateOperationInstanceException, HostNotApplicableException;

}
