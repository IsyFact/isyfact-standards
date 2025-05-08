package de.bund.bva.isyfact.polling.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.datetime.test.TestClock;
import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.polling.PollingMBean;
import de.bund.bva.isyfact.polling.PollingVerwalter;
import de.bund.bva.isyfact.polling.autoconfigure.IsyPollingAutoConfiguration;
import de.bund.bva.isyfact.polling.config.IsyPollingProperties;
import de.bund.bva.isyfact.polling.test.AbstractPollingTest;
import de.bund.bva.isyfact.polling.test.TestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;

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
    TestConfig.class, PollingVerwalterDefaultJmxConnTest.TestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name = test",
    "isy.logging.anwendung.typ = test",
    "isy.logging.anwendung.version = test",

    "isy.polling.jmx.verbindungen.SERVER1.host = localhost",
    "isy.polling.jmx.verbindungen.SERVER1.port = 9010",
    "isy.polling.jmx.verbindungen.SERVER1.benutzer = server1",
    "isy.polling.jmx.verbindungen.SERVER1.passwort = server1",

    "isy.polling.jmx.verbindungen.SERVER2.host = localhost",
    "isy.polling.jmx.verbindungen.SERVER2.port = 9010",
    "isy.polling.jmx.verbindungen.SERVER2.benutzer = SERVER2",
    "isy.polling.jmx.verbindungen.SERVER2.passwort = SERVER2",

    "isy.polling.cluster.CLUSTER1.name = Name-Cluster1", "isy.polling.cluster.CLUSTER1.wartezeit = 12" })
@ImportAutoConfiguration(IsyPollingAutoConfiguration.class)
public class PollingVerwalterDefaultJmxConnTest extends AbstractPollingTest {

    @Autowired
    private PollingVerwalter pollingVerwalter;

    /**
     * Testet die Methode "startePolling".
     */
    @Test
    public void startePollingTest() throws Exception {

        Assert.assertTrue("JMX ist nicht gestartet.", pruefeJMXStatus());

        TestClock testClock = TestClock.now();
        DateTimeUtil.setClock(testClock);

        // Cluster 1 aktualisieren. Da der Test lokal ist, führt das dazu, 
        // dass das Polling nicht gestartet werden darf.
        pollingVerwalter.aktualisiereZeitpunktLetztePollingAktivitaet("CLUSTER1");
        Assert.assertFalse("Polling darf nicht gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));

        // Einen Teil der Wartezeit verstreichen lassen
        testClock.advanceBy(Duration.ofSeconds(5));

        // Für Cluster1 darf das Polling immer noch nicht gestartet werden. 
        Assert.assertFalse("Polling darf nicht gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));

        // Rest der Wartezeit verstreichen lassen
        testClock.advanceBy(Duration.ofSeconds(8));

        // Cluster 1 erneut überprüfen
        Assert.assertTrue("Polling darf gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));

    }

    @Configuration
    static class TestConfig {
        @Bean
        public MBeanExporter mBeanExporter(@Qualifier("cluster1Monitor") PollingMBean cluster1Monitor,
            IsyPollingProperties isyPollingProperties) {
            MBeanExporter mBeanExporter = new MBeanExporter();
            mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
            mBeanExporter.setAssembler(new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource()));
            mBeanExporter.setAutodetect(false);

            Map<String, Object> mBeans = new HashMap<>();
            String key = "de.bund.bva.isyfact.polling:type=PollingStatus,name=\"Polling-Aktivitaet-"
                + isyPollingProperties.getCluster().get("CLUSTER1").getName() + "\"";

            mBeans.put(key, cluster1Monitor);

            mBeanExporter.setBeans(mBeans);

            return mBeanExporter;
        }
    }
}
