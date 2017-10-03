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

	String getExecutionDateTime();

	String getOperationName();

	String getHostName();

    String getDays();

    String getHours();

    String getMinutes();

    String getSeconds();
}
