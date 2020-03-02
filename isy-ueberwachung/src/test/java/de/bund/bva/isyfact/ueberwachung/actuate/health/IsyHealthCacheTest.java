package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.Assert.*;

public class IsyHealthCacheTest {

    private String component;
    private String instance;

    @Before
    public void init() {
        component = "irgendeine Komponente";
        instance = "irgendeine Instanz";
    }

    @Test
    public void testNurEigeneHealthGesetzt() {
        Health expectedHealth = Health.up().build();
        IsyHealthCache cache = new IsyHealthCache(expectedHealth);

        assertEquals(expectedHealth, cache.getHealth());
        assertNull(cache.getHealthForComponent(component));
        assertNull(cache.getHealthForComponentInstance(component, instance));
    }

    @Test
    public void testComponentUndComponentInstanceGesetzt() {
        IsyHealthCache cache = new IsyHealthCache(
                new Health.Builder(Status.DOWN)
                        .withDetail(component, new Health.Builder(Status.UP)
                                .withDetail(instance, new Health.Builder(Status.DOWN).build()).build()).build());

        assertEquals(Status.DOWN, cache.getHealth().getStatus());
        assertEquals(Status.UP, cache.getHealthForComponent(component).getStatus());
        assertEquals(Status.DOWN, cache.getHealthForComponentInstance(component, instance).getStatus());
    }

}
