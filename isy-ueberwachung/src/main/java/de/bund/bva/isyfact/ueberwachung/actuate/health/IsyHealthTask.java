package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.springframework.util.Assert;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.model.AbstractTask;

/**
 * This Task updates the cache in {@link IsyHealthContributorRegistryCache}.
 */
public class IsyHealthTask extends AbstractTask {

    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(IsyHealthTask.class);

    private final IsyCachingHealthContributorRegistry cachingHealthContributorRegistry;

    public IsyHealthTask(IsyCachingHealthContributorRegistry cachingHealthContributorRegistry) {
        Assert.notNull(cachingHealthContributorRegistry,
        "IsyCachingHealthContributorRegistry must not be null");
        this.cachingHealthContributorRegistry = cachingHealthContributorRegistry;
    }

    @Override
    public void execute() {
        LOGISY.debug("Starte Health Caching");

        cachingHealthContributorRegistry.updateCache();

        LOGISY.debug("Health Cache aktualisiert");
    }

}
