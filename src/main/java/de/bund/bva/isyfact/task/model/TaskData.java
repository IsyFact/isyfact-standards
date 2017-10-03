package de.bund.bva.isyfact.task.model;

/**
 * Ein TaskData Objekt kapselt die Zeichenketten-Properties für eine Aufgabe.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface TaskData {
	String getId();

	String getUsername();

	String getPassword();

	String getOperationName();

	String getHostName();

	String getExecutionDateTime();

	long getFixedRateDays();

	long getFixedRateHours();

	long getFixedRateMinutes();

	long getFixedRateSeconds();

	long getFixedDelayDays();

	long getFixedDelayHours();

	long getFixedDelayMinutes();

	long getFixedDelaySeconds();

}
