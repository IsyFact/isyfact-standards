package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.util.Assert;

/**
 * This class serves as a cache for the individual {@link Health} results from the {@link HealthIndicator}s in
 * a {@link HealthContributorRegistry}.
 * <p/>
 * Using the method {@link #getAdaptedRegistry()} will return wrapped instance of the original registry
 * linking it to the cache so that calls to {@link HealthIndicator#health()} will return the corresponding
 * cached value for the HealthIndicator.
 * <p/>
 * The method {@link #update()} will refresh the cache using the original registry hence synchronising the
 * cache and the registry in case the registry has been modified during runtime.
 */
@AutoConfigureBefore
public class IsyHealthContributorRegistryCache {

    /**
     * Default {@link Health} used as an initial value for the cache.
     */
    private static final Health DEFAULT_HEALTH = Health.unknown().build();

    /**
     * The registry for which the health values are cached for.
     */
    private final HealthContributorRegistry registry;

    /**
     * The root node of the cache.
     * (Analogous to the registry.)
     */
    private volatile CacheNode rootNode;

    /**
     * Constructor of the class.
     *
     * @param registry The {@link HealthContributorRegistry} for which the Health will be cached.
     */
    public IsyHealthContributorRegistryCache(HealthContributorRegistry registry) {
        Assert.notNull(registry, "Das Registry darf nicht null sein.");
        this.registry = registry;
        this.rootNode = new CacheNode();
    }

    /**
     * Adapts the original {@link HealthContributorRegistry} linking it to the cache, so that calls to
     * {@link HealthIndicator#health()} are replaces by the cached values.
     * <p/>
     * {@link HealthContributorRegistry#registerContributor(String, Object)} and
     * {@link HealthContributorRegistry#unregisterContributor(String)} remain uncached modifying the original
     * registry.
     * <p/>
     * {@link HealthContributor}s added to the adapted or original registry between two update calls will return
     * {@link #DEFAULT_HEALTH} until updated.
     *
     * @see #update()
     *
     * @return An adapted instance of the original registry linked to the cache.
     */
    public HealthContributorRegistry getAdaptedRegistry() {
        return new CachingHealthContributorRegistry();
    }

    /**
     * Updates the cache by walking the original registry and thereby always syncing the cache to the registry.
     */
    public void update() {
        CacheNode rootNode = new CacheNode();
        for (NamedContributor<HealthContributor> namedContributor : registry) {
            update(namedContributor, rootNode);
        }
        this.rootNode = rootNode;
    }

    private void update(NamedContributor<HealthContributor> namedContributor, CacheNode parentNode) {
        String name = namedContributor.getName();
        CacheNode cacheNode = parentNode.makeChild(name);
        HealthContributor contributor = namedContributor.getContributor();

        if (contributor instanceof CompositeHealthContributor) {
            CompositeHealthContributor composite = (CompositeHealthContributor) contributor;
            for (NamedContributor<HealthContributor> healthContributorNamedContributor : composite) {
                update(healthContributorNamedContributor, cacheNode);
            }
        } else {
            cacheNode.health = ((HealthIndicator) contributor).health();
        }
    }

    private static class CacheNode {

        private Map<String, CacheNode> children;

        private Health health = DEFAULT_HEALTH;

        CacheNode getChild(String name) {
            if (children == null) {
                children = new HashMap<>();
            }
            return children.get(name);
        }

        CacheNode makeChild(String name) {
            if (children == null) {
                children = new HashMap<>();
            }
            CacheNode node = new CacheNode();
            children.put(name, node);
            return node;
        }

        public void setHealth(Health health) {
            this.health = health;
        }

        public Health getHealth() {
            return health;
        }
    }

    private class CachingHealthContributorRegistry implements HealthContributorRegistry {

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
            return adapt(registry.getContributor(name), rootNode.getChild(name));
        }

        @Override
        public Iterator<NamedContributor<HealthContributor>> iterator() {
            return createAdapterIterator(registry, rootNode);
        }

        private HealthContributor adapt(HealthContributor healthContributor, CacheNode cacheNode) {
            if (healthContributor instanceof CompositeHealthContributor) {
                return adapt((CompositeHealthContributor) healthContributor, cacheNode);
            }
            return adapt((HealthIndicator) healthContributor, cacheNode);
        }

        private CompositeHealthContributor adapt(CompositeHealthContributor composite, CacheNode cacheNode) {
            return new CompositeHealthContributor() {
                @Override
                public HealthContributor getContributor(String name) {
                    CacheNode childNode = null;
                    if (cacheNode != null) {
                        childNode = cacheNode.getChild(name);
                    }
                    return adapt(composite.getContributor(name), childNode);
                }

                @Override
                public Iterator<NamedContributor<HealthContributor>> iterator() {
                    return createAdapterIterator(composite, cacheNode);
                }
            };
        }


        private HealthIndicator adapt(HealthIndicator healthIndicator, CacheNode cacheNode) {
            if (cacheNode != null) {
                return cacheNode::getHealth;
            }
            return () -> DEFAULT_HEALTH;
        }

        private Iterator<NamedContributor<HealthContributor>> createAdapterIterator(
                Iterable<NamedContributor<HealthContributor>> composite, CacheNode cacheNode) {
            Iterator<NamedContributor<HealthContributor>> iterator = composite.iterator();
            return new Iterator<NamedContributor<HealthContributor>>() {

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public NamedContributor<HealthContributor> next() {
                    NamedContributor<HealthContributor> next = iterator.next();
                    String name = next.getName();
                    CacheNode childNode = null;
                    if (cacheNode != null) {
                        childNode = cacheNode.getChild(name);
                    }
                    return NamedContributor.of(name, adapt(next.getContributor(), childNode));
                }

            };
        }
    }
}
