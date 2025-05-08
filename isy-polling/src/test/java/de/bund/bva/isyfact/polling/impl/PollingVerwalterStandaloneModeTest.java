package de.bund.bva.isyfact.polling.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;

import de.bund.bva.isyfact.polling.PollingMBean;
import de.bund.bva.isyfact.polling.autoconfigure.IsyPollingAutoConfiguration;
import de.bund.bva.isyfact.polling.config.IsyPollingProperties;
import de.bund.bva.isyfact.polling.test.AbstractPollingStandaloneTest;
import de.bund.bva.isyfact.polling.test.TestConfig;

/**
 * Tests für den Polling Verwalter.
 *
 * Damit die Tests funktionieren, muss JMX über die folgenden Startparameter der VM
 * aktiviert werden:
 *
 * -Dcom.sun.management.jmxremote
 * -Dcom.sun.management.jmxremote.port=9010
 * -Dcom.sun.management.jmxremote.local.only=false
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sun.management.jmxremote.authenticate=false
 */

@SpringBootTest(classes = {
    TestConfig.class, PollingVerwalterStandaloneModeTest.TestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=IsyLogging", "isy.logging.anwendung.typ=Test",
    "isy.logging.anwendung.version=0.0.0", "isy.logging.component.loggedauer=true",
    "isy.logging.component.loggeaufruf=true",

    "isy.polling.cluster.CLUSTER1.name=Name-Cluster1",
    "isy.polling.cluster.CLUSTER1.wartezeit=12",
    "isy.polling.cluster.CLUSTER1.jmxverbindungen=SERVER1, SERVER2",

    "isy.polling.cluster.CLUSTER2.name=Name-Cluster2",
    "isy.polling.cluster.CLUSTER2.wartezeit=12",
    "isy.polling.cluster.CLUSTER2.jmxverbindungen=SERVER3,SERVER4",

    "isy.polling.cluster.CLUSTER3.name=Name-Cluster3",
    "isy.polling.cluster.CLUSTER3.wartezeit=12",
    "isy.polling.cluster.CLUSTER3.jmxverbindungen=SERVER5"
})
@ImportAutoConfiguration(IsyPollingAutoConfiguration.class)
public class PollingVerwalterStandaloneModeTest extends AbstractPollingStandaloneTest {

    @Configuration
    static class TestConfig {
        @Bean
        public MBeanExporter mBeanExporter(@Qualifier("cluster1Monitor") PollingMBean cluster1Monitor,
            @Qualifier("cluster2Monitor") PollingMBean cluster2Monitor, IsyPollingProperties isyPollingProperties) {
            MBeanExporter mBeanExporter = new MBeanExporter();
            mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
            mBeanExporter.setAssembler(new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource()));
            mBeanExporter.setAutodetect(false);

            Map<String, Object> mBeans = new HashMap<>();
            String key = "de.bund.bva.isyfact.polling:type=PollingStatus,name=\"Polling-Aktivitaet-"
                + isyPollingProperties.getCluster().get("CLUSTER1").getName() + "\"";

            mBeans.put(key, cluster1Monitor);

            key = "de.bund.bva.isyfact.polling:type=PollingStatus,name=\"Polling-Aktivitaet-"
                + isyPollingProperties.getCluster().get("CLUSTER2").getName() + "\"";

            mBeans.put(key, cluster2Monitor);

            mBeanExporter.setBeans(mBeans);

            return mBeanExporter;
        }
    }
}
