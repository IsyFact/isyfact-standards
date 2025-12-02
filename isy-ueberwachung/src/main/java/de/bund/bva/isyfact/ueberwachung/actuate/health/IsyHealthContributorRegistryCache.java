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
import org.springframework.boot.actuate.health.NamedContributors;

/**
 * This class serves as a cache for the individual {@link Health} results from the {@link HealthIndicator}s in
 * a {@link HealthContributorRegistry}.
 * <p/>
 * The method {@link #update(NamedContributors)} will refresh the cache using the original registry hence synchronising
 * the cache and the registry in case the registry has been modified during runtime.
 */
class IsyHealthContributorRegistryCache {

    /**
     * Default {@link Health} used as an initial value for the cache.
     */
    private static final Health DEFAULT_HEALTH = Health.unknown().build();

    /**
     * The root node of the cache.
     * (Analogous to the registry.)
     */
    private volatile CacheNode rootNode = new CacheNode();

    HealthContributor getContributor(HealthContributor contributor, String name) {
        return adapt(contributor, rootNode.getChild(name));
    }

    Iterator<NamedContributor<HealthContributor>> iterator(NamedContributors<HealthContributor> contributors) {
        return createAdapterIterator(contributors, rootNode);
    }

    /**
     * Updates the cache by walking the original registry and thereby always syncing the cache to the registry.
     */
    void update(NamedContributors<HealthContributor> contributors) {
        CacheNode newRootNode = new CacheNode();
        for (NamedContributor<HealthContributor> namedContributor : contributors) {
            update(namedContributor, newRootNode);
        }
        rootNode = newRootNode;
    }

    private static void update(NamedContributor<HealthContributor> namedContributor, CacheNode parentNode) {
        String name = namedContributor.getName();
        HealthContributor contributor = namedContributor.getContributor();

        CacheNode cacheNode = parentNode.makeChild(name);

        if (contributor instanceof CompositeHealthContributor composite) {
            for (NamedContributor<HealthContributor> healthContributorNamedContributor : composite) {
                update(healthContributorNamedContributor, cacheNode);
            }
        } else {
            cacheNode.health = ((HealthIndicator) contributor).health();
        }
    }

    private static final class CacheNode implements HealthIndicator {

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

        @Override
        public Health health() {
            return health;
        }
    }

    private static HealthContributor adapt(HealthContributor healthContributor, CacheNode cacheNode) {
        if (healthContributor instanceof CompositeHealthContributor contributor) {
            return adapt(contributor, cacheNode);
        }
        return adapt(cacheNode);
    }

    private static CompositeHealthContributor adapt(CompositeHealthContributor composite, CacheNode cacheNode) {
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


    private static HealthIndicator adapt(CacheNode cacheNode) {
        if (cacheNode != null) {
            return cacheNode;
        }
        return () -> DEFAULT_HEALTH;
    }

    private static Iterator<NamedContributor<HealthContributor>> createAdapterIterator(
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
