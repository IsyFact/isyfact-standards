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
package de.bund.bva.pliscommon.sicherheit.impl;

import java.util.Set;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.sicherheit.Berechtigungsmanager;
import de.bund.bva.pliscommon.sicherheit.Rolle;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AuthentifzierungErgebnis;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.pliscommon.sicherheit.common.exception.InitialisierungsException;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitKonfigurationSchluessel;
import de.bund.bva.pliscommon.sicherheit.kontext.ZertifikatInfoAufrufKontext;

/**
 * Diese Klasse dient als Implementierung der Komponente Sicherheit und kann als Bean in anderen Anwendungen
 * und Komponenten verwendet werden. Es wird das Interface Sicherheit implementiert, über welches
 * Berechtigungsmanager instanziiert werden können.
 *
 * @param <K> Typ des Aufrufkontextes
 * @param <E> Typ des Ergebnisses der Authentifizierung
 */
public class SicherheitImpl<K extends AufrufKontext, E extends AuthentifzierungErgebnis> implements
    Sicherheit<K>, InitializingBean, DisposableBean {

    /**
     * Logger der Klasse.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(SicherheitImpl.class);

    /** Defaultwert für die Time-to-live der Cacheinträge. 0 = deaktiviert. **/
    private static final int CACHE_DEFAULT_TTL = 0;

    /** Defaultwert für die maximale Anzahl an Einträgen im Cache (in Memory). **/
    private static final int CACHE_DEFAULT_MAX_ENTRIES = 10000;

    /** Pfad zum Rollen-Rechte-Mapping. */
    private String rollenRechteDateiPfad;

    /** Zugriff auf den AufrufKontextVerwalter. */
    private AufrufKontextVerwalter<K> aufrufKontextVerwalter;

    /** Das ausgelesene Rollenrechtemapping. */
    private RollenRechteMapping mapping;

    /** Referenz auf den AccessManager für den Zugriff auf Rollen/Rechte der Benutzer. */
    private AccessManager<K, E> accessManager;

    /** Zugriff auf die Aufrufkontext-Factory. */
    private AufrufKontextFactory<K> aufrufKontextFactory;

    /** Zugriff auf die Konfiguration. */
    private Konfiguration konfiguration;

    /**
     * Cache-Verwalter zum Cachen von Authentifizierungsinformationen, so dass der AccessManager entlastet
     * wird.
     **/
    private CacheVerwalter<E> cacheVerwalter = new CacheVerwalter<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Berechtigungsmanager getBerechtigungsManager() throws AuthentifizierungTechnicalException {

        // Der AufrufKontextverwalter muss durch die nutzende Anwendung korrekt bereitgestellt werden
        // Ist das nicht der Fall muss davon ausgegangen werden, dass die Authentifizierung fehlgeschlagen ist
        // und der Aufrufer dies bisher technisch nicht behandelt hat.
        if (this.aufrufKontextVerwalter == null) {
            throw new AuthentifizierungTechnicalException(
                SicherheitFehlerSchluessel.MSG_AUFRUFKONTEXT_NICHT_VERFUEGBAR);
        }

        // Der AufrufKontext muss durch die nutzende Anwendung korrekt bereitgestellt werden
        // Ist das nicht der Fall muss davon ausgegangen werden, dass die Authentifizierung fehlgeschlagen ist
        // und der Aufrufer dies bisher technisch nicht behandelt hat.
        K aufrufKontext = this.aufrufKontextVerwalter.getAufrufKontext();
        if (aufrufKontext == null) {
            throw new AuthentifizierungTechnicalException(
                SicherheitFehlerSchluessel.MSG_AUFRUFKONTEXT_NICHT_VERFUEGBAR);
        }

        // im Regelfall kommen die Rollen mit dem Aufrufkontext mit
        // falls nicht authentifiziere den Nutzer
        if (!aufrufKontext.isRollenErmittelt()) {
            return getBerechtigungsManagerUndAuthentifiziere(aufrufKontext);
        }
        return erzeugeBerechtigungsmanager(aufrufKontext);
    }

    /**
     * Erzeugt einen Berechtigungsmanager anhand eines Aufrufkontextes.
     * @param aufrufKontext Aufrufkontext
     * @return einen neuen Berechtigungmanager.
     */
    private Berechtigungsmanager erzeugeBerechtigungsmanager(K aufrufKontext) {
        BerechtigungsmanagerImpl berechtigungsmanager =
            new BerechtigungsmanagerImpl(aufrufKontext.getRolle());
        berechtigungsmanager.setRollenRechteMapping(this.mapping);
        return berechtigungsmanager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leereCache() {
        this.cacheVerwalter.leereCache();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    @SuppressWarnings("unchecked")
    public Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziereNutzer(String kennung,
        String passwort, String bhknz, String zertifikatOu, String correlationId)
        throws AuthentifizierungTechnicalException {
        return getBerechtigungsManagerUndAuthentifiziere((K) erzeugeAufrufKontextNutzer(kennung, passwort,
            bhknz, zertifikatOu, correlationId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(K unauthentifizierterAufrufKontext)
        throws AuthentifizierungTechnicalException {

        // Der AufrufKontext muss bereitgestellt werden, da dieser die zur Authentifizierung benötigten Daten
        // bereithält.
        if (unauthentifizierterAufrufKontext == null) {
            throw new AuthentifizierungTechnicalException(
                SicherheitFehlerSchluessel.MSG_AUFRUFKONTEXT_NICHT_VERFUEGBAR);
        }

        Object cacheKey = this.accessManager.erzeugeCacheSchluessel(unauthentifizierterAufrufKontext);
        E authentifzierungErgebnis = null;
        if (cacheKey != null) {
            authentifzierungErgebnis = this.cacheVerwalter.getFromCache(cacheKey);
        }
        if (authentifzierungErgebnis == null) {
            // Zugriff auf den AccessManager, um Informationen über aktuellen Benutzer zu ermitteln
            try {
                authentifzierungErgebnis =
                    this.accessManager.authentifiziere(unauthentifizierterAufrufKontext);
                this.cacheVerwalter.putIntoCache(cacheKey, authentifzierungErgebnis);
            } finally {
                if (authentifzierungErgebnis != null) {
                    this.accessManager.logout(authentifzierungErgebnis);
                }
            }
        }
        if (authentifzierungErgebnis == null) {
            throw new AuthentifizierungTechnicalException("AccessManager hat kein Ergebnis geliefert.");
        }
        aktualisiereAufrufKontextAusAuthentifizierungErgebnis(
            unauthentifizierterAufrufKontext.getKorrelationsId(), authentifzierungErgebnis);
        return erzeugeBerechtigungsmanager(this.aufrufKontextVerwalter.getAufrufKontext());
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    @SuppressWarnings("unchecked")
    public Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(String kennung, String passwort,
        String clientZertifikat, String clientZertifikatDn, String correlationId)
        throws AuthentifizierungTechnicalException {
        return getBerechtigungsManagerUndAuthentifiziere((K) erzeugeAufrufKontext(kennung, passwort,
            clientZertifikat, clientZertifikatDn, correlationId));
    }

    /**
     * Schreibt Daten aus einer Benutzersession in den Aufrufkontext.
     *
     * @param correlationId            Korrelations-ID des Service-Aufrufs
     * @param authentifzierungErgebnis Ergebnis der Authentifizierung
     */
    private void aktualisiereAufrufKontextAusAuthentifizierungErgebnis(String correlationId,
        E authentifzierungErgebnis) {

        // Erzeuge Aufrufkontext aufbauend auf den Session-Informationen, oder befülle vorhandenen
        K aufrufKontext = this.aufrufKontextVerwalter.getAufrufKontext();

        boolean neuerAufrufKontext = false;
        if (aufrufKontext == null) {
            aufrufKontext = this.aufrufKontextFactory.erzeugeAufrufKontext();
            this.aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);
            neuerAufrufKontext = true;
        }

        this.accessManager.befuelleAufrufkontext(aufrufKontext, authentifzierungErgebnis);
        aufrufKontext.setKorrelationsId(correlationId);

        // Falls der AufrufKontext neu erzeugt wurde, löse Lifecycle aus.
        if (neuerAufrufKontext) {
            this.aufrufKontextFactory.nachAufrufKontextVerarbeitung(aufrufKontext);
        }
    }

    /**
     * Setzt das Feld {@link #konfiguration}.
     * @param konfiguration
     *            Neuer Wert für konfiguration
     */
    @Required
    public void setKonfiguration(Konfiguration konfiguration) {
        this.konfiguration = konfiguration;
    }

    /**
     * Setzt das Feld 'aufrufKontextVerwalter'.
     * @param aufrufKontextVerwalter
     *            Neuer Wert für aufrufKontextVerwalter
     */
    @Required
    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<K> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    /**
     * Setzt das Feld {@link #aufrufKontextFactory}.
     * @param aufrufKontextFactory
     *            Neuer Wert für aufrufKontextFactory
     */
    @Required
    public void setAufrufKontextFactory(AufrufKontextFactory<K> aufrufKontextFactory) {
        this.aufrufKontextFactory = aufrufKontextFactory;
    }

    /**
     * Setzt den Pfad zur Datei mit dem Rollen-Rechte-Mapping.
     * @param rollenRechteDateiPfad
     *            Neuer Wert für rollenRechteDateiPfad
     */
    @Required
    public void setRollenRechteDateiPfad(String rollenRechteDateiPfad) {
        this.rollenRechteDateiPfad = rollenRechteDateiPfad;
    }

    /**
     * Setzt den {@link AccessManager}, welcher für das Auslesen der Rollen und Rechte zuständig ist.
     *
     * @param accessManager
     *            AccessManager.
     */
    public void setAccessManager(AccessManager<K, E> accessManager) {
        this.accessManager = accessManager;
    }

    /**
     * Setzt die Konfiguration des Caches.
     * @param cacheKonfiguration
     *            Die zu setzende Cache-Konfiguration
     */
    public void setCacheKonfiguration(String cacheKonfiguration) {
        this.cacheVerwalter.setCacheKonfiguration(cacheKonfiguration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info(LogKategorie.SICHERHEIT, EreignisSchluessel.INITIALISIERUNG_SICHERHEIT,
            "Initialisiere Sicherheitskomponente mit RollenRechteDatei: {}", this.rollenRechteDateiPfad);
        if (this.rollenRechteDateiPfad == null || this.rollenRechteDateiPfad.isEmpty()) {
            throw new InitialisierungsException(
                SicherheitFehlerSchluessel.MSG_INITIALISIERUNGS_ELEMENT_FEHLT, "rollenRechteDateiPfad");
        }
        if (null == this.aufrufKontextVerwalter) {
            throw new InitialisierungsException(
                SicherheitFehlerSchluessel.MSG_INITIALISIERUNGS_ELEMENT_FEHLT, "aufrufKontextVerwalter");
        }

        XmlAccess access = new XmlAccess();
        this.mapping = access.parseRollenRechteFile(this.rollenRechteDateiPfad);

        int cacheTimeToLive =
            this.konfiguration.getAsInteger(SicherheitKonfigurationSchluessel.CONF_CACHE_TTL,
                CACHE_DEFAULT_TTL);
        int cacheMaxEntriesInMemory =
            this.konfiguration.getAsInteger(SicherheitKonfigurationSchluessel.CONF_CACHE_MAX_ELEMENTE,
                CACHE_DEFAULT_MAX_ENTRIES);

        // Wenn kein Cache konfiguriert wurde oder der Cache deaktiviert wurde, wird das Caching abgeschaltet.
        if (cacheTimeToLive == 0) {
            this.cacheVerwalter.setCacheAktiviert(false);
        } else {
            this.cacheVerwalter.setCacheAktiviert(true);
            this.cacheVerwalter.setTimeToLiveSeconds(cacheTimeToLive);
            this.cacheVerwalter.setMaxEntriesLocalHeap(cacheMaxEntriesInMemory);
        }

        LOG.debug("Initialisierung der Sicherheitskomponente beendet.");
    }

    /**
     * Hilfsmethode zur Erzeugung eines Aufrufkontext, um veraltetes Authentifzierungsschema mit einzelnen
     * Parametern anstelle eines AufrufKontext in {@link SicherheitImpl} zu unterstützen.
     * @see Sicherheit#getBerechtigungsManagerUndAuthentifiziere(String, String, String, String, String) *
     */
    @Deprecated
    public static ZertifikatInfoAufrufKontext erzeugeAufrufKontext(String kennung, String passwort,
        String clientZertifikat, String clientZertifikatDn, String correlationId) {
        LOG.debugFachdaten("Erzeuge Berechtigungsmanager mit Authentifizierung für {}", kennung);

        // Prüfung der Parameter
        if (kennung == null || kennung.isEmpty()) {
            throw new AuthentifizierungFehlgeschlagenException("Benutzerkennung ist leer.");
        }
        if (passwort == null || passwort.isEmpty()) {
            throw new AuthentifizierungFehlgeschlagenException("Passwort ist leer.");
        }

        // entweder Zertifikat oder ZertifikatDn (oder beides) muss übergeben werden
        if ((clientZertifikat == null || clientZertifikat.isEmpty())
            && (clientZertifikatDn == null || clientZertifikatDn.isEmpty())) {
            throw new AuthentifizierungFehlgeschlagenException("Client-Zertifikat ist leer.");
        }

        ZertifikatInfoAufrufKontext kontext = new ZertifikatInfoAufrufKontext();
        kontext.setDurchfuehrenderBenutzerKennung(kennung);
        kontext.setDurchfuehrenderBenutzerPasswort(passwort);
        kontext.setClientZertifikat(clientZertifikat);
        kontext.setClientZertifikatDn(clientZertifikatDn);
        kontext.setKorrelationsId(correlationId);
        return kontext;
    }

    /**
     * Hilfsmethode zur Erzeugung eines Aufrufkontext, um veraltetes Authentifzierungsschema mit einzelnen
     * Parametern anstelle eines AufrufKontext in {@link SicherheitImpl} zu unterstützen.
     * @see Sicherheit#getBerechtigungsManagerUndAuthentifiziereNutzer(String, String, String, String, String)
     */
    @Deprecated
    public static ZertifikatInfoAufrufKontext erzeugeAufrufKontextNutzer(String kennung, String passwort,
        String bhknz, String zertifikatOu, String correlationId) {

        // Prüfung der Parameter
        if (kennung == null || kennung.isEmpty()) {
            LOG.debug("Authentifizierung fehlgeschlagen. Es wurde keine Benutzerkennung angegeben.");
            throw new AuthentifizierungFehlgeschlagenException("Benutzerkennung ist leer.");
        }
        if (passwort == null || passwort.isEmpty()) {
            LOG.debug("Authentifizierung fehlgeschlagen. Es wurde kein Passwort angegeben.");
            throw new AuthentifizierungFehlgeschlagenException("Passwort ist leer.");
        }

        if (bhknz == null || bhknz.isEmpty()) {
            LOG.debug("Authentifizierung fehlgeschlagen. Es wurde kein Behoerdenkennzeichen angegeben.");
            throw new AuthentifizierungFehlgeschlagenException("Behördenkennzeichen ist leer.");
        }

        ZertifikatInfoAufrufKontext kontext = new ZertifikatInfoAufrufKontext();
        kontext.setDurchfuehrenderBenutzerKennung(kennung);
        kontext.setDurchfuehrenderBenutzerPasswort(passwort);
        kontext.setDurchfuehrendeBehoerde(bhknz);
        kontext.setKorrelationsId(correlationId);
        kontext.setZertifikatOu(zertifikatOu);
        return kontext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.cacheVerwalter.shutdownManager();
    }

    @Override
    public Set<Rolle> getAlleRollen() {
        return this.mapping.getRollenRechteMapping().keySet();
    }

}
