package de.bund.bva.isyfact.task.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.konfiguration.impl.LocalHostHandlerImpl;
import de.bund.bva.isyfact.task.monitoring.IsyTaskAspect;
import de.bund.bva.isyfact.task.security.AuthenticatorFactory;
import de.bund.bva.isyfact.task.security.impl.IsySecurityAuthenticatorFactory;
import de.bund.bva.isyfact.task.security.impl.NoOpAuthenticatorFactory;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

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
            AuthenticatorFactory authenticatorFactory,
            MessageSource isyTaskMessageSource
    ) {
        return new IsyTaskAspect(registry, hostHandler, isyTaskConfigurationProperties, authenticatorFactory, isyTaskMessageSource);
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
    public AuthenticatorFactory authenticatorFactoryIsySecurity(
            IsyTaskConfigurationProperties configurationProperties,
            Authentifizierungsmanager authentifizierungsmanager,
            MessageSource isyTaskMessageSource
    ) {
        return new IsySecurityAuthenticatorFactory(configurationProperties, authentifizierungsmanager, isyTaskMessageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageSource isyTaskMessageSource () {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("resources/isy-task/nachrichten/ereignisse", "resources/isy-task/nachrichten/hinweise");
        return messageSource;
    }
}
