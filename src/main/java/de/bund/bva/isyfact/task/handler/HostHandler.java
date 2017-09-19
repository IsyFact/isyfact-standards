package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.model.TaskData;

/**
 * Der HostHandler ist ein Werkzeug zur Überprüfung von Host-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface HostHandler {
    boolean isHostApplicable(TaskData taskData) throws HostNotApplicableException;
}
