package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.Assert.*;

public class IsyHealthCacheTest {

    private Health expectedHealth;
    private String component;
    private String instance;
    private IsyHealthCache cache;

    @Before
    public void init() {
        cache = new IsyHealthCache();

        expectedHealth = new Health.Builder().status(Status.UP).build();
        component = "irgendeine Komponente";
        instance = "irgendeine Instanz";
    }

    @Test
    public void testGetHealthLeerInitial() {
        Health health = cache.getHealth();
        assertNull(health);
    }

    @Test
    public void testGetHealthLeerOhneBufferUpdate() {
        cache.putHealthInBuffer(expectedHealth);

        assertNull(cache.getHealth());
    }

    @Test
    public void testGetHealthVoll() {
        cache.putHealthInBuffer(expectedHealth);

        cache.aktualisiereCacheMitBuffer();

        assertEquals(expectedHealth, cache.getHealth());
    }

    @Test
    public void testGetHealthForComponentLeerInitial() {
        Health health = cache.getHealthForComponent(component);
        assertNull(health);
    }

    @Test
    public void testGetHealthForComponentLeerOhneBufferUpdate() {
        cache.putHealthForComponentInBuffer(component, expectedHealth);

        assertNull(cache.getHealthForComponent(component));
    }

    @Test
    public void testGetHealthForComponentVoll() {
        cache.putHealthForComponentInBuffer(component, expectedHealth);

        cache.aktualisiereCacheMitBuffer();

        assertEquals(expectedHealth, cache.getHealthForComponent(component));
    }

    @Test
    public void testGetHealthForComponentInstanceLeerInitial() {
        Health health = cache.getHealthForComponentInstance(component, instance);
        assertNull(health);
    }

    @Test
    public void testGetHealthForComponentInstanceLeerOhneBufferUpdate() {
        cache.putHealthForComponentInstanceInBuffer(component, instance, expectedHealth);

        assertNull(cache.getHealthForComponentInstance(component, instance));
    }

    @Test
    public void testGetHealthForComponentInstanceVoll() {
        cache.putHealthForComponentInstanceInBuffer(component, instance, expectedHealth);

        cache.aktualisiereCacheMitBuffer();

        assertEquals(expectedHealth, cache.getHealthForComponentInstance(component, instance));
    }

}
