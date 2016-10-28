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
package de.bund.bva.pliscommon.ueberwachung.admin;

import java.util.concurrent.Callable;

/**
 * Das Interface zur Administration der Anwendung.
 *
 * @author Capgemini, Jan Mundo
 * @version $Id: Watchdog.java 128443 2015-01-20 14:50:43Z sdm_sspielmann $
 */
public interface Watchdog {
    /**
     * Diese Methode prüft die Funktionsfähigkeit einer Anwendung. Sie wird automatisch in regelmäßigen
     * Abständen vom Watchdog aufgerufen.
     *
     * Die Methode führt ein eigenes Exception-Handling durch. Alle Throwables werden gefangen und geloggt, um
     * zu verhindern, dass diese den eigentlichen Timer-Thread erreichen.
     *
     * Diese Methode darf nicht im Service-Interface angeboten werden.
     *
     * @return <code>true</code> falls die Prüfung erfolgreich war, ansonsten <code>false</code>.
     */
    public boolean pruefeSystem();

    /**
     * Ergänzt eine Prüfung für den Selbstest. Die Prüfung gilt als erfolgreich, wenn innerhalb des Timeouts
     * <code>true</code> vom {@link Callable} geliefert wird. Bei <code>false</code> oder Auftreten einer
     * Exception gilt die Prüfung als gescheitert.
     * @param beschreibung
     *            Beschreibung der Prüfung, z.B. für Log-Ausgaben.
     * @param pruefung
     *            Implementierung der Prüfung. Gibt <code>true</code> wenn Prüfung erfolgreich.
     */
    public void addPruefung(String beschreibung, Callable<Boolean> pruefung);
}
