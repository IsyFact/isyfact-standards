package de.bund.bva.isyfact.konfiguration.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.konfiguration.common.Konfiguration;
import de.bund.bva.isyfact.konfiguration.common.impl.ReloadablePropertyKonfiguration;
import de.bund.bva.isyfact.konfiguration.config.IsyKonfigurationProperties;

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
        return new ReloadablePropertyKonfiguration(properties.getProperties().toArray(new String[0]));
    }
}
