package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.Iterator;

import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;

/**
 * Adapts the original {@link HealthContributorRegistry} linking it to the cache, so that calls to
 * {@link HealthIndicator#health()} are replaces by the cached values.
 * <p/>
 * {@link HealthContributorRegistry#registerContributor(String, Object)} and
 * {@link HealthContributorRegistry#unregisterContributor(String)} remain uncached modifying the original
 * registry.
 * <p/>
 * {@link HealthContributor}s added to the adapted or original registry between two update calls will return
 * {@link org.springframework.boot.actuate.health.Status#UNKNOWN} until updated.
 *
 * @see #updateCache()
 */
public class IsyCachingHealthContributorRegistry implements HealthContributorRegistry {
    /**
     * The registry for which the health values are cached for.
     */
    private final HealthContributorRegistry registry;

    private final IsyHealthContributorRegistryCache cache = new IsyHealthContributorRegistryCache();

    /**
     * Constructor of the class.
     *
     * @param registry The {@link HealthContributorRegistry} for which the Health will be cached.
     */
    public IsyCachingHealthContributorRegistry(HealthContributorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void registerContributor(String name, HealthContributor contributor) {
        registry.registerContributor(name, contributor);
    }

    @Override
    public HealthContributor unregisterContributor(String name) {
        return registry.unregisterContributor(name);
    }

    @Override
    public HealthContributor getContributor(String name) {
        return cache.getContributor(registry.getContributor(name), name);
    }

    @Override
    public Iterator<NamedContributor<HealthContributor>> iterator() {
        return cache.iterator(registry);
    }

    /**
     * Updates the cache by walking the original registry and thereby always syncing the cache to the registry.
     */
    public void updateCache() {
        cache.update(registry);
    }
}
