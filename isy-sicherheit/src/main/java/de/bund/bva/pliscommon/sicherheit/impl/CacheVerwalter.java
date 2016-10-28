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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.generator.ConfigurationSource;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AuthentifzierungErgebnis;

/**
 * Verwalter-Klasse für den Cache. Diese Implementierung kümmert sich auch um die Verwaltung des Status des
 * Caches.
 *
 */
public class CacheVerwalter<E extends AuthentifzierungErgebnis> {

    /** Der Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(CacheVerwalter.class);

    /** Name des Cache-Managers für die Sicherheitskomponente. */
    private static final String CACHE_MANAGER_NAME = "de.bund.bva.pliscommon.sicherheit.client.cachemanager";

    /** Name des Authentifizierungs-Caches. */
    private static final String AUTHENTIFIZIERUNGEN_CACHE_NAME = "authentifizierungen";

    /**
     * Pfad zur Standard-Konfiguration des Cache-Managers.
     */
    private static final String DEFAULT_CACHE_MANAGER_KONFIGURATION_PFAD =
        "/resources/plis-sicherheit/ehcache/sicherheitcache.default.xml";

    /** Der Cache-Verwalter. */
    private CacheManager cacheManager;

    /** Der Cache für die Authentifizierungen. */
    private Cache authentifizierungenCache;

    /** Kennzeichen, ob der Cache aktiviert ist. */
    private boolean cacheAktiviert = false;

    /**
     * Standard-Konstruktor.
     */
    public CacheVerwalter() {
        this.cacheManager = erzeugeCacheManager(DEFAULT_CACHE_MANAGER_KONFIGURATION_PFAD);
        this.authentifizierungenCache = this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
    }

    /**
     * Liefert die Anzahl <em>nicht</em> abgelaufener Elemente im Cache. Hierbei werden zuvor auf dem Cache
     * alle abgelaufenen Elemente enfernt. Dies ist notwendig, da das Entfernen von abgelaufenen Elementen aus
     * einem Cache nur für DiskStoreage-Caches automatisch funktioniert. Für In-Memory-Caches muss dies
     * explizit getan werden.
     *
     * @return Anzahl der Elemente im Cache.
     */
    public int getAnzahlElementeImCache() {
        // Workaround: Cache säubern, da In-Memory-Caches nicht gesaeubert werden.
        this.authentifizierungenCache.evictExpiredElements();

        return this.authentifizierungenCache.getSize();
    }

    /**
     * Leert den Berechtigungen-Cache.
     */
    public void leereCache() {
        this.authentifizierungenCache.removeAll();
    }

    /**
     * Holt ein Element aus dem Cache.
     * @param key
     *            Cacheschlüssel.
     * @return Die Sessiondaten des Access-Managers, wenn zum Schlüssel ein Eintrag im Cache enthalten ist.
     *         Ansonsten <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public E getFromCache(Object key) {
        Element cacheElement = this.authentifizierungenCache.get(key);
        if (cacheElement != null) {
            return (E) cacheElement.getObjectValue();
        } else {
            return null;
        }
    }

    /**
     * Legt ein neues Element im Cache ab.
     * @param key
     *            Cacheschlüssel
     * @param authentifzierungErgebnis
     *            Der Wert, der in dem Cache abgelegt wird.
     */
    public void putIntoCache(Object key, E authentifzierungErgebnis) {
        if (this.cacheAktiviert) {
            this.authentifizierungenCache.put(new Element(key, authentifzierungErgebnis));
        }
    }

    /**
     * Erzeugt einen neuen Cache-Manager mit der angegebenen Konfiguration.
     * @param cacheKonfiguration
     *            Die Cache-Konfiguration.
     * @return Cache-Manager
     */
    private CacheManager erzeugeCacheManager(String cacheKonfiguration) {
        // Eindeutigen Namen für den Cache-Manager erzeugen.
        String name = CACHE_MANAGER_NAME;
        int zaehler = 0;
        while (CacheManager.getCacheManager(name) != null) {
            zaehler++;
            name = CACHE_MANAGER_NAME + "-" + zaehler;
        }

        // neuen Cache-Manager anlegen.
        ConfigurationSource configSource =
            ConfigurationSource.getConfigurationSource(getClass().getResource(cacheKonfiguration));
        Configuration config = configSource.createConfiguration();
        config.setName(name);
        CacheManager cacheManager = new CacheManager(config);

        return cacheManager;
    }

    /**
     * Terminiert den Cache-Manager und initialisiert einen neuen Cache- Manager mit der übergebenen
     * Cache-Konfiguration.
     *
     * @param cacheKonfiguration
     *            Die zu setzende Cache-Konfiguration.
     */
    public void setCacheKonfiguration(String cacheKonfiguration) {
        synchronized (this.cacheManager) {
            LOG.debug("Setzen der Cache Konfiguration.");

            // Cache-Manager herunterfahren.
            this.cacheManager.shutdown();
            LOG.debug("Cache-Manager herunterfahren.");

            // neuen Cache-Manager anlegen.
            this.cacheManager = erzeugeCacheManager(cacheKonfiguration);
            LOG.debug("Neuer Cache-Manager angelegt.");

            // Mapping Cache explizit speichern
            this.authentifizierungenCache = this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
            // Falls Cache nicht vorhanden (er wurde nicht explizit
            // konfiguriert): anlegen
            if (this.authentifizierungenCache == null) {
                this.cacheManager.addCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
                this.authentifizierungenCache = this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
            }
            LOG.debug("Anwenderdaten-Cache angelegt.");
        }
    }

    /**
     * Setzt die Zeit, die ein Objekt innerhalb des Anwenderdaten-Caches gültig bleibt. Nach Ablauf dieser
     * Zeit wird das Objekt ungültig und muss erneut aus der Datenquelle geladen werden.
     * <p>
     * Die Zeit wird nur beachtet, wenn der Typ des Caches <b><u>nicht eternal</u></b> ist.
     *
     * @param timeToLiveSeconds
     *            Die Zeitspanne in Sekunden, für die Objekte gültig bleiben sollen.
     */
    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME).getCacheConfiguration()
            .setTimeToLiveSeconds(timeToLiveSeconds);
    }

    /**
     * Setzt die maximale Anzahl der im Speicher gecachten Elemente.
     *
     * @param maxEntriesLocalHeap
     *            Die maximale Anzahl der im Speicher gecachten Elemente.
     */
    public void setMaxEntriesLocalHeap(int maxEntriesLocalHeap) {
        this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME).getCacheConfiguration()
            .setMaxEntriesLocalHeap(maxEntriesLocalHeap);
    }

    /**
     * Liefert das Feld 'cacheAktiviert' zurück.
     * @return Wert von cacheAktiviert
     */
    public boolean isCacheAktiviert() {
        return this.cacheAktiviert;
    }

    /**
     * Setzt das Feld 'cacheAktiviert'.
     * @param cacheAktiviert
     *            Neuer Wert für cacheAktiviert
     */
    public void setCacheAktiviert(boolean cacheAktiviert) {
        this.cacheAktiviert = cacheAktiviert;
    }

    /**
     * Beendet den CacheManager. Diese Methode sollte nur in der <code>destroy()</code>-Methode der
     * <code>DisposableBean</code> aufgerufen werden, welche die Referenz auf den <code>CacheVerwalter</code>
     * hält.
     */
    public void shutdownManager() {
        this.cacheManager.shutdown();
        LOG.debug("Cache-Manager herunterfahren.");
    }
}
