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
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests for {@link IsyActuatorSecurityAutoConfiguration}.
 * <p>
 * Tests the behavior of the AutoConfiguration, depending on the property (application.properties)
 * and dependency (pom.xml) configuration.
 */
class IsyActuatorSecurityAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner(AnnotationConfigApplicationContext::new)
        .withConfiguration(AutoConfigurations.of(
            IsyUeberwachungAutoConfiguration.class,
            IsyActuatorSecurityAutoConfiguration.class
        ));

    /**
     * No user configured, so no security should be applied.
     * (avoids a breaking change in IF3 applications)
     */
    @Test
    void noUserConfigured() {
        contextRunner.run(context ->
            assertThat(context).hasNotFailed()
                .hasSingleBean(IsyActuatorSecurityAutoConfiguration.class)
                // WebSecurityConfigurerAdapter and PasswordEncoder not created
                .doesNotHaveBean(IsyActuatorSecurityAutoConfiguration.HttpBasicWebEndpointSecurityConfiguration.class)
                .doesNotHaveBean(PasswordEncoder.class)
        );
    }

    /**
     * User configured, so security should be applied.
     */
    @Test
    void userWithoutPasswordConfigured() {
        contextRunner
            .withPropertyValues(
                "isy.ueberwachung.security.username=test",
                // empty/null password should work
                "isy.ueberwachung.security.password="
            ).run(context ->
                assertThat(context).hasNotFailed()
                    .hasSingleBean(IsyActuatorSecurityAutoConfiguration.class)
                    .hasSingleBean(IsyActuatorSecurityAutoConfiguration.HttpBasicWebEndpointSecurityConfiguration.class)
                    .hasSingleBean(PasswordEncoder.class)
            );
    }

    /**
     * Password without user configured. Username must not be empty.
     */
    @Test
    void passwordWithoutUserConfigured() {
        contextRunner
            .withPropertyValues(
                // username must not be empty => BindValidationException.class
                "isy.ueberwachung.security.username=    ",
                "isy.ueberwachung.security.password=test"
            ).run(context ->
                assertThat(context).hasFailed().getFailure()
                    .getRootCause()
                    .isInstanceOf(IllegalArgumentException.class)
                    .message()
                    .isEqualTo("Cannot pass null or empty values to constructor")
            );
    }

    /**
     * Assuming Spring Security is not on the classpath, so security must not be applied.
     */
    @Test
    void disabledWithDisabledSecurity() {
        // bean not available
        contextRunner
            .withClassLoader(new FilteredClassLoader(HttpSecurity.class))
            .run(context -> assertThat(context)
                .hasNotFailed()
                .doesNotHaveBean(IsyActuatorSecurityAutoConfiguration.class)
                .doesNotHaveBean(AuthenticationConfiguration.class)
                .doesNotHaveBean(WebSecurityConfiguration.class)
            );
    }
}