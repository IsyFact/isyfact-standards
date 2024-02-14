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
package de.bund.bva.isyfact.konfiguration.common.konstanten;

/**
 * Diese Klasse enthält Ereignisschlüssel für das Logging.
 * <p>
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
public class EreignisSchluessel {

    /** Die Konfigurationsdatei {} wird neu geladen. */
    public static final String KONFIGURATION_DATEI_NEU_GELADEN = "EPLKON00001";

    /** Mindestens eine Konfigurationsdatei wurde geändert. */
    public static final String KONFIGURATION_DATEI_GEAENDERT = "EPLKON00002";

    /** Listener wurde nicht hinzugefügt, da die gleiche Instanz bereits registriert ist. */
    public static final String KONFIGURATION_LISTENER_NICHT_HINZUGEFUEGT = "EPLKON00003";

}
