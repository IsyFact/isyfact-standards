package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.springframework.context.ApplicationContext;

/**
 * Der TaskHandler ist eine Werkzeugeklasse für Tasks.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface TaskHandler {
    Task createTask(String id, Konfiguration konfiguration, ApplicationContext applicationContext)
        throws HostNotApplicableException;
}
