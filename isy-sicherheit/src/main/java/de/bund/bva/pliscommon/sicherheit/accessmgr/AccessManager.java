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
package de.bund.bva.pliscommon.sicherheit.accessmgr;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;

/**
 * Interface zum Zugriff auf den AccessManager.
 * 
 * 
 */
public interface AccessManager<K extends AufrufKontext, E extends AuthentifzierungErgebnis> {
    /**
     * 
     * @param unauthentifizierterAufrufKontext
     *            Das unauthentifizierte AufrufKontext-Objekt mit den Authentifizierungsdaten
     * @return Ein Token mit dem Ergebnis der Authentifzierung.
     * 
     * @throws AuthentifizierungTechnicalException
     *             Falls es technische Problemen bei der Authentifizierung gibt.
     * @throws AuthentifizierungFehlgeschlagenException
     *             Falls der {@link AccessManager} die Authentifizierung abgelehnt hat.
     */
    E authentifiziere(K unauthentifizierterAufrufKontext) throws AuthentifizierungTechnicalException,
        AuthentifizierungFehlgeschlagenException;

    /**
     * Führt ein Logout für die angegebene Authentifzierung durch.
     * 
     * @param authentifzierungErgebnis
     *            Die Authentifizierung für die der Logout durchgeführt werden soll
     */
    void logout(E authentifzierungErgebnis);

    /**
     * Dient zur Überprüfung der Verbindung zum Authentifizierungs-/Autorisierungs-Server.
     * 
     * @return <code>true</code>, falls die Verbindung erfolgreich aufgebaut werden konnte, ansonsten
     *         <code>false</code>.
     */
    boolean pingAccessManager();

    /**
     * 
     * Dient zur Überprüfung der Verbindung zum Authentifizierungs-/Autorisierungs-Server, indem ein Login/
     * Logout durchgeführt wird. Hierdurch wird geprüft, ob der Server Anfragen korrekt verarbeiten kann.
     * 
     * @param unauthentifizierterAufrufKontext
     *            Das unauthentifizierte AufrufKontext-Objekt mit den Authentifizierungsdaten
     * 
     * @return <code>true</code>, falls ein Login/ Logout erfolgreich durchgeführt werden konnte, ansonsten
     *         <code>false</code>.
     */
    boolean pingAccessManagerByLoginLogout(K unauthentifizierterAufrufKontext);

    /**
     * Befüllt den übergebenen AufrufKontext mit den Ergebnissen des Authentifizierungsaufurf.
     * @param aufrufKontext
     *            Der zu befüllende Aufrufkontext
     * @param authentifzierungErgebnis
     *            Das Ergebnis der Authentifzierung
     */
    void befuelleAufrufkontext(K aufrufKontext, E authentifzierungErgebnis);

    /**
     * Erzeugt den Schlüssel unter dem der Aufrufkontext gecached wird. Im Cache der PLIS-Sicherheit wird
     * anhand des hier zurückgegebenen Keys geprüft, ob bereits eine Authentifizierung durchgeführt wurde.
     * Falls ja, wird das {@link AuthentifzierungErgebnis} aus dem Cache verwendet und der
     * {@link AccessManager} nicht erneut aufgerufen. Der Cache-Key muss also alle für die Authentifizierung
     * relevanten Attribute umfassen (Kennung, Passwort, etc). Wird <code>null</code> zurückgeliefert, wird
     * kein Caching durchgeführt.
     * @param aufrufKontext
     *            AufrufKontext für den gerade eine Authentifzierung durchgeführt wird.
     * @return Key für das Caching des Aufrufkontext oder <code>null</code>.
     */
    Object erzeugeCacheSchluessel(K aufrufKontext);
}
