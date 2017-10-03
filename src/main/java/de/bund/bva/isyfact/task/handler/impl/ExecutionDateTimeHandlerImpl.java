package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.*;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.TaskData;

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