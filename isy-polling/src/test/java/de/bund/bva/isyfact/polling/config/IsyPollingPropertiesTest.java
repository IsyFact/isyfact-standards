package de.bund.bva.isyfact.polling.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IsyPollingPropertiesTest {

    @Test
    public void testPropertiesGesetzt() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("isy.logging.anwendung.name", "test");
        properties.put("isy.logging.anwendung.typ", "test");
        properties.put("isy.logging.anwendung.version", "test");

        properties.put("isy.polling.jmx.domain", "testdomain");

        properties.put("isy.polling.jmx.verbindungen.server1.host", "host1");
        properties.put("isy.polling.jmx.verbindungen.server1.port", "9001");
        properties.put("isy.polling.jmx.verbindungen.server1.benutzer", "userid1");
        properties.put("isy.polling.jmx.verbindungen.server1.passwort", "pwd1");

        properties.put("isy.polling.jmx.verbindungen.server2.host", "host2");
        properties.put("isy.polling.jmx.verbindungen.server2.port", "9002");
        properties.put("isy.polling.jmx.verbindungen.server2.benutzer", "userid2");
        properties.put("isy.polling.jmx.verbindungen.server2.passwort", "pwd2");

        properties.put("isy.polling.cluster.POSTFACH1_CLUSTER.name", "Postfachabruf-1");
        properties.put("isy.polling.cluster.POSTFACH1_CLUSTER.wartezeit", "600");
        properties.put("isy.polling.cluster.POSTFACH2_CLUSTER.name", "Postfachabruf-2");
        properties.put("isy.polling.cluster.POSTFACH2_CLUSTER.wartezeit", "700");
        properties.put("isy.polling.cluster.POSTFACH2_CLUSTER.jmxverbindungen", "server1,server2");

        ConfigurableApplicationContext context =
            new SpringApplicationBuilder().sources(TestConfig.class).properties(properties).run();

        IsyPollingProperties isyPollingProperties = context.getBean(IsyPollingProperties.class);

        assertEquals("testdomain", isyPollingProperties.getJmx().getDomain());
        assertEquals(2, isyPollingProperties.getJmx().getVerbindungen().size());
        assertNotNull(isyPollingProperties.getJmx().getVerbindungen().get("server2"));
        assertEquals("host2", isyPollingProperties.getJmx().getVerbindungen().get("server2").getHost());

        assertEquals("Postfachabruf-1", isyPollingProperties.getCluster().get("POSTFACH1_CLUSTER").getName());

        assertEquals(2,
            isyPollingProperties.getCluster().get("POSTFACH2_CLUSTER").getJmxverbindungen().size());
        assertEquals("server1",
            isyPollingProperties.getCluster().get("POSTFACH2_CLUSTER").getJmxverbindungen().get(0));
        assertEquals("server2",
            isyPollingProperties.getCluster().get("POSTFACH2_CLUSTER").getJmxverbindungen().get(1));
    }


    @Test(expected = BeanCreationException.class)
    public void testPropertiesNichtGesetzt() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("isy.polling.jmx.verbindungen.server1.port", "-1");

        new SpringApplicationBuilder().sources(TestConfig.class).properties(properties).run();
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
