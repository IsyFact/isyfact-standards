package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.Map;

import org.springframework.boot.actuate.health.Health;

/**
 * Cache für die Speicherung der {@link Health}-Informationen der Anwendung.
 * Ähnlich der Implementierung des {@link org.springframework.boot.actuate.health.HealthEndpoint} werden aus der
 * Gesamt-Health die Health der Component bzw. ComponentInstance abgefragt.
 */
public class IsyHealthCache {

    /**
     * Health der Anwendung, in deren Details die der weitern HealthIndicators gespeichert sind
     */
    private Health health;

    public IsyHealthCache(Health health) {
        this.health = health;
    }

    public Health getHealth() {
        return health;
    }

    public Health getHealthForComponent(String component) {
        return getNestedHealth(this.health, component);
    }

    public Health getHealthForComponentInstance(String component, String instance) {
        Health indicator = getNestedHealth(this.health, component);
        return getNestedHealth(indicator, instance);
    }

    private Health getNestedHealth(Health health, String name) {
        if (health != null) {
            Map<String, Object> details = health.getDetails();
            if (!details.isEmpty()) {
                return (Health) details.get(name);
            }
        }
        return null;
    }

}
