package de.bund.bva.pliscommon.konfiguration.autoconfigure;

import de.bund.bva.pliscommon.konfiguration.autoconfigure.IsyKonfigurationAutoConfiguration;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class IsyKonfigurationAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(
        AutoConfigurations
            .of(IsyKonfigurationAutoConfiguration.class));

    @Test
    public void isyKonfigurationAutoConfigurationDisabled() {
        contextRunner.withPropertyValues("isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test").run(context -> {
            assertThat(context).doesNotHaveBean(Konfiguration.class);
        });
    }

    @Test
    public void isyKonfigurationAutoConfigurationEnabled() {
        contextRunner.withPropertyValues("isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test", "isy.konfiguration.properties=/config/config_A.properties, /config/config_B.properties")
            .run(context -> assertThat(context).hasSingleBean(Konfiguration.class));
    }

}