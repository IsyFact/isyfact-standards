package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.jmx.TaskMonitor;

public abstract class AbstractTask implements Task {

    private final TaskMonitor monitor;

    protected AbstractTask() {
        this.monitor = null;
    }

    protected AbstractTask(TaskMonitor monitor) {
        this.monitor = monitor;
    }

    protected <T extends TaskMonitor> T getMonitor() {
        return monitor != null ? (T) monitor : null;
    }

    @Override
    public void zeichneErfolgreicheAusfuehrungAuf() {
        if (monitor != null) {
            monitor.zeichneErfolgreicheAusfuehrungAuf();
        }

    }

    @Override
    public void zeichneFehlgeschlageneAusfuehrungAuf(Exception fehler) {
        if (monitor != null) {
            monitor.zeichneFehlgeschlageneAusfuehrungAuf(fehler);
        }
    }
}
