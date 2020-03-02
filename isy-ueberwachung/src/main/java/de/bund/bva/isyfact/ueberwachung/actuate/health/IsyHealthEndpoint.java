package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Entkoppelter HealthEndpoint der den {@link HealthEndpoint} ersetzt und die {@link Health}-Informationen
 * aus dem {@link IsyHealthCache} abfragt.
 */
@Endpoint(id = "health")
public class IsyHealthEndpoint extends HealthEndpoint {

    /**
     * Default Wert für die eigene Health bevor der Cache das erste Mal aktualisiert wurde.
     */
    private static final Health DEFAULT_HEALTH = Health.unknown().build();

    private final HealthIndicator healthIndicator;
    private volatile IsyHealthCache cache;

    public IsyHealthEndpoint(HealthIndicator healthIndicator) {
        super(healthIndicator);
        this.healthIndicator = healthIndicator;

        // stellt sicher, dass Abfragen der eigenen Health nicht fehlschlagen, bevor der Cache das erste Mal befüllt wurde
        cache = new IsyHealthCache(DEFAULT_HEALTH);
    }

    @Override
    @ReadOperation
    public Health health() {
        return cache.getHealth();
    }

    @Override
    @ReadOperation
    public Health healthForComponent(@Selector String component) {
        return cache.getHealthForComponent(component);
    }

    @Override
    @ReadOperation
    public Health healthForComponentInstance(@Selector String component,
                                             @Selector String instance) {
        return cache.getHealthForComponentInstance(component, instance);
    }

    /**
     * Aktualisiert den Cache mit einem neuen, welcher die eigene Health und die aller vorhandenen Indicators enthält.
     * Die Abfrage der ineinander geschachtelten Indicators entspricht der in
     * {@link org.springframework.boot.actuate.health.HealthEndpoint#healthForComponent(String)} und
     * {@link org.springframework.boot.actuate.health.HealthEndpoint#healthForComponentInstance(String, String)}
     */
    public void aktualisiereCache() {
        cache = new IsyHealthCache(healthIndicator.health());
    }

}
