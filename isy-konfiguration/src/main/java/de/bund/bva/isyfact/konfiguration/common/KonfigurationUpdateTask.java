package de.bund.bva.isyfact.konfiguration.common;

import de.bund.bva.isyfact.task.model.AbstractTask;
import de.bund.bva.isyfact.task.model.TaskMonitor;

/**
 * Task zum Neuladen der Konfiguration. Siehe {@link ReloadableKonfiguration#checkAndUpdate()}.
 */
public class KonfigurationUpdateTask extends AbstractTask {

    /** Konfiguration, die mit dem Task überwacht werden soll. */
    private final ReloadableKonfiguration konfiguration;

    public KonfigurationUpdateTask(TaskMonitor monitor, ReloadableKonfiguration konfiguration) {
        super(monitor);
        this.konfiguration = konfiguration;
    }

    @Override
    public void execute() {
        konfiguration.checkAndUpdate();
    }
}
