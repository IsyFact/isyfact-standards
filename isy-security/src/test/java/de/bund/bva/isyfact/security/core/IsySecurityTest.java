package de.bund.bva.isyfact.security.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import de.bund.bva.isyfact.security.Security;
import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;

class IsySecurityTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void createSecurityBean() {
        contextRunner.withConfiguration(AutoConfigurations.of(IsySecurityAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(Security.class)
                );
    }
}