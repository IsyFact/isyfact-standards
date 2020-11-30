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
 * This class creates and manages a cache for the {@link Health} of {@link HealthIndicator}.
 * <p/>
 * Using {@link #getAdaptedRegistry()} will return a wrapped instance the original registry linking it to the
 * cache so that any call to {@link HealthIndicator#health()} will be proxied by the cache.
 * <p/>
 * The cache can be updated using
 *
 * With
 * Diese Klasse, wrappt ein {@link HealthContributorRegistry} so, dass bei der Abfrage nach der Health eines
 * {@link HealthIndicator}s der gecachte Wert zurückgegeben wird.
 * <p/>
 * Der Cache wird von dem Wrapper verwaltet und kann über die Mehtode
 * {@link IsyHealthContributorRegistryCache#update()} aktualisiert werden.
 */
@AutoConfigureBefore
public class IsyHealthContributorRegistryCache {

    /**
     * Default Wert für die eigene Health bevor der Cache das erste Mal aktualisiert wurde.
     */
    private static final Health DEFAULT_HEALTH = Health.unknown().build();

    /**
     * Das Registry, das gecachet wird.
     */
    private final HealthContributorRegistry registry;

    /**
     * Die Wurzel des Caches.
     */
    private volatile CacheNode rootNode;

    /**
     * Konstruktor der Klasse.
     * @param registry Das {@link HealthContributorRegistry}, das gewrappt wird.
     */
    public IsyHealthContributorRegistryCache(HealthContributorRegistry registry) {
        Assert.notNull(registry, "Das Registry darf nicht null sein.");
        this.registry = registry;
        this.rootNode = new CacheNode();
    }

    public HealthContributorRegistry getAdaptedRegistry() {
        return new CachingHealthContributorRegistry();
    }

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

    /**
     * Hilfsklasse mit der eine Cachestruktor analog zum Registry aufgebaut wird.
     */
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
