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
package de.bund.bva.isyfact.ueberwachung.common.konstanten;

/**
 * Diese Klasse enthält Ereignisschlüssel für das Logging.
 */
public class EreignisSchluessel {

    /** Infos Loadbalancer. */
    public static final String PLUEB00001 = "PLUEB00001";

    /** Essentielles Nachbarsystem {} nicht erreicht. Status: {} */
    public static final String NACHBARSYSTEM_ESSENTIELL_NICHT_ERREICHBAR = "PLUEB00002";

    /** Nicht-essentielles Nachbarsystem {} nicht erreicht. Status: {} */
    public static final String NACHBARSYSTEM_NICHT_ESSENTIELL_NICHT_ERREICHBAR = "PLUEB00003";
}
