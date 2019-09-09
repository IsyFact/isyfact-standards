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

import de.bund.bva.isyfact.polling.annotation.PollingAktion;

/**
 * Testklasse zum Ausführen einer Polling-Aktion.
 * 
 */
public class PollingAktionAusfuehrer {

    /**
     * Führt ein Polling Aktion aus
     */
    @PollingAktion(pollingCluster = "CLUSTER1")
    public void doPollingAktionClusterKorrekt() {
        System.out.println("Pollingaktion für CLUSTER 1 ausgeführt.");
    }

    /**
     * Führt ein Polling Aktion aus
     */
    @PollingAktion(pollingCluster = "CLUSTER10")
    public void doPollingAktionClusterUnbekannt() {
        System.out.println("Pollingaktion für CLUSTER 10 ausgeführt.");
    }

}
