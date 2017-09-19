package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.TaskDataHandler;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.model.impl.TaskDataImpl;

/**
 * Der TaskDataHandler erstellt TaskData-Instanzen.
 * 
 * 
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class TaskDataHandlerImpl implements TaskDataHandler {
	private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskDataHandlerImpl.class);

	/**
	 * Liefert eine Instanz des Typs TaskData.
	 *
	 * @param id
	 * @param username
	 * @param password
	 * @param executionDateTime
	 * @param hostName
	 * @param operationName
	 * @param days
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return ein Task Data Objekt
	 */
	@Override
	public TaskData createTaskData(
			String id,
			String username,
			String password,
			String executionDateTime,
			String operationName,
			String hostName,
			String days,
			String hours,
			String minutes,
			String seconds
	) {
		return new TaskDataImpl(
				id,
				username,
				password,
				executionDateTime,
				operationName,
				hostName,
                days,
				hours,
				minutes,
				seconds);
	}

	/**
	 * Liefert eine Instanz des Typs TaskData.
	 *
	 *
	 * @param id
	 * @param username
	 * @param password
	 * @param executionDateTime
	 * @param operationName
	 * @param hostName
	 * @return ein Task Data Objekt
	 */
	public TaskData createTaskData(
			String id,
			String username,
			String password,
			String executionDateTime,
			String operationName,
			String hostName) {
		return createTaskData(
				id,
				username,
				password,
				executionDateTime,
				operationName,
				hostName,
				null,
				null,
				null,
				null);
	}

}