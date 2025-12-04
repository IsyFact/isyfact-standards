package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

class IsyActuatorSecurityAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner(AnnotationConfigApplicationContext::new)
        .withConfiguration(AutoConfigurations.of(
            IsyLoadbalancerAutoConfiguration.class,
            IsyActuatorSecurityAutoConfiguration.class
        ));

    @Test
    void disabledWithDisabledSecurity() {
        // bean not available
        contextRunner
            .withClassLoader(new FilteredClassLoader(SecurityFilterChain.class, HttpSecurity.class))
            .run(context -> assertThat(context)
                .hasNotFailed()
                .doesNotHaveBean(IsyActuatorSecurityAutoConfiguration.class)
                .doesNotHaveBean(AuthenticationConfiguration.class)
                .doesNotHaveBean(WebSecurityConfiguration.class)
            );
    }
}