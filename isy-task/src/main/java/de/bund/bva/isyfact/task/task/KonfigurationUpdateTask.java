package de.bund.bva.isyfact.task.task;

import de.bund.bva.isyfact.task.model.AbstractTask;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.konfiguration.common.ReloadableKonfiguration;

/**
 * Task zum Neuladen der Konfiguration. Siehe {@link ReloadableKonfiguration#checkAndUpdate()}.
 */
@Deprecated
public class KonfigurationUpdateTask extends AbstractTask {

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

