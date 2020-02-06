package de.bund.bva.isyfact.ueberwachung.actuate.health;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.actuate.health.Health;

/**
 * Cache fuer die Speicherung der {@link Health}-Informationen der Anwendung.
 * Beim befuellen des Caches werden die Daten zuerst in {@link #bufferedHealth} gespeichert.
 * Sobald dieser Cache komplett befuellt wurde kann er mit {@link #aktualisiereCacheMitBuffer()}
 * live geschaltet werden.
 */
public class IsyHealthCache {

    private Object selfKey = new Object();

    private Map<Object, Health> bufferedHealth = new HashMap<>();
    private Map<Object, Health> cachedHealth = new HashMap<>();

    /**
     * Ersetzt den aktuellen Cache mit den Health-Informationen aus dem Buffer.
     */
    public void aktualisiereCacheMitBuffer() {
        cachedHealth = bufferedHealth;
        bufferedHealth = new HashMap<>();
    }

    public Health getHealth() {
        return cachedHealth.get(selfKey);
    }

    public void putHealthInBuffer(Health health) {
        bufferedHealth.put(selfKey, health);
    }

    public Health getHealthForComponent(String component) {
        return cachedHealth.get(new ComponentKey(component));
    }

    public void putHealthForComponentInBuffer(String component, Health health) {
        bufferedHealth.put(new ComponentKey(component), health);
    }

    public Health getHealthForComponentInstance(String component, String instance) {
        return cachedHealth.get(new ComponentInstanceKey(component, instance));
    }

    public void putHealthForComponentInstanceInBuffer(String component, String instance, Health health) {
        bufferedHealth.put(new ComponentInstanceKey(component, instance), health);
    }

    /**
     * Cache-Key fuer den Component-Namen.
     */
    private static final class ComponentKey {

        private String component;

        private ComponentKey(String component) {
            this.component = component;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ComponentKey component1 = (ComponentKey) o;
            return Objects.equals(component, component1.component);
        }

        @Override
        public int hashCode() {
            return Objects.hash(component);
        }

    }

    /**
     * Cache-Key für den Component- und Instance-Namen.
     */
    private static final class ComponentInstanceKey {

        private String component;
        private String instance;

        private ComponentInstanceKey(String component, String instance) {
            this.component = component;
            this.instance = instance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ComponentInstanceKey that = (ComponentInstanceKey) o;
            return Objects.equals(component, that.component) &&
                    Objects.equals(instance, that.instance);
        }

        @Override
        public int hashCode() {
            return Objects.hash(component, instance);
        }

    }

}
