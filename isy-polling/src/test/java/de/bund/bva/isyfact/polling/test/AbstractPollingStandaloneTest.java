/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.polling.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.bund.bva.isyfact.datetime.test.TestClock;
import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.polling.PollingVerwalter;

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
