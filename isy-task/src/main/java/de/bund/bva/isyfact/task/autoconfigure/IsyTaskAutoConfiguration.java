package de.bund.bva.isyfact.task.autoconfigure;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.task.TaskScheduler;
import de.bund.bva.isyfact.task.TaskSchedulerStarter;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.impl.TaskSchedulerImpl;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfigurationVerwalter;
import de.bund.bva.isyfact.task.konfiguration.impl.LocalHostHandlerImpl;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;
import de.bund.bva.isyfact.task.sicherheit.impl.IsySicherheitAuthenticatorFactory;
import de.bund.bva.isyfact.task.sicherheit.impl.NoOpAuthenticatorFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class IsyTaskAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.task")
    public IsyTaskConfigurationProperties isyTaskConfigurationProperties() {
        return new IsyTaskConfigurationProperties();
    }

    @Bean
    public TaskScheduler isyTaskScheduler(IsyTaskConfigurationProperties configurationProperties,
        TaskKonfigurationVerwalter taskKonfigurationVerwalter, HostHandler hostHandler) {
        return new TaskSchedulerImpl(configurationProperties, taskKonfigurationVerwalter, hostHandler);
    }

    @Bean
    @ConditionalOnProperty(value = "isy.task.autostart", havingValue = "true")
    public TaskSchedulerStarter taskSchedulerStarter(TaskScheduler taskScheduler) {
        return new TaskSchedulerStarter(taskScheduler);
    }

    @Bean
    public TaskKonfigurationVerwalter taskKonfigurationVerwalter(
        IsyTaskConfigurationProperties configurationProperties, AuthenticatorFactory authenticatorFactory) {
        return new TaskKonfigurationVerwalter(configurationProperties, authenticatorFactory);
    }

    @Bean
    @ConditionalOnMissingBean(HostHandler.class)
    public HostHandler localHostHandler() {
        return new LocalHostHandlerImpl();
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
