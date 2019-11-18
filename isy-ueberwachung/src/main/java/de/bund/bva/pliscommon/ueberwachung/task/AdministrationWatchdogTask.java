package de.bund.bva.pliscommon.ueberwachung.task;

import org.springframework.beans.factory.annotation.Required;

import de.bund.bva.isyfact.task.model.AbstractTask;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.pliscommon.ueberwachung.admin.Watchdog;

/**
 * Task zur regelmäßigen Überprüfung der Verfügbarkeit von Nachbarsystemen.
 */
public class AdministrationWatchdogTask extends AbstractTask {
    /** Der Anwendungs-Watchdog. */
    private Watchdog administrationWatchdog;

    public AdministrationWatchdogTask(TaskMonitor monitor) {
        super(monitor);
    }

    @Override
    public void execute() {
        this.administrationWatchdog.pruefeSystem();
    }

    @Required
    public void setAdministrationWatchdog(Watchdog administrationWatchdog) {
        this.administrationWatchdog = administrationWatchdog;
    }
}