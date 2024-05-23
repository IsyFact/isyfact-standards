package de.bund.bva.isyfact.persistence.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;

class IsyPersistenceAutoConfigurationTest {

    ApplicationContextRunner contextRunner = new ApplicationContextRunner(AnnotationConfigApplicationContext::new)
            .withConfiguration(AutoConfigurations.of(IsyPersistenceAutoConfiguration.class));


    @Test
    void testDataSourceUrlPropertyNichtGesetzt() {
        contextRunner.withPropertyValues(
            "isy.logging.anwendung.name=test",
            "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test"
        ).run(context -> {
            assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(
                () ->
                context.getBean(IsyDataSource.class)
            );
        });
    }

    @Test
    void testPropertiesFehlerhaft() {
        contextRunner
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
            "isy.persistence.datasource.url=test",
            "spring.sql.init.enabled=false"
        ).run(context -> {
            assertThat(context).hasFailed()
                .getFailure()
                .cause()
                .isInstanceOf(UnsatisfiedDependencyException.class);
        });
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }

}
