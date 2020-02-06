package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;

/**
 * Entkoppelter HealthEndpoint der den {@link HealthEndpoint} ersetzt und die {@link Health}-Informationen
 * aus dem {@link IsyHealthCache} abfragt.
 */
@Endpoint(id = "health")
public class IsyHealthEndpoint extends HealthEndpoint {

    private final IsyHealthCache cache;

    public IsyHealthEndpoint(IsyHealthTask isyHealthTask) {
        super(isyHealthTask.getHealthIndicator());
        this.cache = isyHealthTask.getCache();
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

}
