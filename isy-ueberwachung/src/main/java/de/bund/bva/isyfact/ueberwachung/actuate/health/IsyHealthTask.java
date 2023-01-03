package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

/**
 * This Task updates the cache in {@link IsyHealthContributorRegistryCache}.
 */
@Component
public class IsyHealthTask {

    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(IsyHealthTask.class);

    private final IsyCachingHealthContributorRegistry cachingHealthContributorRegistry;

    public IsyHealthTask(IsyCachingHealthContributorRegistry cachingHealthContributorRegistry) {
        Assert.notNull(cachingHealthContributorRegistry,
        "IsyCachingHealthContributorRegistry must not be null");
        this.cachingHealthContributorRegistry = cachingHealthContributorRegistry;
    }

    @Scheduled(fixedRate = 30, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void execute() {
        LOGISY.debug("Starte Health Caching");

        cachingHealthContributorRegistry.updateCache();

        LOGISY.debug("Health Cache aktualisiert");
    }

}
