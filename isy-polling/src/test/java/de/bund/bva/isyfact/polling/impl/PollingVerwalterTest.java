package de.bund.bva.isyfact.polling.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.datetime.test.TestClock;
import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.polling.PollingMBean;
import de.bund.bva.isyfact.polling.autoconfigure.IsyPollingAutoConfiguration;
import de.bund.bva.isyfact.polling.config.IsyPollingProperties;
import de.bund.bva.isyfact.polling.test.TestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.bund.bva.isyfact.polling.PollingVerwalter;
import de.bund.bva.isyfact.polling.common.exception.PollingClusterUnbekanntException;
import de.bund.bva.isyfact.polling.test.AbstractPollingTest;
import de.bund.bva.isyfact.polling.test.PollingAktionAusfuehrer;
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
 *
 */
@SpringBootTest(classes = {
    TestConfig.class, PollingVerwalterTest.TestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {

    "isy.logging.anwendung.name = test",
    "isy.logging.anwendung.typ = test",
    "isy.logging.anwendung.version = test",

    "isy.polling.jmx.verbindungen.SERVER1.host=localhost",
    "isy.polling.jmx.verbindungen.SERVER1.port=9010",
    "isy.polling.jmx.verbindungen.SERVER1.benutzer=server1",
    "isy.polling.jmx.verbindungen.SERVER1.passwort=server1",

    "isy.polling.jmx.verbindungen.SERVER2.host=localhost",
    "isy.polling.jmx.verbindungen.SERVER2.port=9010",
    "isy.polling.jmx.verbindungen.SERVER2.benutzer=SERVER2",
    "isy.polling.jmx.verbindungen.SERVER2.passwort=SERVER2",

    "isy.polling.jmx.verbindungen.SERVER3.host=localhost",
    "isy.polling.jmx.verbindungen.SERVER3.port=9010",
    "isy.polling.jmx.verbindungen.SERVER3.benutzer=SERVER3",
    "isy.polling.jmx.verbindungen.SERVER3.passwort=SERVER3",

    "isy.polling.jmx.verbindungen.SERVER4.host=localhost",
    "isy.polling.jmx.verbindungen.SERVER4.port=9010",
    "isy.polling.jmx.verbindungen.SERVER4.benutzer=SERVER4",
    "isy.polling.jmx.verbindungen.SERVER4.passwort=SERVER4",

    "isy.polling.jmx.verbindungen.SERVER5.host=localhost",
    "isy.polling.jmx.verbindungen.SERVER5.port=9990",
    "isy.polling.jmx.verbindungen.SERVER5.benutzer=SERVER5",
    "isy.polling.jmx.verbindungen.SERVER5.passwort=SERVER5",

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
public class PollingVerwalterTest extends AbstractPollingTest {

    @Autowired
    private PollingVerwalter pollingVerwalter;
    
    @Autowired
    private PollingAktionAusfuehrer pollingAktionAusfuehrer;
    
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

        // Cluster 2 wird nicht aktualisiert. Da der Test lokal ist, führt das dazu, 
        // dass das Polling gestartet werden darf.
        Assert.assertTrue("Polling darf gestartet werden", pollingVerwalter.startePolling("CLUSTER2"));

        // Einen Teil der Wartezeit verstreichen lassen
        testClock.advanceBy(Duration.ofSeconds(5));

        // Für Cluster1 darf das Polling immer noch nicht gestartet werden. 
        Assert.assertFalse("Polling darf nicht gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));
        
        // Rest der Wartezeit verstreichen lassen
        testClock.advanceBy(Duration.ofSeconds(8));
        
        // Cluster 1 erneut überprüfen
        Assert.assertTrue("Polling darf gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));

        // Für Cluster 3 ist keine MBean definiert und ein nicht existenter Port. Daher kann die MBean nicht erreicht werden
        // und das Polling darf ausgeführt werden.
        pollingVerwalter.aktualisiereZeitpunktLetztePollingAktivitaet("CLUSTER3"); 
        Assert.assertTrue("Polling darf gestartet werden", pollingVerwalter.startePolling("CLUSTER3"));        
    }
    
    /**
     * Testet, ob der Interceptor funktioniert.
     */
    @Test
    public void annotationTest() {
        // Zeitpunkt der letzten Ausführung merken
        long ausfuehrungszeitpunkt1 = pollingVerwalter.getZeitpunktLetztePollingAktivitaet("CLUSTER1");
        // Polling-Aktion ausführen
        pollingAktionAusfuehrer.doPollingAktionClusterKorrekt();
        // Zeitpunkt der letzten Ausführung lesen
        long ausfuehrungszeitpunkt2 = pollingVerwalter.getZeitpunktLetztePollingAktivitaet("CLUSTER1");
        Assert.assertTrue("Ausführungszeitpunkt wurde nicht geändert.", ausfuehrungszeitpunkt1 != ausfuehrungszeitpunkt2);
    }

    @Test(expected = PollingClusterUnbekanntException.class)
    public void annotationTestClusterUnbekannt() {
        pollingAktionAusfuehrer.doPollingAktionClusterUnbekannt();
    }

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
