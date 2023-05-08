package de.bund.bva.isyfact.konfiguration.common;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Task to reload the configuration. See {@link ReloadableKonfiguration#checkAndUpdate()}.
 */
@Component
public class KonfigurationUpdateTask {

    /** Configuration to be monitored with the task. */
    private final ReloadableKonfiguration konfiguration;

    public KonfigurationUpdateTask(ReloadableKonfiguration konfiguration) {
        this.konfiguration = konfiguration;
    }

    @Scheduled(fixedDelay = 300, timeUnit = TimeUnit.SECONDS)
    public void execute() {
        konfiguration.checkAndUpdate();
    }
}
