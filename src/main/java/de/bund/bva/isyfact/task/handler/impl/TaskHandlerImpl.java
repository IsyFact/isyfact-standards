package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.exception.CreateOperationInstanceException;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.handler.*;
import de.bund.bva.isyfact.task.model.FixedDateTime;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.impl.TaskImpl;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.springframework.context.ApplicationContext;

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
	 * @param konfiguration
	 * @param id
	 * @return
	 */
	@Override
	public synchronized Task createTask(String id, Konfiguration konfiguration, ApplicationContext applicationContext)
			throws CreateOperationInstanceException, HostNotApplicableException {

	    Task task = null;
		HostHandler hostHandler = new HostHandlerImpl();
		if (hostHandler.isHostApplicable(id, konfiguration)) {

			SecurityHandler securityHandler
					= new SecurityHandlerImpl();
			SecurityAuthenticator securityAuthenticator =
					securityHandler.getSecurityAuthenticator(id, konfiguration);

			OperationHandler operationHandler = new OperationHandlerImpl();
			Operation operation = operationHandler.getOperation(id, applicationContext);

			AusfuehrungsplanHandler ausfuehrungsplanHandler = new AusfuehrungsplanHandlerImpl();
			AusfuehrungsplanHandlerImpl.Ausfuehrungsplan ausfuehrungsplan = ausfuehrungsplanHandler.getAusfuehrungsplan(id, konfiguration);

			ExecutionDateTimeHandler executionDateTimeHandler = new ExecutionDateTimeHandlerImpl();
			LocalDateTime executionDateTime = executionDateTimeHandler.getExecutionDateTime(id, konfiguration);
			operation.setExecutionDateTime(executionDateTime);

			FixedRateHandler fixedRateHandler = new FixedRateHandlerImpl();
			FixedDateTime fixedRate = fixedRateHandler.getFixedRate(id, konfiguration);
			operation.setFixedRate(fixedRate);

			FixedDelayHandler fixedDelayHandler = new FixedDelayHandlerImpl();
			FixedDateTime fixedDelay = fixedDelayHandler.getFixedDelay(id, konfiguration);
			operation.setFixedDelay(fixedDelay);

			task = new TaskImpl(
					id,
					securityAuthenticator,
					operation,
					ausfuehrungsplan,
					executionDateTime
			);
		}
		return task;
	}
}