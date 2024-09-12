package de.bund.bva.isyfact.persistence.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;

class IsyPersistenceAutoConfigurationTest {

    @Test
    void testDataSourceUrlPropertyNichtGesetzt() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("isy.logging.anwendung.name", "test");
        properties.put("isy.logging.anwendung.typ", "test");
        properties.put("isy.logging.anwendung.version", "test");

        ConfigurableApplicationContext context = new SpringApplicationBuilder()
            .sources(TestConfig.class)
            .properties(properties)
            .run();

        assertThatThrownBy(() -> context.getBean(IsyDataSource.class))
            .isInstanceOf(NoSuchBeanDefinitionException.class)
            .hasMessageContaining(
                "No qualifying bean of type 'de.bund.bva.isyfact.persistence.datasource.IsyDataSource' available"
            );
    }

    @Test
    void testPropertiesFehlerhaft() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("isy.persistence.datasource.url", "test");
        properties.put("spring.sql.init.enabled", "false");

        assertThatThrownBy(() ->
            new SpringApplicationBuilder()
                .sources(TestConfig.class)
                .properties(properties)
                .run())
            .isInstanceOf(UnsatisfiedDependencyException.class)
            .getRootCause()
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("URL must start with 'jdbc'");
    }

    @Test
    void testExpectCyclicReferenceException() {
        assertThatThrownBy(() ->
            new SpringApplicationBuilder()
                .sources(TestConfig.class)
                .profiles("h2")
                .run())
            .isInstanceOf(UnsatisfiedDependencyException.class)
            .hasMessageContaining("Is there an unresolvable circular reference?");
    }

    @Test
    void testValidAutoConfiguration() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("isy.logging.anwendung.name", "test");
        properties.put("isy.logging.anwendung.typ", "test");
        properties.put("isy.logging.anwendung.version", "test");
        properties.put("spring.sql.init.mode", "never");

        AssertableApplicationContext assertableContext = AssertableApplicationContext.get(
            () -> new SpringApplicationBuilder()
                .sources(
                    TestConfig.class,
                    DataSourceInitializerTest.TestDataSourceInitializationConfig.class
                )
                .profiles("h2")
                .properties(properties)
                .run()
        );

        assertThat(assertableContext)
            .hasNotFailed()
            .hasSingleBean(IsyDataSource.class)
            .hasBean("dataSource")
            .hasBean("appDataSource");
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }

}
