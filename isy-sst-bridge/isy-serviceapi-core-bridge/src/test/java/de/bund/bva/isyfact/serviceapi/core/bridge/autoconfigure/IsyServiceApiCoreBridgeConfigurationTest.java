package de.bund.bva.isyfact.serviceapi.core.bridge.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import de.bund.bva.isyfact.serviceapi.autoconfigure.IsyServiceApiCoreAutoConfiguration;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.bridge.IF1OrIF2AufrufKontextToResolver;

public class IsyServiceApiCoreBridgeConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            IsyServiceApiCoreAutoConfiguration.class,
            IsyServiceApiCoreBridgeConfiguration.class));

    @Test
    public void autoConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(DefaultAufrufKontextToResolver.class);
            assertThat(context).hasSingleBean(IF1OrIF2AufrufKontextToResolver.class);
        });
    }
}