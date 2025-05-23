package de.bund.bva.isyfact.polling.test;

import de.bund.bva.isyfact.polling.annotation.PollingAktion;

/**
 * Testklasse zum Ausführen einer Polling-Aktion.
 * 
 */
public class PollingAktionAusfuehrer {

    /**
     * Führt ein Polling Aktion aus
     */
    @PollingAktion(pollingCluster="CLUSTER1")
    public void doPollingAktionClusterKorrekt() {
        System.out.println("Pollingaktion für CLUSTER 1 ausgeführt.");
    }

    /**
     * Führt ein Polling Aktion aus
     */
    @PollingAktion(pollingCluster="CLUSTER10")
    public void doPollingAktionClusterUnbekannt() {
        System.out.println("Pollingaktion für CLUSTER 10 ausgeführt.");
    }

}
