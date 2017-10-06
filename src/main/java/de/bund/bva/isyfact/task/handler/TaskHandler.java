package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.exception.CreateOperationInstanceException;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Der TaskHandler ist eine Werkzeugeklasse für Tasks.
 * 
 * 
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface TaskHandler {
	Task createTask(String id, Konfiguration konfiguration)
			throws CreateOperationInstanceException, HostNotApplicableException;
}
