package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.exception.CreateOperationInstanceException;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.handler.*;
import de.bund.bva.isyfact.task.model.FixedDateTime;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.model.impl.TaskImpl;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

import java.time.LocalDateTime;

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
	    Task task = null;
		HostHandler hostHandler = new HostHandlerImpl();
		if (hostHandler.isHostApplicable(taskData)) {
			SecurityHandler securityHandler
					= new SecurityHandlerImpl();
			SecurityAuthenticator securityAuthenticator = securityHandler.createSecurityAuthenticator(
					taskData.getUsername(),
					taskData.getPassword());

			ExecutionDateTimeHandler executionDateTimeHandler = new ExecutionDateTimeHandlerImpl();
			LocalDateTime executionDateTime = executionDateTimeHandler.createExecutionDateTime(taskData);

			OperationHandler operationHandler = new OperationHandlerImpl();
			Operation operation = operationHandler.createOperationInstance(taskData);
			operation.setExecutionDateTime(executionDateTime);

			FixedDateTimeHandler fixedRateHandler =
					new FixedDateTimeHandlerImpl(
							taskData.getFixedRateDays(),
							taskData.getFixedRateHours(),
							taskData.getFixedRateMinutes(),
							taskData.getFixedRateSeconds()
					);
			FixedDateTime fixedRate = fixedRateHandler.createFixedDateTime();
			operation.setFixedRate(fixedRate);

			FixedDateTimeHandler fixedDelayHandler = new FixedDateTimeHandlerImpl(
					taskData.getFixedDelayDays(),
					taskData.getFixedDelayHours(),
					taskData.getFixedDelayMinutes(),
					taskData.getFixedDelaySeconds()
			);
			FixedDateTime fixedDelay = fixedDelayHandler.createFixedDateTime();
			operation.setFixedDelay(fixedDelay);

			task = new TaskImpl(
					taskData.getId(),
					securityAuthenticator,
					executionDateTime,
					operation
			);
		}
		return task;
	}
}