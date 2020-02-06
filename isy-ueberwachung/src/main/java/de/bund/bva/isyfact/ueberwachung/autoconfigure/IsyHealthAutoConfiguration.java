package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthCache;
import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthEndpoint;
import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthTask;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicatorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Auto-Configuration fuer den entkoppelten HealthEndpoint mit Cache.
 * Dieser ersetzt den Standard-HealthEndpoint von Spring und wird aufgrund der Conditionals nur aktiviert,
 * wenn der normale HealthEndpoint in den Properties aktiviert ist.
 */
@Configuration
@PropertySource("classpath:config/health.properties")
public class IsyHealthAutoConfiguration {

    @Bean
    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
    public IsyHealthCache isyHealthCache() {
        return new IsyHealthCache();
    }

    /**
     * Erstellung des Tasks analog zur Erstellung des Standard-HealthEndpoints in
     * {@link org.springframework.boot.actuate.autoconfigure.health.HealthEndpointConfiguration}.
     *
     * @param healthAggregator Der HealthAggregator von Spring
     * @param registry         Die HealthIndicatorRegistry von Spring
     * @param isyHealthCache   Der Cache indem die Health-Informationen gespeichert werden
     * @return IsyHealthTask-Instanz
     */
    @Bean
    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
    public IsyHealthTask isyHealthTask(HealthAggregator healthAggregator,
                                       HealthIndicatorRegistry registry,
                                       IsyHealthCache isyHealthCache) {
        return new IsyHealthTask(new CompositeHealthIndicator(healthAggregator, registry), isyHealthCache);
    }

    /**
     * Der IsyHealthEndpoint erhaelt vom Task die Referenzen auf den Cache und den HealthIndicator,
     * um auf die gecashten Health-Daten zugreifen zu koennen und den Standard-HealthEndpoint
     * ueberschreiben zu koennen.
     *
     * @param isyHealthTask Der Task mit den notwendigen Informationen
     * @return IsyHealthEndpoint-Instanz
     */
    @Bean
    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
    public IsyHealthEndpoint isyHealthEndpoint(IsyHealthTask isyHealthTask) {
        return new IsyHealthEndpoint(isyHealthTask);
    }

}
