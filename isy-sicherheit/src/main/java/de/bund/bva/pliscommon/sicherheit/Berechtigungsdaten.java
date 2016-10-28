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
package de.bund.bva.pliscommon.sicherheit;

/**
 * Dieses Interface definiert den Typ Berechtigungsdaten zu dem Rollen und Rechte gehören. Es ist nicht
 * geplant, dass es Implementierungen dieses Interfaces gibt, sondern nur Implementierungen von Subinterfaces.
 * 
 * @see Recht
 * @see Rolle
 * 
 */
public interface Berechtigungsdaten {

    /**
     * Gibt die Id (Name/Bezeichner) der jeweiligen Instanz zurück. Jede Instanz wird über Ihre Id
     * identifiziert.
     * 
     * @return Die Id der Instanz
     */
    String getId();
}
