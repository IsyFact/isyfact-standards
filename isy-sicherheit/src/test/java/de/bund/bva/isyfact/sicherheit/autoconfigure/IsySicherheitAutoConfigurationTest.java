package de.bund.bva.isyfact.sicherheit.autoconfigure;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.isyfact.sicherheit.SicherheitAdmin;
import de.bund.bva.isyfact.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.isyfact.sicherheit.config.IsySicherheitConfigurationProperties;
import de.bund.bva.isyfact.sicherheit.config.SicherheitTestConfig;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class IsySicherheitAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(IsySicherheitAutoConfiguration.class));

    @Test
    public void isySicherheitAutoConfigurationCustomAufrufKontextFactory() {
        contextRunner.withPropertyValues("isy.logging.autoconfiguration.enabled=false").withConfiguration(
            UserConfigurations.of(CustomAufrufKontextFactoryConfig.class, SicherheitTestConfig.class))
            .run(context -> {
                assertThat(context).hasBean("customAufrufKontextFactory");
                assertThat(context).doesNotHaveBean("aufrufKontextFactory");
            });
    }

    @Test
    public void isySicherheitAutoConfigurationDisabled() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(GesichertInterceptor.class);
            assertThat(context).doesNotHaveBean(Advisor.class);
            assertThat(context).doesNotHaveBean(IsySicherheitConfigurationProperties.class);
            assertThat(context).doesNotHaveBean(AufrufKontextFactory.class);
            assertThat(context).doesNotHaveBean(CustomScopeConfigurer.class);
            assertThat(context).doesNotHaveBean(SicherheitAdmin.class);
        });
    }

    @Test
    public void isySicherheitAutoConfigurationEnabled() {
        contextRunner.withPropertyValues("isy.logging.autoconfiguration.enabled=false")
            .withConfiguration(UserConfigurations.of(SicherheitTestConfig.class)).run(context -> {
                assertThat(context).hasSingleBean(GesichertInterceptor.class);
                assertThat(context).hasSingleBean(Advisor.class);
                assertThat(context).hasSingleBean(IsySicherheitConfigurationProperties.class);
                assertThat(context).hasSingleBean(AufrufKontextFactory.class);
                assertThat(context).hasSingleBean(CustomScopeConfigurer.class);
                assertThat(context).hasSingleBean(SicherheitAdmin.class);
            });
    }

    @Configuration
    public static class CustomAufrufKontextFactoryConfig {

        @Bean
        public AufrufKontextFactory customAufrufKontextFactory() {
            return new AufrufKontextFactoryImpl();
        }
    }
}
