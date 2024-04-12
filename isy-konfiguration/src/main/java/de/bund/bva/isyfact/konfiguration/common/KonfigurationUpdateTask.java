package de.bund.bva.isyfact.konfiguration.common;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
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
