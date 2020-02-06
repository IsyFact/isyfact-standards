package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IsyHealthEndpointTest {

    private Health expectedHealth;
    private String component;
    private String instance;
    private String wrongComponent;
    private String wrongInstance;

    private IsyHealthEndpoint isyHealthEndpoint;
    private IsyHealthCache healthCacheMock;

    @Before
    public void init() {
        healthCacheMock = Mockito.mock(IsyHealthCache.class);

        isyHealthEndpoint = new IsyHealthEndpoint(new IsyHealthTask(Mockito.mock(HealthIndicator.class), healthCacheMock));

        expectedHealth = new Health.Builder().status(Status.UP).build();
        component = "irgendeine Komponente";
        instance = "irgendeine Instanz";
        wrongComponent = "eine falsche Komponente";
        wrongInstance = "eine falsche Instanz";
    }

    @Test
    public void testHealth() {
        Mockito.when(healthCacheMock.getHealth()).thenReturn(expectedHealth);

        assertEquals(expectedHealth, isyHealthEndpoint.health());
    }

    @Test
    public void testHealthForComponent() {
        Mockito.when(healthCacheMock.getHealthForComponent(component)).thenReturn(expectedHealth);

        assertEquals(expectedHealth, isyHealthEndpoint.healthForComponent(component));
        assertNull(isyHealthEndpoint.healthForComponent(wrongComponent));
    }

    @Test
    public void testHealthForComponentInstance() {
        Mockito.when(healthCacheMock.getHealthForComponentInstance(component, instance)).thenReturn(expectedHealth);

        assertEquals(expectedHealth, isyHealthEndpoint.healthForComponentInstance(component, instance));
        assertNull(isyHealthEndpoint.healthForComponentInstance(wrongComponent, wrongInstance));
    }

}
