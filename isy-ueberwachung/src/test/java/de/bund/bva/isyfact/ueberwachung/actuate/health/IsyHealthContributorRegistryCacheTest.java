package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.CompositeHealthContributor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthContributor;
import org.springframework.boot.health.contributor.HealthContributors;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.health.contributor.Status;
import org.springframework.boot.health.registry.DefaultHealthContributorRegistry;
import org.springframework.boot.health.registry.HealthContributorRegistry;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Class for testing the functionality of {@link IsyHealthContributorRegistryCache} and the adapted registry it
 * it creates.
 */
public class IsyHealthContributorRegistryCacheTest {

    private static final Health UP_HEALTH = Health.up().build();

    private HealthContributorRegistry liveRegistry;

    private IsyCachingHealthContributorRegistry cachingRegistry;

    @BeforeEach
    public void setup() {
        liveRegistry = new DefaultHealthContributorRegistry();
        liveRegistry.registerContributor("A1", ContributorBuilder.indicator());
        liveRegistry.registerContributor("A2", ContributorBuilder.composite("B")
                .with(ContributorBuilder.indicator())
                .with(ContributorBuilder.composite("C")
                        .with(ContributorBuilder.indicator())
                        .with(ContributorBuilder.indicator())
                        .build())
                .build());
        liveRegistry.registerContributor("A3", ContributorBuilder.indicator());

        cachingRegistry = new IsyCachingHealthContributorRegistry(liveRegistry);
    }

    /**
     * Verifies that the structure of the live registry and the caching registry are identical.
     */
    @Test
    public void testVergleicheStrukture() {
        assertEquals(liveRegistry, cachingRegistry);
    }

    // 2 Register and Unregister

    /**
     * Verifies that adding an additional HealthContributor to the caching registry also adds it to the
     * original registry and that the health method of the Indicator is invoked when updating the cache.
     */
    @Test
    public void testCachingRegistryRegisterContributor() {
        HealthIndicator indicator = ContributorBuilder.indicator();
        cachingRegistry.registerContributor("HOLA", indicator);
        assertEquals(liveRegistry, cachingRegistry);
        cachingRegistry.updateCache();
        verify(indicator).health();
    }

    /**
     * Verifies that removing a HealthContributor from the caching registry also removes it from the live
     * registry and the health method of the Indicators is not invoked anymore when updating the cache
     * once the contributor was removed.
     */
    @Test
    public void testCachingRegistryUnregisterContributor() {
        HealthContributor healthContributor = cachingRegistry.unregisterContributor("A2");
        assertEquals(liveRegistry, cachingRegistry);
        cachingRegistry.updateCache();
        consumeIndicators(healthContributor, healthIndicator -> {
            verify(healthIndicator, never()).health();
        });
    }

    /**
     * Verifies that adding an additional HealthContributor to the live registry also adds it to the
     * caching registry and that the health method of the Indicator is invoked when updating the cache.
     */
    @Test
    public void testLiveRegistryRegisterContributor() {
        HealthIndicator indicator = ContributorBuilder.indicator();
        liveRegistry.registerContributor("HOLA", indicator);
        assertEquals(liveRegistry, cachingRegistry);
        cachingRegistry.updateCache();
        verify(indicator).health();
    }

    /**
     * Verifies that removing a HealthContributor from the live registry also removes it from the caching
     * registry and the health method of the Indicators is not invoked anymore when updating the cache
     * once the contributor was removed.
     */
    @Test
    public void testLiveRegistryUnregisterContributor() {
        HealthContributor healthContributor = liveRegistry.unregisterContributor("A2");
        assertEquals(liveRegistry, cachingRegistry);
        cachingRegistry.updateCache();
        consumeIndicators(healthContributor, healthIndicator -> {
            verify(healthIndicator, never()).health();
        });
    }

    /**
     * Verifies that the health methods of the caching registry all initially return {@link Status#UNKNOWN} and
     * the health method of the live HealthIndicators in the live registry have not been invoked.
     */
    @Test
    public void testInitialHealth() {
        // Assert that Health is initially UNKNOWN for the cachingRegistry
        consumeIndicators(cachingRegistry, healthIndicator -> {
            Assertions.assertEquals(Status.UNKNOWN, healthIndicator.health().getStatus());
        });

        // Assert that the mocked Indicators of the original Registry have not been called
        consumeIndicators(liveRegistry, healthIndicator -> {
            verify(healthIndicator, times(0)).health();
        });
    }

    /**
     * Verifies that calling the update method of the registry cache will invoke the health method on all the
     * HealthIndicators in the live registry and that the health of the cached indicators now returns
     * {@link Status#UP}.
     */
    @Test
    public void testUpdateCache() {
        // Update cached Indicator Values
        cachingRegistry.updateCache();

        // Assert that the mocked Indicators of the original Registry have been called
        consumeIndicators(liveRegistry, healthIndicator -> {
            verify(healthIndicator).health();
        });

        // Assert that the Health of all Indicators is now UP
        consumeIndicators(cachingRegistry, healthIndicator -> {
            Assertions.assertEquals(Status.UP, healthIndicator.health().getStatus());
        });
    }

    private void consumeIndicators(HealthContributorRegistry registry, Consumer<HealthIndicator> consumer) {
        for (HealthContributors.Entry contributorEntry : registry) {
            consumeIndicators(contributorEntry.contributor(), consumer);
        }
    }

    private void consumeIndicators(HealthContributor contributor, Consumer<HealthIndicator> consumer) {
        if (contributor instanceof CompositeHealthContributor composite) {
            for (HealthContributors.Entry contributorEntry : composite) {
                consumeIndicators(contributorEntry.contributor(), consumer);
            }
        } else {
            consumer.accept((HealthIndicator) contributor);
        }
    }

    private void assertEquals(HealthContributors contA, HealthContributors contB) {
        assertSubsetOf(contA.iterator(), contB.iterator());
        assertSubsetOf(contB.iterator(), contA.iterator());
    }

    @SuppressWarnings("unchecked")
    private void assertSubsetOfHealthContributor(HealthContributor contA, HealthContributor contB) {
        if (contA instanceof CompositeHealthContributor && contB instanceof CompositeHealthContributor) {
            assertSubsetOf(((CompositeHealthContributor) contA).iterator(), ((CompositeHealthContributor) contB).iterator());
        } else if (!(contA instanceof HealthIndicator && contB instanceof HealthIndicator)) {
            Assertions.fail("Die Strukturen ist nicht gleich");
        }
    }

    private void assertSubsetOf(Iterator<HealthContributors.Entry> iterA, Iterator<HealthContributors.Entry> iterB) {
        while (iterA.hasNext() && iterB.hasNext()) {
            HealthContributors.Entry nextA = iterA.next();
            HealthContributors.Entry nextB = iterB.next();
            Assertions.assertEquals(nextA.name(), nextB.name());
            assertSubsetOfHealthContributor(nextA.contributor(), nextB.contributor());
        }
        if (iterA.hasNext() || iterB.hasNext()) {
            Assertions.fail("Die Strukturen ist nicht gleich");
        }
    }

    private static class ContributorBuilder {
        static CompositeBuilder composite(String prefix) {
            return new CompositeBuilder(prefix);
        }

        static HealthIndicator indicator() {
            HealthIndicator mock = mock(HealthIndicator.class);
            doReturn(UP_HEALTH).when(mock).health();
            return mock;
        }

        static class CompositeBuilder {

            private final Map<String, HealthContributor> map = new LinkedHashMap<>();

            private final String prefix;

            private int count;

            private CompositeBuilder(String prefix) {
                this.prefix = prefix;
            }

            CompositeBuilder with(HealthContributor healthIndicator) {
                map.put(prefix + ++count, healthIndicator);
                return this;
            }

            CompositeHealthContributor build() {
                return CompositeHealthContributor.fromMap(map);
            }
        }
    }
}
