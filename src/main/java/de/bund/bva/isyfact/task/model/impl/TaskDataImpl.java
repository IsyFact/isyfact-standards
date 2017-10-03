package de.bund.bva.isyfact.task.model.impl;

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
	private final String operationName;
	private final String hostName;
	private final String executionDateTime;
	private final long fixedRateDays;
	private final long fixedRateHours;
	private final long fixedRateMinutes;
	private final long fixedRateSeconds;
	private final long fixedDelayDays;
	private final long fixedDelayHours;
	private final long fixedDelayMinutes;
	private final long fixedDelaySeconds;


	/**
	 *
	 * @param id
	 * @param username
	 * @param password
	 * @param operationName
	 * @param hostName
	 * @param executionDateTime
	 */
	public TaskDataImpl(
			String id,
			String username,
			String password,
			String operationName,
			String hostName,
			String executionDateTime
	) {
		this(
				id,
				username,
				password,
				operationName,
				hostName,
				executionDateTime,
				0L,
				0L,
				0L,
				0L);
	}

	/**
	 * @param id
	 * @param username
	 * @param password
	 * @param operationName
	 * @param hostName
	 * @param executionDateTime
	 * @param fixedRatedays
	 * @param fixedRateHours
	 * @param fixedRateMinutes
	 * @param fixedRateSeconds
	 */
	public TaskDataImpl(
			String id,
			String username,
			String password,
			String operationName,
			String hostName,
			String executionDateTime,
			long fixedRatedays,
			long fixedRateHours,
			long fixedRateMinutes,
			long fixedRateSeconds
	) {
		this(
				id,
				username,
				password,
				operationName,
				hostName,
				executionDateTime,
				fixedRatedays,
				fixedRateHours,
				fixedRateMinutes,
				fixedRateSeconds,
				0L,
				0L,
				0L,
				0L);
	}

	/**
	 *
	 * @param id
	 * @param username
	 * @param password
	 * @param operationName
	 * @param hostName
	 * @param executionDateTime
	 * @param fixedRatedays
	 * @param fixedRateHours
	 * @param fixedRateMinutes
	 * @param fixedRateSeconds
	 * @param fixedDelayDays
	 * @param fixedDelayHours
	 * @param fixedDelayMinutes
	 * @param fixedDelaySeconds
	 */
	public TaskDataImpl(
			String id,
			String username,
			String password,
			String operationName,
			String hostName,
			String executionDateTime,
			long fixedRatedays,
			long fixedRateHours,
			long fixedRateMinutes,
			long fixedRateSeconds,
			long fixedDelayDays,
			long fixedDelayHours,
			long fixedDelayMinutes,
			long fixedDelaySeconds
	) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.executionDateTime = executionDateTime;
		this.operationName = operationName;
		this.hostName = hostName;
		this.fixedRateDays = fixedRatedays;
		this.fixedRateHours = fixedRateHours;
		this.fixedRateMinutes = fixedRateMinutes;
		this.fixedRateSeconds = fixedRateSeconds;
		this.fixedDelayDays = fixedDelayDays;
		this.fixedDelayHours = fixedDelayHours;
		this.fixedDelayMinutes = fixedDelayMinutes;
		this.fixedDelaySeconds = fixedDelaySeconds;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getExecutionDateTime() {
		return executionDateTime;
	}

	@Override
	public String getOperationName() {
		return operationName;
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public long getFixedRateDays() {
		return fixedRateDays;
	}

	@Override
	public long getFixedRateHours() {
		return fixedRateHours;
	}

	@Override
	public long getFixedRateMinutes() {
		return fixedRateMinutes;
	}

	@Override
	public long getFixedRateSeconds() {
		return fixedRateSeconds;
	}

	@Override
	public long getFixedDelayDays() {
		return fixedDelayDays;
	}

	@Override
	public long getFixedDelayHours() {
		return fixedDelayHours;
	}

	@Override
	public long getFixedDelayMinutes() {
		return fixedDelayMinutes;
	}

	@Override
	public long getFixedDelaySeconds() {
		return fixedDelaySeconds;
	}
}
