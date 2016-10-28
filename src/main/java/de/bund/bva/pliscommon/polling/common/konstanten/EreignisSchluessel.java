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
 * Diese Klasse enthält Ereignisschlüssel für das Logging.
 * @author Capgemini, Marcel Vielsack
 * @version $Id:$
 */
public class EreignisSchluessel {

    /** Es wurde kein JMX Verbindungsparameter angegeben. */
    public static final String KEIN_JMX_VERBINDUNGS_PARAM = "EPLPOL00001";

    /** Die JMX Verbindung wurde nicht geschlossen. */
    public static final String JMX_VERBINDUNG_NICHT_GESCHLOSSEN = "EPLPOL00002";

    /** Fehler in der Konfig des Polling clusters. */
    public static final String POLLING_CLUSTER_KONFIG_FEHLER = "EPLPOL00003";

    /** Polling Cluster unbekannt. */
    public static final String POLLING_CLUSTER_UNBEKANNT = "EPLPOL00004";

}
