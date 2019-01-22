package de.bund.bva.isyfact.persistence.autoconfigure;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;

public class IsyPersistenceOracleAutoConfigurationTest {

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testOraclePropertiesNichtGesetzt() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("isy.logging.anwendung.name", "test");
        properties.put("isy.logging.anwendung.typ", "test");
        properties.put("isy.logging.anwendung.version", "test");

        ConfigurableApplicationContext context = new SpringApplicationBuilder()
            .sources(TestConfig.class)
            .properties(properties)
            .run();

        context.getBean(IsyDataSource.class);
    }

    @Test(expected = UnsatisfiedDependencyException.class)
    public void testOraclePropertiesUnvollstaendigGesetzt() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("oracle.datasource.databaseurl", "test");

        new SpringApplicationBuilder()
            .sources(TestConfig.class)
            .properties(properties)
            .run();
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {

    }
}