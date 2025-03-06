package de.bund.bva.isyfact.polling.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

class IsyPollingPropertiesTest {

    ApplicationContextRunner contextRunner = new ApplicationContextRunner(AnnotationConfigApplicationContext::new)
        .withUserConfiguration(TestConfig.class);

    @Test
    void testPropertiesGesetzt() {
        contextRunner.withPropertyValues(
            "isy.logging.anwendung.name=test",
            "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test",

            "isy.polling.jmx.domain=testdomain",

            "isy.polling.jmx.verbindungen.server1.host=host1",
            "isy.polling.jmx.verbindungen.server1.port=9001",
            "isy.polling.jmx.verbindungen.server1.benutzer=userid1",
            "isy.polling.jmx.verbindungen.server1.passwort=pwd1",

            "isy.polling.jmx.verbindungen.server2.host=host2",
            "isy.polling.jmx.verbindungen.server2.port=9002",
            "isy.polling.jmx.verbindungen.server2.benutzer=userid2",
            "isy.polling.jmx.verbindungen.server2.passwort=pwd2",

            "isy.polling.cluster.POSTFACH1_CLUSTER.name=Postfachabruf-1",
            "isy.polling.cluster.POSTFACH1_CLUSTER.wartezeit=600",
            "isy.polling.cluster.POSTFACH2_CLUSTER.name=Postfachabruf-2",
            "isy.polling.cluster.POSTFACH2_CLUSTER.wartezeit=700",
            "isy.polling.cluster.POSTFACH2_CLUSTER.jmxverbindungen=server1,server2"
        ).run(context -> {
            IsyPollingProperties isyPollingProperties = context.getBean(IsyPollingProperties.class);

            assertThat(isyPollingProperties.getJmx().getDomain()).isEqualTo("testdomain");
            assertThat(isyPollingProperties.getJmx().getVerbindungen()).hasSize(2);
            assertThat(isyPollingProperties.getJmx().getVerbindungen().get("server2")).isNotNull();
            assertThat(isyPollingProperties.getJmx().getVerbindungen().get("server2").getHost()).isEqualTo("host2");

            assertThat(isyPollingProperties.getCluster().get("POSTFACH1_CLUSTER").getName()).isEqualTo("Postfachabruf-1");

            assertThat(isyPollingProperties.getCluster().get("POSTFACH2_CLUSTER").getJmxverbindungen()).hasSize(2);
            assertThat(isyPollingProperties.getCluster().get("POSTFACH2_CLUSTER").getJmxverbindungen().get(0))
                .isEqualTo("server1");
            assertThat(isyPollingProperties.getCluster().get("POSTFACH2_CLUSTER").getJmxverbindungen().get(1))
                .isEqualTo("server2");
        });
    }

    @Test
    void testPropertiesNichtGesetzt() {
        contextRunner.withPropertyValues(
            "isy.polling.jmx.verbindungen.server1.port=-1"
        ).run(context ->
            assertThat(context).hasFailed()
                .getFailure()
                .cause()
                    .hasRootCauseInstanceOf(BindValidationException.class)
        );
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
