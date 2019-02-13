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

import java.util.Set;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;

/**
 * Interface zum Zugriff auf Berechtigungsmanager.
 *
 */
public interface Sicherheit<K extends AufrufKontext> {

    /**
     * Erzeugt und Initialisiert einen Berechtigungsmanager. Es werden die im Rahmen der Authentifizierung am
     * Access-Manager bereits ermittelten Rollen über den vorliegenden Aufrufkontext ermittelt.
     *
     * @return Eine korrekt initialisierte Instanz des Berechtigungsmanager
     *
     * @throws AuthentifizierungFehlgeschlagenException
     *             Wird geworfen, wenn der Benutzer durch ungültige Parameter nicht am
     *             Authentifizierungs-Server authentifiziert werden kann.
     * @throws AuthentifizierungTechnicalException
     *             Wird geworfen wenn kein AufrufKontext gefunden werden kann, oder falls der Zugriff auf den
     *             Authentifizierungs-Server nicht erfolgreich ist.
     */
    Berechtigungsmanager getBerechtigungsManager() throws AuthentifizierungTechnicalException;

    /**
     * Leert den Cache von PLIS-Sicherheit. Authentifizierungsdaten werden nach Aufruf der Methode geloescht.
     * Es erfolgt fuer folgende Benutzeraufrufe zunaechst jeweils wieder eine Authentifizierung ueber den
     * {@link AccessManager}.
     */
    void leereCache();

    /**
     * Erzeugt und initialisiert einen Berechtigungsmanager. Dazu erfolgt zunächst eine Authentifzierung des
     * Benutzers am Access-Manager mithilfe der Access-Manager-spezifischen Authentifizierungsdaten
     *
     * @param unauthentifizierterAufrufKontext
     *            Das unauthentifizierte AufrufKontext-Objekt mit den Authentifizierungsdaten
     * @return Den Berechtigungsmanager, falls die Authentifizierung erfolgreich ist.
     * @throws AuthentifizierungFehlgeschlagenException
     *             Falls die Authentifizierung fachlich nicht erfolgreich ist.
     * @throws AuthentifizierungTechnicalException
     *             Falls es technische Problemen bei der Authentifizierung gibt.
     */
    Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(K unauthentifizierterAufrufKontext)
        throws AuthentifizierungTechnicalException;

    /**
     * Liefert alle im System hinterlegten Rollen.
     * @return Alle im RollenRechte-Mapping hinterlegten Rollen.
     */
    Set<Rolle> getAlleRollen();
}
