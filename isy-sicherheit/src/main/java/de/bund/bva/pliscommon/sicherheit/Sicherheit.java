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
    public void leereCache();

    /**
     * Erzeugt und initialisiert einen Berechtigungsmanager. Dazu erfolgt zunächst eine Authentifzierung des
     * Benutzers am Access-Manager mithilfe der Kennung, dem Passwort und dem Zertifikat (im Testmodus ist
     * auch eine direkte Übergabe des DN möglich).
     * <p>
     * <u>Hinweis:</u>Für Serviceaufrufe außerhalb der PLIS-Anwendungslandschaft wird die Verwendung dieser
     * Methode unter Benutzung des mitgelieferten Zertifikats empfohlen.
     *
     * @param kennung
     *            Der Anmeldename.
     * @param passwort
     *            Das Passwort.
     * @param clientZertifikat
     *            Zertifikat des Benutzers.
     * @param clientZertifikatDn
     *            Optinale Zertifikat-DN des Benutzers, kann zum Testen anstatt eines Zertifikats angegeben
     *            werden.
     * @param correlationId
     *            Korrelations-Id.
     *
     * @return Eine korrekt initialisierte Instanz des Berechtigungsmanager.
     *
     * @throws AuthentifizierungFehlgeschlagenException
     *             Wird geworfen, wenn der Access-Manager die Authentifzierung abgelehnt hat.
     * @throws AuthentifizierungTechnicalException
     *             Wird geworfen, falls es beim Zugriff auf den Authentifizierungs-Server zu einem technischen
     *             Problem kommt.
     * @deprecated Diese Methode ist technologiespezifisch und nur noch aus Rückwärtskompatibilitätsgründen
     *             hier. Sie wird mittelfristig entfernt. Es sollte die allgemeine Methode
     *             {@link #getBerechtigungsManagerUndAuthentifiziere(AufrufKontext)} verwendet werden.
     */
    @Deprecated
    Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(String kennung, String passwort,
        String clientZertifikat, String clientZertifikatDn, String correlationId)
        throws AuthentifizierungTechnicalException;

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
     * Erzeugt und initialisiert einen Berechtigungsmanager. Dazu erfolgt zunächst eine Authentifzierung des
     * Benutzers am Access-Manager mithilfe mithilfe der Kennung, dem Passwort, BHKNZ und Zertifikat-OU.
     * <p>
     * <u>Hinweis:</u>Für Serviceaufrufe innerhalb der PLIS-Anwendungslandschaft wird die Verwendung dieser
     * Methode empfohlen, da eine Authentifizierung mittels Zertifikat nicht notwendig ist. Es genügt die
     * direkte Angabe des BHKNZ und der Zertifikat-OU.
     *
     * @param kennung
     *            Der Anmeldename des Nutzers.
     * @param passwort
     *            Das Passwort.
     * @param bhknz
     *            BHKNZ des Nutzers.
     * @param zertifikatOu
     *            Zertifikat-OU.
     * @param correlationId
     *            Korrelations-Id des Service-Aufrufs.
     *
     * @return Eine korrekt initialisierte Instanz des Berechtigungsmanager.
     *
     * @throws AuthentifizierungFehlgeschlagenException
     *             Wird geworfen, wenn der Benutzer durch ungültige Parameter nicht am
     *             Authentifizierungs-Server authentifiziert werden kann.
     * @throws AuthentifizierungTechnicalException
     *             Wird geworfen, falls es beim Zugriff auf den Authentifizierungs-Server zu einem technischen
     *             Problem kommt.
     * @deprecated Diese Methode ist technologiespezifisch und nur noch aus Rückwärtskompatibilitätsgründen
     *             hier. Sie wird mittelfristig entfernt. Es sollte die allgemeine Methode
     *             {@link #getBerechtigungsManagerUndAuthentifiziere(AufrufKontext)} verwendet werden.
     */
    @Deprecated
    Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziereNutzer(String kennung, String passwort,
        String bhknz, String zertifikatOu, String correlationId) throws AuthentifizierungTechnicalException;

    /**
     * Liefert alle im System hinterlegten Rollen.
     * @return Alle im RollenRechte-Mapping hinterlegten Rollen.
     */
    Set<Rolle> getAlleRollen();
}
