package de.bund.bva.isyfact.polling.test;

import java.time.Duration;

import de.bund.bva.isyfact.datetime.test.TestClock;
import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.polling.PollingVerwalter;
import de.bund.bva.isyfact.polling.config.IsyPollingProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests für den Polling Verwalter im Standalone-Modus.
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
public abstract class AbstractPollingStandaloneTest extends AbstractPollingTest {

    @Autowired
    protected PollingVerwalter pollingVerwalter;
    
    @Autowired
    protected PollingAktionAusfuehrer pollingAktionAusfuehrer;

    @Test
    public void startePollingTest() throws Exception {

        assertTrue("JMX ist nicht gestartet.", pruefeJMXStatus());

        TestClock testClock = TestClock.now();
        DateTimeUtil.setClock(testClock);
        
        // Cluster 1 aktualisieren. Da "Modus Standalone" gesetzt ist, darf das Polling gestartet werden darf.
        pollingVerwalter.aktualisiereZeitpunktLetztePollingAktivitaet("CLUSTER1"); 
        assertTrue("Polling darf nicht gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));

        // Cluster 2 aktualisieren. Da "Modus Standalone" gesetzt ist, darf das Polling gestartet werden darf.
        assertTrue("Polling darf nicht gestartet werden", pollingVerwalter.startePolling("CLUSTER2"));

        // Einen Teil der Wartezeit verstreichen lassen
        testClock.advanceBy(Duration.ofSeconds(5));

        // Da "Modus Standalone" gesetzt ist, darf für Cluster1 das Polling gestartet werden. 
        assertTrue("Polling darf gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));
        
        // Rest der Wartezeit verstreichen lassen
        testClock.advanceBy(Duration.ofSeconds(8));
        
        // Cluster 1 erneut überprüfen
        assertTrue("Polling darf gestartet werden", pollingVerwalter.startePolling("CLUSTER1"));

        // Für Cluster 3 ist keine MBean definiert und ein nicht existenter Port. Daher kann die MBean nicht erreicht werden
        // und das Polling darf ausgeführt werden.
        pollingVerwalter.aktualisiereZeitpunktLetztePollingAktivitaet("CLUSTER3"); 
        assertTrue("Polling darf gestartet werden", pollingVerwalter.startePolling("CLUSTER3"));        
    }

    @Test
    public void annotationTest() {
        
        // Zeitpunkt der letzten Ausführung merken
        long ausfuehrungszeitpunkt1 = pollingVerwalter.getZeitpunktLetztePollingAktivitaet("CLUSTER1");
        assertEquals("Zeitpunkt der letzen Ausführung", 0, ausfuehrungszeitpunkt1);
        // Polling-Aktion ausführen
        pollingAktionAusfuehrer.doPollingAktionClusterKorrekt();
        // Zeitpunkt der letzten Ausführung lesen
        long ausfuehrungszeitpunkt2 = pollingVerwalter.getZeitpunktLetztePollingAktivitaet("CLUSTER1");
        assertEquals("Zeitpunkt der letzen Ausführung", 0, ausfuehrungszeitpunkt2);

        // Aktion für unbekannten Cluster ausführen
        pollingAktionAusfuehrer.doPollingAktionClusterUnbekannt();
    }   
}
