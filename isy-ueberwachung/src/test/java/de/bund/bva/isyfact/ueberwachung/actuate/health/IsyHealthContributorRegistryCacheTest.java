package de.bund.bva.isyfact.ueberwachung.actuate.health;

import static org.mockito.Mockito.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.DefaultHealthContributorRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.actuate.health.NamedContributors;
import org.springframework.boot.actuate.health.Status;

/**
 *
 */
public class IsyHealthContributorRegistryCacheTest {

    private static final Health UP_HEALTH = Health.up().build();

    HealthContributorRegistry liveRegistry;

    IsyHealthContributorRegistryCache registryCache;

    HealthContributorRegistry cachingRegistry;

    @Before
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

        this.registryCache = new IsyHealthContributorRegistryCache(liveRegistry);
        this.cachingRegistry = registryCache.getAdaptedRegistry();
    }

    // 1 compare Structures
    @Test
    public void testVergleicheStrukture() {
        assertSubsetOf(liveRegistry, cachingRegistry);
        assertSubsetOf(cachingRegistry, liveRegistry);
    }

    // 2 Register and Unregister
    @Test
    public void testCachingRegistryRegisterContributor() {
        HealthIndicator indicator = ContributorBuilder.indicator();
        cachingRegistry.registerContributor("HOLA", indicator);
        assertSubsetOf(liveRegistry, cachingRegistry);
        registryCache.update();
        verify(indicator).health();
    }

    @Test
    public void testCachingRegistryUnregisterContributor() {
        HealthContributor healthContributor = cachingRegistry.unregisterContributor("A2");
        assertSubsetOf(liveRegistry, cachingRegistry);
        registryCache.update();
        consumeIndicators(healthContributor, healthIndicator -> {
            verify(healthIndicator, Mockito.never()).health();
        });
    }

    @Test
    public void testLiveRegistryRegisterContributor() {
        HealthIndicator indicator = ContributorBuilder.indicator();
        liveRegistry.registerContributor("HOLA", indicator);
        assertSubsetOf(cachingRegistry, liveRegistry);
        registryCache.update();
        verify(indicator).health();
    }

    @Test
    public void testLiveRegistryUnregisterContributor() {
        HealthContributor healthContributor = liveRegistry.unregisterContributor("A2");
        assertSubsetOf(cachingRegistry, liveRegistry);
        registryCache.update();
        consumeIndicators(healthContributor, healthIndicator -> {
            verify(healthIndicator, Mockito.never()).health();
        });
    }

    // 3 Check Health -> Update -> Check Health
    @Test
    public void testInitialHealth() {
        // Assert that Health is initially UNKNOWN for the cachingRegistry
        consumeIndicators(cachingRegistry, healthIndicator -> {
            Assert.assertEquals(Status.UNKNOWN, healthIndicator.health().getStatus());
        });

        // Assert that the mocked Indicators of the original Registry have not been called
        consumeIndicators(liveRegistry, healthIndicator -> {
            verify(healthIndicator, times(0)).health();
        });
    }

    @Test
    public void testUpdateCache() {
        // Update cached Indicator Values
        registryCache.update();

        // Assert that the mocked Indicators of the original Registry have been called
        consumeIndicators(liveRegistry, healthIndicator -> {
            verify(healthIndicator).health();
        });

        // Assert that the Health of all Indicators is now UP
        consumeIndicators(cachingRegistry, healthIndicator -> {
            Assert.assertEquals(Status.UP, healthIndicator.health().getStatus());
        });
    }

    private void consumeIndicators(HealthContributorRegistry registry, Consumer<HealthIndicator> consumer) {
        for (NamedContributor<HealthContributor> namedContributor : registry) {
            consumeIndicators(namedContributor.getContributor(), consumer);
        }
    }

    private void consumeIndicators(HealthContributor contributor, Consumer<HealthIndicator> consumer) {
        if (contributor instanceof CompositeHealthContributor) {
            CompositeHealthContributor composite = (CompositeHealthContributor) contributor;
            for (NamedContributor<HealthContributor> namedContributor : composite) {
                consumeIndicators(namedContributor.getContributor(), consumer);
            }
        } else {
            consumer.accept((HealthIndicator) contributor);
        }
    }

    @SuppressWarnings("unchecked")
    private void assertSubsetOf(HealthContributor contA, HealthContributor contB) {
        if (contA instanceof NamedContributors && contB instanceof NamedContributors) {
            assertSubsetOf((NamedContributors<HealthContributor>) contA, (NamedContributors<HealthContributor>) contB);
        } else if (!(contA instanceof HealthIndicator && contB instanceof HealthIndicator)) {
            Assert.fail("Die Strukturen ist nicht gleich");
        }
    }

    private void assertSubsetOf(NamedContributors<HealthContributor> contA, NamedContributors<HealthContributor> contB) {
        Iterator<NamedContributor<HealthContributor>> iterA = contA.iterator();
        Iterator<NamedContributor<HealthContributor>> iterB = contB.iterator();
        while (iterA.hasNext() && iterB.hasNext()) {
            NamedContributor<HealthContributor> nextA = iterA.next();
            NamedContributor<HealthContributor> nextB = iterB.next();
            Assert.assertEquals(nextA.getName(), nextB.getName());
            assertSubsetOf(nextA.getContributor(), nextB.getContributor());
        }
        if (iterA.hasNext() || iterB.hasNext()) {
            Assert.fail("Die Strukturen ist nicht gleich");
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

            private Map<String, HealthContributor> map = new LinkedHashMap<>();

            private String prefix;

            private int count = 0;

            private CompositeBuilder(String prefix) {
                this.prefix = prefix;
            }

            CompositeBuilder with(HealthContributor healthIndicator) {
                this.map.put(prefix + (++count), healthIndicator);
                return this;
            }

            CompositeHealthContributor build() {
                return CompositeHealthContributor.fromMap(map);
            }
        }
    }
}
