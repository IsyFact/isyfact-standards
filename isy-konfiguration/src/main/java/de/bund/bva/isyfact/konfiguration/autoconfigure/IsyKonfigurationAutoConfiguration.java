package de.bund.bva.isyfact.konfiguration.autoconfigure;

import de.bund.bva.isyfact.konfiguration.common.Konfiguration;
import de.bund.bva.isyfact.konfiguration.common.impl.ReloadablePropertyKonfiguration;
import de.bund.bva.isyfact.konfiguration.config.IsyKonfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
@Configuration
@EnableConfigurationProperties
public class IsyKonfigurationAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.konfiguration")
    public IsyKonfigurationProperties isyKonfigurationProperties() {
        return new IsyKonfigurationProperties();
    }

    @Bean
    @ConditionalOnProperty("isy.konfiguration.properties")
    public Konfiguration konfiguration(IsyKonfigurationProperties properties) {
        if (properties.getNamenschema() != null) {
            return new ReloadablePropertyKonfiguration(properties.getProperties().toArray(new String[0]), properties.getNamenschema());
        } else {
            return new ReloadablePropertyKonfiguration(properties.getProperties().toArray(new String[0]));
        }
    }
}
