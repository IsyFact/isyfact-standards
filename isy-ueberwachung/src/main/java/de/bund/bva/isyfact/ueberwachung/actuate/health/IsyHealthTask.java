package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.springframework.util.Assert;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.model.AbstractTask;

/**
 * Dieser Task aktualisiert den Cache in {@link IsyHealthContributorRegistryCache}.
 */
public class IsyHealthTask extends AbstractTask {

    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(IsyHealthTask.class);

    private final IsyHealthContributorRegistryCache isyHealthContributorRegistryCache;

    public IsyHealthTask(IsyHealthContributorRegistryCache isyHealthContributorRegistryCache) {
        Assert.notNull(isyHealthContributorRegistryCache,
        "IsyHealthContributorRegistryCache must not be null");
        this.isyHealthContributorRegistryCache = isyHealthContributorRegistryCache;
    }

    @Override
    public void execute() {
        LOGISY.debug("Starte Health Caching");

        isyHealthContributorRegistryCache.update();

        LOGISY.debug("Health Cache aktualisiert");
    }

}
