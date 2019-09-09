package de.bund.bva.isyfact.logging.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertNotNull;

public class ApplicationListenerInitTest {

    private Map<String, Object> properties = new HashMap<>();

    @Test
    public void testPropertiesGesetzt() {
        properties.put("isy.logging.anwendung.name", "test");
        properties.put("isy.logging.anwendung.typ", "test");
        properties.put("isy.logging.anwendung.version", "1.0-TEST");

        ConfigurableApplicationContext context = new SpringApplicationBuilder()
            .sources(TestConfig.class)
            .properties(properties)
            .run();
        assertNotNull(context.getBean(LogApplicationListener.class));
    }

    @Test(expected = ConfigurationPropertiesBindException.class)
    public void testPropertiesNichtGesetzt() {
        new SpringApplicationBuilder()
            .sources(TestConfig.class)
            .run();
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
