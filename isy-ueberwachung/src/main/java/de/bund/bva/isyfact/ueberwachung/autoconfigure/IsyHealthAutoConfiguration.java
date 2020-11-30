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
 * Auto configuration for the {@link HealthEndpoint} and {@link HealthEndpointWebExtension} with a caching
 * {@link HealthContributorRegistry}.
 * These health endpoint replace the ones configured by spring and furthermore only activate when the usual
 * health endpoint would have been created.
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
         * Creates an instance of {@link IsyHealthContributorRegistryCache}.
         *
         * @param healthContributorRegistry The original registry for which the cache will be created.
         * @return An IsyHealthContributorRegistryCache instance.
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public IsyHealthContributorRegistryCache isyHealthContributorRegistry(
                HealthContributorRegistry healthContributorRegistry) {
            return new IsyHealthContributorRegistryCache(healthContributorRegistry);
        }

        /**
         * Creates an instance of {@link HealthEndpoint} which utilizes the adapted registry of
         * an {@link IsyHealthContributorRegistryCache}.
         *
         * @param registryCache
         *          The {@link IsyHealthContributorRegistryCache} which provides the caching registry.
         * @param groups
         *          The groups with which the endpoint will be configured.
         * @return HealthEndpoint instance.
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public HealthEndpoint healthEndpoint(
                IsyHealthContributorRegistryCache registryCache,
                HealthEndpointGroups groups) {
            return new HealthEndpoint(registryCache.getAdaptedRegistry(), groups);
        }


        /**
         * Creates an instance of {@link HealthEndpointWebExtension} which utilizes the adapted registry of
         * an {@link IsyHealthContributorRegistryCache}.
         *
         * @param registryCache
         *          The {@link IsyHealthContributorRegistryCache} which provides the caching registry.
         * @param groups
         *          The groups with which the endpoint will be configured.
         * @return HealthEndpointWebExtension instance.
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public HealthEndpointWebExtension healthEndpointWebExtension(
                IsyHealthContributorRegistryCache registryCache,
                HealthEndpointGroups groups) {
            return new HealthEndpointWebExtension(registryCache.getAdaptedRegistry(), groups);
        }

        /**
         * Creates a task which updates the cache in {@link IsyHealthContributorRegistryCache} in fixed
         * intervals.
         *
         * @param registryCache The {@link IsyHealthContributorRegistryCache} that is to be updated regularly.
         * @return IsyHealthTask instance
         */
        @Bean
        @ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
        public IsyHealthTask isyHealthTask(IsyHealthContributorRegistryCache registryCache) {
            return new IsyHealthTask(registryCache);
        }

    }

}
