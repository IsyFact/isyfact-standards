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
package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.sicherheit.SicherheitAdmin;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.accessmgr.AuthentifzierungErgebnis;

/**
 * Implementierung von SicherheitAdmin zur Überprüfung des Verbindungsaufbaus zum Access Manager.
 *
 */
public class SicherheitAdminImpl<T extends AufrufKontext> implements SicherheitAdmin {

    /** Referenz auf den AccessManager für den Zugriff auf Rollen/Rechte der Benutzer. */
    private final AccessManager<T, AuthentifzierungErgebnis> accessManager;

    /**
     * Erzeugt ein SicherheitAdmin, das den übergebenen AccessManager verwendet.
     *
     * @param accessManager
     *            der {@link AccessManager}, welcher für das Auslesen der Rollen und Rechte zuständig ist
     */
    public SicherheitAdminImpl(AccessManager<T, AuthentifzierungErgebnis> accessManager) {
        this.accessManager = accessManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean pingAccessManager() {
        return this.accessManager.pingAccessManager();
    }
}
