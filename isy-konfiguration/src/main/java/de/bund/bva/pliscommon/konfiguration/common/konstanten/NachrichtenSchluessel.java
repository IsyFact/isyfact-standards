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
package de.bund.bva.pliscommon.konfiguration.common.konstanten;

/**
 * Diese Klasse enthaelt Konstanten fuer alle in Resource-Bundles verwendeten Schluessel.
 *
 */
public abstract class NachrichtenSchluessel {
    /** Der angegebene Konfigurationsparameter ist nicht gesetzt. */
    public static final String ERR_PARAMETER_LEER = "KONF100";

    /** Der angegebene Konfigurationsparameter hat ein ungültiges Format. */
    public static final String ERR_PARAMETERWERT_UNGUELTIG = "KONF101";

    /** Konfigurationsdatei konnte nicht geladen werden. */
    public static final String ERR_DATEI_LESEN = "KONF200";

    /** Konfigurationsdatei konnte nicht geladen werden. */
    public static final String ERR_DATEI_NICHT_GEFUNDEN = "KONF201";

    /** Konfigurationsdatei konnte nicht geladen werden. */
    public static final String ERR_DATEI_FORMAT = "KONF202";

    /** Der Pfad des Property-Ordners sollte mit einem "/" enden. */
    public static final String ERR_PROPERTY_ORDNER_PFAD = "KONF203";

}
