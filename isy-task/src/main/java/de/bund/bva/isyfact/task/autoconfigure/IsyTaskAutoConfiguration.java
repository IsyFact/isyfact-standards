package de.bund.bva.isyfact.task.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.konfiguration.impl.LocalHostHandlerImpl;
import de.bund.bva.isyfact.task.monitoring.IsyTaskAspect;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;
import de.bund.bva.isyfact.task.sicherheit.impl.IsySicherheitAuthenticatorFactory;
import de.bund.bva.isyfact.task.sicherheit.impl.NoOpAuthenticatorFactory;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.aop.TimedAspect;

@Configuration
@EnableConfigurationProperties
@EnableScheduling
public class IsyTaskAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.task")
    public IsyTaskConfigurationProperties isyTaskConfigurationProperties() {
        return new IsyTaskConfigurationProperties();
    }

    @Bean
    @ConditionalOnMissingBean(HostHandler.class)
    public HostHandler localHostHandler() {
        return new LocalHostHandlerImpl();
    }

    @Bean
    public IsyTaskAspect isyTaskAspect(
            MeterRegistry registry,
            HostHandler hostHandler,
            IsyTaskConfigurationProperties isyTaskConfigurationProperties,
            AuthenticatorFactory authenticatorFactory
    ) {
        return new IsyTaskAspect(registry, hostHandler, isyTaskConfigurationProperties, authenticatorFactory);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    @ConditionalOnProperty(value = "isy.task.authentication.enabled", havingValue = "false", matchIfMissing = true)
    @ConditionalOnMissingBean(AuthenticatorFactory.class)
    public AuthenticatorFactory authenticatorFactoryNoOp() {
        return new NoOpAuthenticatorFactory();
    }

    @Bean
    @ConditionalOnProperty(value = "isy.task.authentication.enabled")
    @ConditionalOnMissingBean(AuthenticatorFactory.class)
    public AuthenticatorFactory authenticatorFactoryIsySicherheit(
            IsyTaskConfigurationProperties configurationProperties, Sicherheit sicherheit,
            AufrufKontextVerwalter aufrufKontextVerwalter, AufrufKontextFactory aufrufKontextFactory) {
        return new IsySicherheitAuthenticatorFactory(configurationProperties, sicherheit,
                aufrufKontextFactory, aufrufKontextVerwalter);
    }
}
