package de.bund.bva.isyfact.ueberwachung.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.web.SecurityFilterChain;

import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyLoadbalancerAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyLoadbalancerSecurityAutoConfiguration;

public class LoadbalancerSecurityBeanConfigurationTest {

    /**
     * Enable autoconfiguration conditions evaluation report.
     */
    private final ConditionEvaluationReportLoggingListener initializer = new ConditionEvaluationReportLoggingListener();

    /**
     * Sets the contextRunner.
     */
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner(
            AnnotationConfigApplicationContext::new).withConfiguration(AutoConfigurations.of(
                    IsyLoadbalancerAutoConfiguration.class, IsyLoadbalancerSecurityAutoConfiguration.class
            ))
            .withInitializer(initializer);

    @Test
    public void enabledWithEnabledSecurity() {
        // bean available
        contextRunner
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(LoadbalancerSecurityConfiguration.class)
                        .hasSingleBean(LoadbalancerServletConfigurationProperties.class)
                        .hasSingleBean(AuthenticationConfiguration.class)
                        .hasSingleBean(WebSecurityConfiguration.class)
                );
    }

    @Test
    public void disabledWithDisabledSecurity() {
        // bean not available
        contextRunner
                .withClassLoader(new FilteredClassLoader(SecurityFilterChain.class, HttpSecurity.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(LoadbalancerSecurityConfiguration.class)
                        .doesNotHaveBean(AuthenticationConfiguration.class)
                        .doesNotHaveBean(WebSecurityConfiguration.class)
                        .hasSingleBean(LoadbalancerServletConfigurationProperties.class)
                );
    }
}