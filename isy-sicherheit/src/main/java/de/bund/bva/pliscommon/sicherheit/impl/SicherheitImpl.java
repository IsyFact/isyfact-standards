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

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.sicherheit.Berechtigungsmanager;
import de.bund.bva.pliscommon.sicherheit.Rolle;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AuthentifzierungErgebnis;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.pliscommon.sicherheit.common.exception.InitialisierungsException;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import de.bund.bva.pliscommon.sicherheit.config.IsySicherheitConfigurationProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

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

    /** Pfad zum Rollen-Rechte-Mapping. */
    private final String rollenRechteDateiPfad;

    /** Zugriff auf den AufrufKontextVerwalter. */
    private final AufrufKontextVerwalter<K> aufrufKontextVerwalter;

    /** Das ausgelesene Rollenrechtemapping. */
    private RollenRechteMapping mapping;

    /** Referenz auf den AccessManager für den Zugriff auf Rollen/Rechte der Benutzer. */
    private final AccessManager<K, E> accessManager;

    /** Zugriff auf die Aufrufkontext-Factory. */
    private AufrufKontextFactory<K> aufrufKontextFactory;

    private final IsySicherheitConfigurationProperties properties;

    /**
     * Cache-Verwalter zum Cachen von Authentifizierungsinformationen, so dass der AccessManager entlastet
     * wird.
     **/
    private final CacheVerwalter<E> cacheVerwalter = new CacheVerwalter<>();

    public SicherheitImpl(String rollenRechteDateiPfad, AufrufKontextVerwalter<K> aufrufKontextVerwalter,
        AufrufKontextFactory<K> aufrufKontextFactory, AccessManager<K, E> accessManager,
        IsySicherheitConfigurationProperties properties) {
        this.rollenRechteDateiPfad = rollenRechteDateiPfad;
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
        this.accessManager = accessManager;
        this.aufrufKontextFactory = aufrufKontextFactory;
        this.properties = properties;
    }

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
        if (this.aufrufKontextVerwalter == null) {
            throw new InitialisierungsException(
                SicherheitFehlerSchluessel.MSG_INITIALISIERUNGS_ELEMENT_FEHLT, "aufrufKontextVerwalter");
        }

        XmlAccess access = new XmlAccess();
        this.mapping = access.parseRollenRechteFile(this.rollenRechteDateiPfad);

        int cacheTimeToLive = properties.getTtl();
        int cacheMaxEntriesInMemory = properties.getMaxelements();

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
