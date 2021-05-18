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
package de.bund.bva.pliscommon.serviceapi.common.konstanten;

/**
 * This class contains all event keys for isy-serviceapi-core.
 */
public class EreignisSchluessel {

    /**
     * Für die Serviceoperation {} ist kein Exception-Mapping für Exceptionklasse {} definiert. Benutze
     * stattdessen technische TO-Exception {}:
     */
    public static final String KEIN_EXCEPTION_MAPPING_DEFINIERT = "EPLSAC00002";

    /** Fehler bei der Fehlerbehandlung. ODER Usprünglicher Fehler war {}. */
    public static final String FEHLER_FEHLERBEHANDLUNG = "EPLSAC00003";

    /** Validiere Konfiguration für Service-Implementierung {}. */
    public static final String VALIDIERUNG_KONFIGURATION = "EPLSAC00004";

    /** Beim Aufrufen des Services [{}] ist ein Timeout aufgetreten. */
    public static final String TIMEOUT = "EPLSAC00005";

    /** Aufruf nach Timeout abgebrochen. */
    public static final String TIMEOUT_ABBRUCH = "EPLSAC00006";

    /** Warte {}ms bis zur Wiederholung des Aufrufs. */
    public static final String TIMEOUT_WARTEZEIT = "EPLSAC00007";

    /** Warten auf Aufrufwiederholung abgebrochen. */
    public static final String TIMEOUT_WARTEZEIT_ABBRUCH = "EPLSAC00008";

    /** Wiederhole Aufruf... */
    public static final String TIMEOUT_WIEDERHOLUNG = "EPLSAC00009";

    /**
     * Die Korrelations-Id {} im Aufrufkontext wurde korrigiert, da diese nicht mit der Korr-Id auf dem MDC {}
     * übereinstimmt.
     */
    public static final String AUFRUFKONTEXT_KORRID_KORRIGIERT = "EPLSAC00010";

    /** Keinen Authorization-Header mit Bearer-Token empfangen. Es wird nicht in den AufrufKontextVerwalter gesetzt. */
    public static final String KEIN_BEARER_TOKEN_UEBERMITTELT = "EPLASC00012";

    /** Kein Bearer-Token im AufrufKontextVerwalter. Der Authorization-Header wird nicht gesetzt. */
    public static final String KEIN_BEARER_TOKEN_IM_AUFRUFKONTEXT = "EPLASC00013";

}
