package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.ExecutionDateTimeHandler;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.EXECUTIONDATETIME;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;

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
	 * @return
	 */
	@Override
	public synchronized LocalDateTime getExecutionDateTime(String id, Konfiguration konfiguration) {
		String executionDateTime = konfiguration.getAsString(PRAEFIX + id + EXECUTIONDATETIME);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
		return LocalDateTime.parse(executionDateTime, dateTimeFormatter);
	}
}