package de.bund.bva.isyfact.task.model.impl;

import de.bund.bva.isyfact.task.handler.FixedRateHandler;
import de.bund.bva.isyfact.task.handler.impl.FixedRateHandlerImpl;
import de.bund.bva.isyfact.task.model.TaskData;

/**
 * Eine CallableOperation enthält die Anweisungen, die erledigt werden sollen.
 * Diese werden in einer call-Methode implementiert.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class TaskDataImpl implements TaskData {
	private final String id;
	private final String username;
	private final String password;
	private final String executionDateTime;
	private final String operationName;
	private final String hostName;
	private final String days;
	private final String hours;
	private final String minutes;
	private final String seconds;

	/**
	 *
	 * @param id
	 * @param username
	 * @param password
	 * @param executionDateTime
	 * @param operationName
	 * @param hostName
	 * @param days
	 * @param hours
	 * @param minutes
	 * @param seconds
	 */
	public TaskDataImpl(
			String id,
			String username,
			String password,
			String executionDateTime,
			String operationName,
			String hostName,
			String days,
			String hours,
			String minutes,
			String seconds) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.executionDateTime = executionDateTime;
		this.operationName = operationName;
		this.hostName = hostName;
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}

	@Override
	public synchronized String getId() {
		return id;
	}

	@Override
	public synchronized String getUsername() {
		return username;
	}

	@Override
	public synchronized String getPassword() {
		return password;
	}

	@Override
	public synchronized String getExecutionDateTime() {
		return executionDateTime;
	}

	@Override
	public synchronized String getOperationName() {
		return operationName;
	}

	@Override
	public synchronized String getHostName() {
		return hostName;
	}

	@Override
	public synchronized String getDays() {
		return days;
	}

	@Override
	public synchronized String getHours() {
		return hours;
	}

	@Override
	public synchronized String getMinutes() {
		return minutes;
	}

	@Override
	public synchronized String getSeconds() {
		return seconds;
	}
}
