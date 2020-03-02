package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test für den initialen Zustand des Endpoints
 */
public class IsyHealthEndpointTest {

    private Health healthUnknown;
    private String component;
    private String instance;

    private IsyHealthEndpoint isyHealthEndpoint;

    @Before
    public void init() {
        isyHealthEndpoint = new IsyHealthEndpoint(mock(HealthIndicator.class));

        healthUnknown = new Health.Builder().status(Status.UNKNOWN).build();
        component = "irgendeine Komponente";
        instance = "irgendeine Instanz";
    }

    @Test
    public void testHealthInitial() {
        assertEquals(healthUnknown, isyHealthEndpoint.health());
        assertNull(isyHealthEndpoint.healthForComponent(component));
        assertNull(isyHealthEndpoint.healthForComponentInstance(component, instance));
    }

}
