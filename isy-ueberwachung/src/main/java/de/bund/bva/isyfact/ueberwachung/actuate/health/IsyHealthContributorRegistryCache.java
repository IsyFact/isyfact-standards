package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.health.contributor.CompositeHealthContributor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthContributor;
import org.springframework.boot.health.contributor.HealthContributors;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.health.registry.HealthContributorRegistry;


/**
 * This class serves as a cache for the individual {@link Health} results from the {@link HealthIndicator}s in
 * a {@link HealthContributorRegistry}.
 * <p/>
 * The method {@link #update(List)} will refresh the cache using the original registry hence synchronising
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

    /**
     * Retrieves the HealthContributor from the cache node structure.
     * @param contributor the HealthContributor to be found in the cache
     * @param name name of the contributor
     * @return the contributor stored in the cache under the given parameters
     */
    HealthContributor getContributor(HealthContributor contributor, String name) {
        return adapt(contributor, rootNode.getChild(name));
    }

    /**
     * Provides an iterator that traverses the tree structure in the cache to retrieve all contributor Entry objects.
     * @param contributors set of contributors
     * @return iterator iterating over all contributor entries in the cache
     */
    Iterator<HealthContributors.Entry> iterator(HealthContributors contributors) {

        return createAdapterIterator(contributors, rootNode);
    }

    /**
     * Updates the cache by walking the original registry and thereby always syncing the cache to the registry.
     */
    void update(List<HealthContributors.Entry> contributors) {
        CacheNode newRootNode = new CacheNode();
        for (HealthContributors.Entry contributorEntry : contributors) {
            update(contributorEntry, newRootNode);
        }
        rootNode = newRootNode;
    }

    private static void update(HealthContributors.Entry contributorEntry, CacheNode parentNode) {
        String name = contributorEntry.name();
        HealthContributor contributor = contributorEntry.contributor();

        CacheNode cacheNode = parentNode.makeChild(name);

        if (contributor instanceof CompositeHealthContributor composite) {
            for (HealthContributors.Entry healthContributorNamedContributor : composite) {
                update(healthContributorNamedContributor, cacheNode);
            }
        } else {
            cacheNode.health = ((HealthIndicator) contributor).health();
        }
    }

    private static final class CacheNode implements HealthIndicator {

        /**
         * Map associating a CacheNode to its name.
         */
        private Map<String, CacheNode> children;

        /**
         * Default value for a health status (default=unknown).
         */
        private Health health = DEFAULT_HEALTH;

        /**
         * Retrieves the CacheNodes child with the given name as key.
         * @param name name/key of the CacheNode
         * @return CacheNode
         */
        CacheNode getChild(String name) {
            if (children == null) {
                children = new HashMap<>();
            }
            return children.get(name);
        }

        /**
         * Adds a new child CacheNode.
         * @param name name/key of the added child CacheNode.
         * @return the newly added child CacheNode.
         */
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
            public Iterator<HealthContributors.Entry> iterator() {
                return createAdapterIterator(composite, cacheNode);
            }

            @Override
            public Stream<HealthContributors.Entry> stream() {
                return composite.stream().map(entry -> {
                    String name = entry.name();
                    CacheNode childNode = cacheNode != null
                            ? cacheNode.getChild(name)
                            : null;
                    return new HealthContributors.Entry(name, adapt(entry.contributor(), childNode));
                });
            }
        };
    }


    private static HealthIndicator adapt(CacheNode cacheNode) {
        if (cacheNode != null) {
            return cacheNode;
        }
        return () -> DEFAULT_HEALTH;
    }

    private static Iterator<HealthContributors.Entry> createAdapterIterator(
            Iterable<HealthContributors.Entry> composite, CacheNode cacheNode) {

        Iterator<HealthContributors.Entry> iterator = composite.iterator();
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public HealthContributors.Entry next() {
                HealthContributors.Entry next = iterator.next();
                String name = next.name();
                CacheNode childNode = null;
                if (cacheNode != null) {
                    childNode = cacheNode.getChild(name);
                }
                return new HealthContributors.Entry(name, adapt(next.contributor(), childNode));
            }

        };
    }
}
