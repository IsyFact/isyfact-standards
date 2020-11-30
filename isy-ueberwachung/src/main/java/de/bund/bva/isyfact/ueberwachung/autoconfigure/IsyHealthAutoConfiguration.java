package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthEndpointGroups;
import org.springframework.boot.actuate.health.HealthEndpointWebExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthContributorRegistryCache;
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
    @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
    public NachbarsystemIndicator nachbarsystemIndicator(NachbarsystemCheck nachbarsystemCheck,
        NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        return new NachbarsystemIndicator(nachbarsystemCheck, nachbarsystemConfigurationProperties);
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class HealthEndpointConfiguration {

        /**
         * Erstellung eines {@link IsyHealthContributorRegistryCache}s, der ein
         * {@link HealthContributorRegistry} mit einem Caching-Mechanismus erweitert.
         *
         * @param healthContributorRegistry Das zu wrappende {@link HealthContributorRegistry}.
         * @return IsyHealthContributorRegistryCache-Instanz.
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public IsyHealthContributorRegistryCache isyHealthContributorRegistry(
                HealthContributorRegistry healthContributorRegistry) {
            return new IsyHealthContributorRegistryCache(healthContributorRegistry);
        }

        /**
         * Erstellung eines {@link HealthEndpoint}s, der einem mit caching erweiterten
         * {@link HealthContributorRegistry} verwendet.
         *
         * @param cachingWrapper Die Wrapper, der ein cachendes {@link HealthContributorRegistry} bereitstellt.
         * @param groups Die Groups mit denen der Endpoint konfiguriert wird.
         * @return HealthEndpoint-Instanz.
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public HealthEndpoint healthEndpoint(
                IsyHealthContributorRegistryCache cachingWrapper,
                HealthEndpointGroups groups) {
            return new HealthEndpoint(cachingWrapper.getAdaptedRegistry(), groups);
        }


        /**
         * Erstellung eines {@link HealthEndpointWebExtension}s, der einem mit caching erweiterten
         * {@link HealthContributorRegistry} verwendet.
         *
         * @param cachingWrapper Die Wrapper, der ein cachendes {@link HealthContributorRegistry} bereitstellt.
         * @param groups Die Groups mit denen der Endpoint konfiguriert wird.
         * @return HealthEndpointWebExtension-Instanz.
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public HealthEndpointWebExtension healthEndpointWebExtension(
                IsyHealthContributorRegistryCache cachingWrapper,
                HealthEndpointGroups groups) {
            return new HealthEndpointWebExtension(cachingWrapper.getAdaptedRegistry(), groups);
        }

        /**
         * Erstellung eines Tasks, um den Cache des {@link IsyHealthContributorRegistryCache} zu
         * aktualisieren.
         *
         * @param cachingWrapper Der zu aktualisierende {@link IsyHealthContributorRegistryCache}
         * @return IsyHealthTask-Instanz
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public IsyHealthTask isyHealthTask(IsyHealthContributorRegistryCache cachingWrapper) {
            return new IsyHealthTask(cachingWrapper);
        }

    }

}
