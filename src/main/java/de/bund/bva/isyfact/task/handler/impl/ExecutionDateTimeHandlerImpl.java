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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Der ExecutionDateTimeHandler ist eine Werkzeugklasse für das Festsetzen der Ausführungszeit eines Tasks.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class ExecutionDateTimeHandlerImpl implements ExecutionDateTimeHandler {
	private final static IsyLogger LOG = IsyLoggerFactory.getLogger(ExecutionDateTimeHandlerImpl.class);

	/**
	 * Liefert den Zeitpunkt, bei dem der Task ausgeführt werden soll.
	 *
	 * @param taskData
	 * @return
	 */
	@Override
	public synchronized LocalDateTime createExecutionDateTime(TaskData taskData) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
		String sExecutionDateTime = taskData.getExecutionDateTime();
		LocalDateTime executionDateTime = LocalDateTime.parse(sExecutionDateTime, dateTimeFormatter);
		LOG.debug("prepareTask: ", " executionDateTime: " + executionDateTime);
		return executionDateTime;
	}
}