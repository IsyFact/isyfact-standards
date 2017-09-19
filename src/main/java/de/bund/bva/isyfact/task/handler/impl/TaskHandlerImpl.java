package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.exception.CreateOperationInstanceException;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.handler.*;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.FixedRate;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.model.impl.TaskImpl;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.isyfact.task.security.impl.SecurityAuthenticatorImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Der TaskHandler ist eine Werkzeugklasse für den Bau von Task-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class TaskHandlerImpl implements TaskHandler {
	private final static IsyLogger LOG = IsyLoggerFactory.getLogger(TaskHandlerImpl.class);

	/**
	 * Erzeugt einen neuen Task aus einem TaskData-Objekt
	 *
	 * @param taskData
	 * @return Task
	 */
	public synchronized Task createTask(TaskData taskData) throws CreateOperationInstanceException, HostNotApplicableException {
		HostHandler hostHandler = new HostHandlerImpl();
		if (hostHandler.isHostApplicable(taskData)) {
			SecurityHandler securityHandler
					= new SecurityHandlerImpl();
			SecurityAuthenticator securityAuthenticator = securityHandler.createSecurityAuthenticator(
					taskData.getUsername(),
					taskData.getPassword());

			LocalDateTime executionDateTime = fetchExecutionDateTime(taskData);

			OperationHandler operationHandler = new OperationHandlerImpl();
			Operation operation = operationHandler.createOperationInstance(taskData);

			FixedRateHandler fixedRateHandler = new FixedRateHandlerImpl(taskData);
			FixedRate fixedRate = fixedRateHandler.createFixedRate();

			operation.setFixedRate(fixedRate);

			Task task = new TaskImpl(
					taskData.getId(),
					securityAuthenticator,
					executionDateTime,
					operation
			);
			return task;
		} else {
			return null;
		}
	}

	/**
	 * Liefert den Zeitpunkt, bei dem der TaskData ausgeführt werden soll.
	 *
	 * @param taskData
	 * @return
	 */
	private synchronized LocalDateTime fetchExecutionDateTime(TaskData taskData) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
		String sExecutionDateTime = taskData.getExecutionDateTime();
		LocalDateTime executionDateTime = LocalDateTime.parse(sExecutionDateTime, dateTimeFormatter);
		LOG.debug("prepareTask: ", " executionDateTime: " + executionDateTime);
		return executionDateTime;
	}
}