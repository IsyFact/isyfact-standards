package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.exception.CreateOperationInstanceException;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.isyfact.task.model.TaskData;

/**
 * Der OperationHandler ist ein Werkzeug für den Bau von Operation-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface OperationHandler {
    Operation createOperationInstance(TaskData taskData) throws CreateOperationInstanceException;
}
