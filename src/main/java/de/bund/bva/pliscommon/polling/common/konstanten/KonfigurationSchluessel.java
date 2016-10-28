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
package de.bund.bva.pliscommon.polling.common.konstanten;

/**
 * Diese Klasse enthält alle Schlüssel, welche zum Auslesen von Konfigurationen verwendet werden. Diese
 * Konfigurationen werden über Implementierungen des Interfaces
 * {@link de.bund.bva.pliscommon.konfiguration.common.Konfiguration} ausgelesen.
 * 
 */
public abstract class KonfigurationSchluessel {    
    /** IDs aller Polling-Cluster. */
    public static final String CONF_POLLING_CLUSTER_LISTE = "polling.cluster.ids";

    // Nachfolgend werden die Präfixe und Suffixe für Polling-Cluster-Konfigurationen definiert
    // Ein konkreter Schlüssel setzt sich aus CONF_POLLING_CLUSTER_PRAEFIX + <Polling-Cluster-Id> + Suffix zusammen

    /** Präfix für den Konfigurationsschlüssel für den Polling-Cluster. */
    public static final String CONF_POLLING_CLUSTER_PRAEFIX = "polling.cluster.";

    /** Suffix für den Konfigurationsschlüssel für den Namen des Polling-Clusters. */
    public static final String CONF_POLLING_CLUSTER_NAME_SUFFIX = ".name";

    /** Suffix für den Konfigurationsschlüssel für die ID des Polling-Clusters. */
    public static final String CONF_POLLING_CLUSTER_WARTEZEIT_SUFFIX = ".wartezeit";

    /** Suffix für den Konfigurationsschlüssel für die ID des Polling-Clusters. */
    public static final String CONF_POLLING_CLUSTER_JMXVERBINDUNGEN_SUFFIX = ".jmxverbindungen";
    
    /** IDs aller JMX-Verbindungen. */
    public static final String CONF_POLLING_JMXVERBINDUNGEN_LISTE = "polling.jmxverbindung.ids";

    // Nachfolgend werden die Präfixe und Suffixe für JMX-Verbindungs-Konfigurationen definiert
    // Ein konkreter Schlüssel setzt sich aus CONF_POLLING_JMXVERBINDUNG_PRAEFIX + 
    // <JMX-Verbindung-Id> + Suffix zusammen

    /** Präfix für den Konfigurationsschlüssel für den Polling-Cluster. */
    public static final String CONF_POLLING_JMXVERBINDUNG_PRAEFIX = "polling.jmxverbindung.";

    /** Suffix für den Konfigurationsschlüssel für den Host der JMX-Verbindung. */
    public static final String CONF_POLLING_JMXVERBINDUNG_HOST_SUFFIX = ".host";

    /** Suffix für den Konfigurationsschlüssel für den Port der JMX-Verbindung. */
    public static final String CONF_POLLING_JMXVERBINDUNG_PORT_SUFFIX = ".port";

    /** Suffix für den Konfigurationsschlüssel für die Benutzer-ID der JMX-Verbindung. */
    public static final String CONF_POLLING_JMXVERBINDUNG_BENUTZER_SUFFIX = ".benutzer";

    /** Suffix für den Konfigurationsschlüssel für das Passwort der JMX-Verbindung. */
    public static final String CONF_POLLING_JMXVERBINDUNG_PASSWORT_SUFFIX = ".passwort";
    
}
