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

import java.util.Date;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.polling.common.exception.PollingClusterKonfigurationException;
import de.bund.bva.isyfact.polling.common.exception.PollingClusterUnbekanntException;
import de.bund.bva.isyfact.polling.common.konstanten.EreignisSchluessel;

/**
 * MBean die Bereitstellung der vom Polling-Verwalter benötigten Informationen.
 *
 */
@ManagedResource(description = "Diese MBean dient zum Abruf der zur Synchronisation "
    + "von Polling-Aktivitäten benötigten Informationen.")
public class PollingMBean {
    /** Der Logger dieser Klasse. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(PollingMBean.class);

    /** Der Polling-Verwalter. Wird über Spring gesetzt. */
    private PollingVerwalter pollingVerwalter;

    /** ID des Polling-Clusters, für den die Werte abgerufen werden können. Wird über Spring gesetzt. */
    private volatile String clusterId;

    /**
     * Setzt das Attribut 'pollingVerwalter'.
     *
     * @param pollingVerwalter
     *            der Attributwert.
     */
    public void setPollingVerwalter(PollingVerwalter pollingVerwalter) {
        this.pollingVerwalter = pollingVerwalter;
    }

    /**
     * Setzt das Feld 'clusterId'.
     * @param clusterId
     *            Neuer Wert für clusterId
     */
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * Liefert den Zeitpunkt der letzten Polling-Aktivität für einen Polling-Cluster.
     *
     * @return Zeitpunkt der letzten Polling-Aktivität.
     */
    @ManagedAttribute(
        description = "Liefert den Zeitpunkt der letzten Polling-Aktivität für einen Polling-Cluster.")
    public Date getZeitpunktLetztePollingAktivitaet() {
        try {
            return new Date(this.pollingVerwalter.getZeitpunktLetztePollingAktivitaet(this.clusterId));
        } catch (PollingClusterKonfigurationException e) {
            LOG.error("PollingClusterKonfigurationException", e);
            return new Date(0);
        } catch (PollingClusterUnbekanntException e) {
            LOG.error("PollingClusterUnbekanntException", e);
            return new Date(0);
        }
    }

    /**
     * Liefert die vergangene Zeit seit der letzten Polling-Aktivität für einen Polling-Cluster in
     * Millisekunden.
     *
     * @return Zeitpunkt der letzten Polling-Aktivität.
     */
    @ManagedAttribute(
        description = "Liefert die vergangene Zeit seit der letzten Polling-Aktivität für einen Polling-Cluster in Millisekunden.")
    public long getZeitraumLetztePollingAktivitaet() {
        try {
            long zeitpunktLetztePollingAktivitaet =
                this.pollingVerwalter.getZeitpunktLetztePollingAktivitaet(this.clusterId);
            if (zeitpunktLetztePollingAktivitaet == 0) {
                // 0 bedeutet, dass dieser Knoten noch nie gepollt hat
                return Long.MAX_VALUE;
            } else {
                return DateTimeUtil.getClock().instant().toEpochMilli() - zeitpunktLetztePollingAktivitaet;
            }
        } catch (PollingClusterKonfigurationException e) {
            LOG.error(EreignisSchluessel.POLLING_CLUSTER_KONFIG_FEHLER,
                "PollingClusterKonfigurationException", e);
            return Long.MAX_VALUE;
        } catch (PollingClusterUnbekanntException e) {
            LOG.error(EreignisSchluessel.POLLING_CLUSTER_UNBEKANNT, "PollingClusterUnbekanntException", e);
            return Long.MAX_VALUE;
        }
    }

}
