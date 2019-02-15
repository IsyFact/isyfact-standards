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
package de.bund.bva.isyfact.konfiguration.common;

/**
 * Interface für Konfigurationen, die Aktualisierungen unterstützen.
 * 
 * 
 */
public interface ReloadableKonfiguration extends Konfiguration {

    /**
     * Prüft ob eine der Konfiguration aktualisiert wurde. Dazu wird der Timestamp der Dateien verglichen.
     * Falls eine Datei aktualisiert wurde wird die Konfiguration aktualisiert und alle Listener
     * benachrichtigt.
     * 
     * @return <code>true</code> falls eine der Konfigurationsdateien aktualisiert wurde.
     */
    public boolean checkAndUpdate();

    /**
     * Registriert einen neuen Listener. Wenn die Instanz bereits registiert ist, wird sie nicht erneut
     * hinzugefügt um doppelte Benachrichtigungen zu vermeiden.
     * @param listener
     *            {@link KonfigurationChangeListener}, der über Änderungen der Konfigurationsparameter
     *            informiert werden möchte.
     */
    public void addKonfigurationChangeListener(KonfigurationChangeListener listener);

    /**
     * Entfernt einen Listener aus der Liste.
     * @param listener
     *            zu entfernender Listener.
     */
    public void removeKonfigurationChangeListener(KonfigurationChangeListener listener);

    /**
     * Überprüft, ob ein Listener bereits registriert wurde.
     * @param listener
     *            zu überprüfender Listener
     * @return <code>true</code>, wenn der Listener registriert ist. <code>false</code>, wenn der Listener
     *         nicht registriert ist.
     */
    public boolean hasKonfigurationChangeListener(KonfigurationChangeListener listener);

}
