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
package de.bund.bva.isyfact.polling;

/**
 * Interface der Komponente Polling Verwalter. Der Polling-Verwalter verwaltet mehrere Polling-Cluster 
 * und unterstützt die Synchronisierung von Polling-Aktivitäten innerhalb eines Polling-Clusters.
 * <br>
 * Ein Polling-Cluster besteht aus Menge von Servern, die jeweils die gleiche Nachrichtenquelle abfragen 
 * und wird durch eine innerhalb der Anwendung eindeutige Id identifiziert.
 * 
 */
public interface PollingVerwalter {

    /**
     * Ermittelt, ob die aktuelle Instanz der Anwendung das Polling im angegebenen Cluster durchführen kann.
     * Ist das der Fall, wird der letzte Ausführungszeitpunkt aktualisiert und true zurückgegeben. 
     * 
     * @param clusterId
     *          ID, für den der Polling-Status ermittelt wird.
     * @return true, falls diese Instanz der Anwendung das Polling starten kann, 
     *         false sonst.
     */
    boolean startePolling(String clusterId);
    
    /**
     * Setzt den Zeitpunkt der letzten Polling-Aktivität für den angegebenen Polling-Cluster.
     * 
     *  @param clusterId
     *          ID des Polling-Clusters.
     */
    void aktualisiereZeitpunktLetztePollingAktivitaet(String clusterId);

    /**
     * Liefert den Zeitpunkt der letzten Polling-Aktivität für den angegebenen Polling-Cluster.
     * 
     * @param clusterId
     *          ID des Polling-Clusters.
     * @return Zeitpunkt der letzten Polling-Aktivität       
     * 
     */
    long getZeitpunktLetztePollingAktivitaet(String clusterId);
}



