package de.bund.bva.isyfact.logging.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

class ApplicationListenerInitTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner(AnnotationConfigApplicationContext::new)
        .withUserConfiguration(ApplicationListenerInitTest.TestConfig.class);

    @Test
    void testPropertiesGesetzt() {
        contextRunner.withPropertyValues(
            "isy.logging.anwendung.name=test",
            "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=1.0-TEST"
        ).run(context -> assertThat(context)
            .hasSingleBean(LogApplicationListener.class));

    }

    @Test
    void testPropertiesNichtGesetzt() {
        contextRunner.run(context -> assertThat(context)
            .hasFailed()
            .getFailure()
            .isInstanceOf(ConfigurationPropertiesBindException.class)
        );
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig { }
}
