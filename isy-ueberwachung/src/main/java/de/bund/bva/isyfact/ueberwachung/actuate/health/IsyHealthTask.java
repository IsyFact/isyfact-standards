package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.model.AbstractTask;

/**
 * Dieser Task ersetzt die {@link Health}-Abfrage aus {@link org.springframework.boot.actuate.health.HealthEndpoint}
 * fuer alle vorhandenen Indicators und speichert diese in {@link IsyHealthCache}.
 */
public class IsyHealthTask extends AbstractTask {

    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(IsyHealthTask.class);

    private final HealthIndicator healthIndicator;
    private final IsyHealthCache cache;

    public IsyHealthTask(HealthIndicator healthIndicator, IsyHealthCache healthCache) {
        Assert.notNull(healthIndicator, "HealthIndicator must not be null");
        Assert.notNull(healthCache, "Cache must not be null");
        this.healthIndicator = healthIndicator;
        this.cache = healthCache;
    }

    @Override
    public void execute() {
        LOGISY.debug("Starte Health Caching");

        saveAllHealthInBuffer(healthIndicator);

        // nachdem im nextCache alles gespeichert wurde wird dieser live geschaltet
        cache.aktualisiereCacheMitBuffer();

        LOGISY.debug("Health Cache aktualisiert");
    }

    /**
     * Speichert die eigene Health, die der Components und die der Instances der Components im Buffer.
     * Die Abfrage der ineinander geschachtelten Indicators entspricht der in
     * {@link org.springframework.boot.actuate.health.HealthEndpoint#healthForComponent(String)} und
     * {@link org.springframework.boot.actuate.health.HealthEndpoint#healthForComponentInstance(String, String)}
     *
     * @param indicator Der Indicator der von dem aus alles gecasht werden soll.
     */
    private void saveAllHealthInBuffer(HealthIndicator indicator) {
        Health health = indicator.health();
        cache.putHealthInBuffer(health);

        for (Map.Entry<String, HealthIndicator> componentEntry : getRegistryEntriesIfComposite(indicator)) {
            String componentName = componentEntry.getKey();
            HealthIndicator componentIndicator = componentEntry.getValue();
            cache.putHealthForComponentInBuffer(componentName, componentIndicator.health());

            saveComponentInstancesInBuffer(componentName, componentIndicator);
        }
    }

    private void saveComponentInstancesInBuffer(String componentName, HealthIndicator componentIndicator) {
        for (Map.Entry<String, HealthIndicator> instanceEntry : getRegistryEntriesIfComposite(componentIndicator)) {
            String instanceName = instanceEntry.getKey();
            HealthIndicator instanceIndicator = instanceEntry.getValue();
            cache.putHealthForComponentInstanceInBuffer(componentName, instanceName, instanceIndicator.health());
        }
    }

    private Set<Map.Entry<String, HealthIndicator>> getRegistryEntriesIfComposite(HealthIndicator indicator) {
        if (indicator instanceof CompositeHealthIndicator) {
            return ((CompositeHealthIndicator) indicator).getRegistry().getAll().entrySet();
        } else {
            return Collections.emptySet();
        }
    }

    // package private Getter für IsyHealthEndpoint

    HealthIndicator getHealthIndicator() {
        return healthIndicator;
    }

    IsyHealthCache getCache() {
        return cache;
    }

}
