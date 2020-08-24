package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicatorRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthEndpoint;
import de.bund.bva.isyfact.ueberwachung.actuate.health.IsyHealthTask;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemIndicator;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl.NachbarsystemCheckImpl;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import reactor.netty.http.client.HttpClient;

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
    public WebClient webClient(
        NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        long timeoutInMillis =
            nachbarsystemConfigurationProperties.getNachbarsystemCheck().getTimeout().toMillis();
        if (timeoutInMillis > (long) Integer.MAX_VALUE) { //damit beim Cast keine Probleme auftreten
            //fachlich gesehen sollte der Wert ohnehin deutlich kleiner sein
            throw new IllegalArgumentException(
                String.format("WebClient wurde nicht erstellt. Timeout zu lang: %s ms", timeoutInMillis));
        }

        // Netty Http-Client erstellen
        HttpClient httpClient = HttpClient.create()
            .tcpConfiguration(tcpClient -> {
                tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutInMillis);
                tcpClient = tcpClient.doOnConnected(conn -> conn
                    .addHandlerLast(new ReadTimeoutHandler(timeoutInMillis, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(timeoutInMillis, TimeUnit.MILLISECONDS)));
                return tcpClient;
            });
        // Http-Connecter basierend auf dem Http-Client
        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder().clientConnector(clientHttpConnector).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public NachbarsystemCheck nachbarsystemCheck(WebClient webClient,
        NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        return new NachbarsystemCheckImpl(webClient, nachbarsystemConfigurationProperties);
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
