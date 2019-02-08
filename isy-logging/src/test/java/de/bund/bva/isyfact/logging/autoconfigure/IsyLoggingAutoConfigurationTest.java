package de.bund.bva.isyfact.logging.autoconfigure;

import de.bund.bva.isyfact.logging.util.LogApplicationListener;
import de.bund.bva.isyfact.logging.util.LoggingMethodInterceptor;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class IsyLoggingAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(
        AutoConfigurations
            .of(IsyLoggingAutoConfiguration.class, IsyPerformanceLoggingAutoConfiguration.class));

    @Test
    public void isyLoggingAutoConfigurationEnabled() {
        contextRunner.withPropertyValues("isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test").run(context -> {
            assertThat(context).hasSingleBean(LogApplicationListener.class);
            assertThat(context).hasBean("componentLogInterceptor");
            assertThat(context).hasBean("boundaryLogInterceptor");
        });
    }

    @Test
    public void isyLoggingAutoConfigurationDisabled() {
        contextRunner.withPropertyValues("isy.logging.autoconfiguration.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(LogApplicationListener.class);
            assertThat(context).doesNotHaveBean(LoggingMethodInterceptor.class);
        });
    }

    @Test
    public void isyPerformanceLoggingDisabled() {
        contextRunner.withPropertyValues("isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test")
            .run(context -> assertThat(context).doesNotHaveBean("performanceLogInterceptor"));
    }

    @Test
    @Ignore("ClassLoader für Test nicht mit @EnableLoadTimeWeaving kompatibel") // TODO Lösung?
    public void isyPerformanceLoggingEnabled() {
        contextRunner.withPropertyValues("isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test", "isy.logging.performancelogging.enabled=true")
            .run(context -> assertThat(context).hasBean("performanceLogInterceptor"));
    }
}