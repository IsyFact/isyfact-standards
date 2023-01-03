package de.bund.bva.isyfact.konfiguration.common;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Task zum Neuladen der Konfiguration. Siehe {@link ReloadableKonfiguration#checkAndUpdate()}.
 */
@Component
public class KonfigurationUpdateTask {

    /** Konfiguration, die mit dem Task überwacht werden soll. */
    private final ReloadableKonfiguration konfiguration;

    public KonfigurationUpdateTask(ReloadableKonfiguration konfiguration) {
        this.konfiguration = konfiguration;
    }

    @Scheduled(fixedDelay = 300, timeUnit = TimeUnit.SECONDS)
    public void execute() {
        konfiguration.checkAndUpdate();
    }
}
