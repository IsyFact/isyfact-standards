package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthCache;
import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthEndpoint;
import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthTask;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemIndicator;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl.NachbarsystemCheckImpl;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicatorRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.TomcatHttpHandlerAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;

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
    public WebClient webClient() {
        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector();
        return  WebClient.builder().clientConnector(clientHttpConnector).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public NachbarsystemCheck nachbarsystemCheck(WebClient webClient) {
        return new NachbarsystemCheckImpl(webClient);
    }

    @Bean
    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
    public NachbarsystemIndicator nachbarsystemIndicator(NachbarsystemCheck nachbarsystemCheck,
        NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        return new NachbarsystemIndicator(nachbarsystemCheck, nachbarsystemConfigurationProperties);
    }

    @Bean
    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
    public IsyHealthCache isyHealthCache() {
        return new IsyHealthCache();
    }

//    /**
//     * Erstellung des Tasks analog zur Erstellung des Standard-HealthEndpoints in
//     * {@link org.springframework.boot.actuate.autoconfigure.health.HealthEndpointConfiguration}.
//     *
//     * @param healthAggregator Der HealthAggregator von Spring
//     * @param registry         Die HealthIndicatorRegistry von Spring
//     * @param isyHealthCache   Der Cache indem die Health-Informationen gespeichert werden
//     * @return IsyHealthTask-Instanz
//     */
//    @Bean
//    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
//    public IsyHealthTask isyHealthTask(HealthAggregator healthAggregator,
//        HealthIndicatorRegistry registry,
//        IsyHealthCache isyHealthCache) {
//        return new IsyHealthTask(new CompositeHealthIndicator(healthAggregator, registry), isyHealthCache);
//    }
//
//    /**
//     * Der IsyHealthEndpoint erhaelt vom Task die Referenzen auf den Cache und den HealthIndicator,
//     * um auf die gecashten Health-Daten zugreifen zu koennen und den Standard-HealthEndpoint
//     * ueberschreiben zu koennen.
//     *
//     * @param isyHealthTask Der Task mit den notwendigen Informationen
//     * @return IsyHealthEndpoint-Instanz
//     */
//    @Bean
//    @ConditionalOnEnabledEndpoint(endpoint = HealthEndpoint.class)
//    public IsyHealthEndpoint isyHealthEndpoint(IsyHealthTask isyHealthTask) {
//        return new IsyHealthEndpoint(isyHealthTask);
//    }

}
