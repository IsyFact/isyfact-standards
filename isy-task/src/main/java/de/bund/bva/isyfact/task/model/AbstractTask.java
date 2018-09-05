package de.bund.bva.isyfact.task.model;

/**
 * Abstrakte Basis-Klasse die für alle Methoden von {@link Task} bis auf {@link Task#execute()} eine
 * Standardimplementierung mitbringt.
 */
public abstract class AbstractTask implements Task {

    private final TaskMonitor monitor;

    private boolean deaktiviert = false;

    protected AbstractTask() {
        this.monitor = null;
    }

    protected AbstractTask(TaskMonitor monitor) {
        this.monitor = monitor;
        this.monitor.setTask(this);
    }

    protected <T extends TaskMonitor> T getMonitor() {
        return monitor != null ? (T) monitor : null;
    }

    @Override
    public synchronized boolean isDeaktiviert() {
        return deaktiviert;
    }

    @Override
    public synchronized void aktivieren() {
        deaktiviert = false;
    }

    @Override
    public synchronized void deaktivieren() {
        deaktiviert = true;
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
