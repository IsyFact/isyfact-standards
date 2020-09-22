package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicatorRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthEndpoint;
import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthTask;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemIndicator;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl.NachbarsystemCheckImpl;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemRestTemplateConfigurer;

/**
 * Auto-Configuration fuer den entkoppelten HealthEndpoint mit Cache.
 * Dieser ersetzt den Standard-HealthEndpoint von Spring und wird aufgrund der Conditionals nur aktiviert,
 * wenn der normale HealthEndpoint in den Properties aktiviert ist.
 */
@Configuration
@PropertySource("classpath:config/health.properties")
public class IsyHealthAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.ueberwachung")
    public NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties() {
        return new NachbarsystemConfigurationProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public NachbarsystemCheck nachbarsystemCheck(
        NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {

        //we want our own RestTemplate to set Properties accordingly
        //but it can be reused for all our subsequent requests (is threadsafe)
        //build a RestTemplate based on nachbarsystemConfigurationProperties
        RestTemplateBuilder templateBuilder = new RestTemplateBuilder();
        templateBuilder = NachbarsystemRestTemplateConfigurer
            .configureForNachbarSystemCheck(templateBuilder, nachbarsystemConfigurationProperties);
        RestTemplate restTemplate = templateBuilder.build();

        return new NachbarsystemCheckImpl(restTemplate);
    }

    @Bean
    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
    public NachbarsystemIndicator nachbarsystemIndicator(NachbarsystemCheck nachbarsystemCheck,
        NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        return new NachbarsystemIndicator(nachbarsystemCheck, nachbarsystemConfigurationProperties);
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class HealthEndpointConfiguration {

        /**
         * Erstellung des IsyHealthEndpoints analog zur Erstellung des Standard-HealthEndpoints in
         * {@link org.springframework.boot.actuate.autoconfigure.health.HealthEndpointConfiguration}.
         *
         * @param healthAggregator Der HealthAggregator von Spring
         * @param registry         Die HealthIndicatorRegistry von Spring
         * @return IsyHealthEndpoint-Instanz
         */
        @Bean
        @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
        public IsyHealthEndpoint isyHealthEndpoint(HealthAggregator healthAggregator,
                                                   HealthIndicatorRegistry registry) {
            return new IsyHealthEndpoint(new CompositeHealthIndicator(healthAggregator, registry));
        }

        /**
         * Erstellung eines Tasks, um den Cache des IsyHealthEndpoints zu aktualisieren.
         *
         * @param isyHealthEndpoint Der zu aktualisierende IsyHealthEndpoint
         * @return IsyHealthTask-Instanz
         */
        @Bean
        @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
        public IsyHealthTask isyHealthTask(IsyHealthEndpoint isyHealthEndpoint) {
            return new IsyHealthTask(isyHealthEndpoint);
        }

    }

}
