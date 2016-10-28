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
package de.bund.bva.pliscommon.util.monitoring;

/**
 * Performance-Monitor zur Messung von Laufzeiten in der Anwendung.
 * Die Laufzeiten werden pro Request gemessen. Dabei können pro Request,
 * verschiedene Teile unter verschiedenen Tags gemessen werden.
 * Z.B. alle SVZ-Aufrufe eines Request unter dem Tag "svz", alle Datenbank-Aufrufe unter dem Tag "jpa".
 * Über {@link #beginRequest()} und {@link #endRequest(String)} werden die Request-Grenzen markiet.
 * Die Laufzeitmessungf erfolgt mit {@link #start(String)} und {@link #stop(String)}.
 * Laufzeiten können nur innerhalb eines Requests gemessen werden.
 * Auf Start muss immer genau ein Stop für denselben Tag-Namen erfolgen.
 * 
 *
 */
public interface PerformanceMonitor {
    
    /**
     * Markiert den Beginn eines Request.
     */
    public void beginRequest();
    
    /**
     * Markiert das Ende eines Requests.
     * @param logInfo extra Info, die im Log ausgegeben wird, um den Request eindeutig zu erkennen.
     */
    public void endRequest(String logInfo);
    
    /**
     * Beginnt die Laufzeitmessung für den Tag-Namen.
     * @param tag Name des Tags.
     */
    public void start(String tag);
    
    /**
     * Beendet die Laufzeitmessung für den Tag-Namen.
     * @param tag der Name des Tags
     */
    public void stop(String tag);
}
