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
 * Manager class for the cache. This implementation also takes care of managing the state of the cache.
 */
public class CacheVerwalter<E extends AuthentifzierungErgebnis> {

    /** The Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(CacheVerwalter.class);

    /** Name of the cache manager for the security component. */
    private static final String CACHE_MANAGER_NAME = "de.bund.bva.pliscommon.sicherheit.client.cachemanager";

    /** Name of the authentication cache. */
    private static final String AUTHENTIFIZIERUNGEN_CACHE_NAME = "authentifizierungen";

    /** Path to the default configuration of the Cache Manager. */
    private static final String DEFAULT_CACHE_MANAGER_KONFIGURATION_PFAD =
        "/resources/plis-sicherheit/ehcache/sicherheitcache.default.xml";

    /** The cache manager. */
    private CacheManager cacheManager;

    /** The cache for the authentications. */
    private Cache authentifizierungenCache;

    /** Indicator whether the cache is enabled. */
    private boolean cacheAktiviert = false;

    /**
     * Standard constructor.
     */
    public CacheVerwalter() {
        this.cacheManager = erzeugeCacheManager(DEFAULT_CACHE_MANAGER_KONFIGURATION_PFAD);
        this.authentifizierungenCache = this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
    }

    /**
     * Returns the number of <em>not</em> expired elements in the cache.
     * <p>
     * Here, all expired elements are removed from the cache beforehand. This is necessary because removing expired
     * elements from a cache only works automatically for DiskStorage caches. For in-memory caches, this must be done
     * explicitly.
     *
     * @return number of elements in the cache
     */
    public int getAnzahlElementeImCache() {
        // Workaround: Clean cache, because in-memory caches are not cleaned.
        this.authentifizierungenCache.evictExpiredElements();

        return this.authentifizierungenCache.getSize();
    }

    /**
     * Clears the permissions cache.
     */
    public void leereCache() {
        this.authentifizierungenCache.removeAll();
    }

    /**
     * Retrieves an item from the cache.
     *
     * @param key
     *         Cache key
     * @return the session data of the Access Manager if there is an entry in the cache for the key, otherwise {@code
     * null}
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
     * Puts a new item in the cache.
     *
     * @param key
     *         the cache key
     * @param authentifzierungErgebnis
     *         the value that will be stored in the cache
     */
    public void putIntoCache(Object key, E authentifzierungErgebnis) {
        if (this.cacheAktiviert) {
            this.authentifizierungenCache.put(new Element(key, authentifzierungErgebnis));
        }
    }

    /**
     * Creates a new cache manager with the specified configuration.
     *
     * @param cacheKonfiguration
     *         the cache configuration
     * @return the created cache manager
     */
    private CacheManager erzeugeCacheManager(String cacheKonfiguration) {
        // Create a unique name for the cache manager.
        String name = CACHE_MANAGER_NAME;
        int zaehler = 0;
        while (CacheManager.getCacheManager(name) != null) {
            zaehler++;
            name = CACHE_MANAGER_NAME + "-" + zaehler;
        }

        // create a new cache manager.
        ConfigurationSource configSource =
            ConfigurationSource.getConfigurationSource(getClass().getResource(cacheKonfiguration));
        Configuration config = configSource.createConfiguration();
        config.setName(name);
        CacheManager cacheManager = new CacheManager(config);

        return cacheManager;
    }

    /**
     * Terminates the cache manager and initializes a new cache manager with the passed cache configuration.
     *
     * @param cacheKonfiguration
     *         the cache configuration to set
     */
    public synchronized void setCacheKonfiguration(String cacheKonfiguration) {
            LOG.debug("Setzen der Cache Konfiguration.");

            // Shuts down Cache Manager.
            this.cacheManager.shutdown();
            LOG.debug("Cache-Manager herunterfahren.");

            // Creates a new cache manager.
            this.cacheManager = erzeugeCacheManager(cacheKonfiguration);
            LOG.debug("Neuer Cache-Manager angelegt.");

            // Explicitly save mapping cache
            this.authentifizierungenCache = this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
            // If cache does not exist (it has not been configured explicitly): create
            if (this.authentifizierungenCache == null) {
                this.cacheManager.addCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
                this.authentifizierungenCache = this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME);
            }
            LOG.debug("Anwenderdaten-Cache angelegt.");
    }

    /**
     * Sets the time an object remains valid within the user data cache.
     * <p>
     * After this time the object becomes invalid and must be reloaded from the data source.
     * <p>
     * The time is only observed if the type of the cache is <b><u>not eternal</u></b>.
     *
     * @param timeToLiveSeconds
     *         the time span in seconds for which objects should remain valid
     */
    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME).getCacheConfiguration()
            .setTimeToLiveSeconds(timeToLiveSeconds);
    }

    /**
     * Sets the maximum number of elements cached in memory.
     *
     * @param maxEntriesLocalHeap
     *         the maximum number of elements cached in memory
     */
    public void setMaxEntriesLocalHeap(int maxEntriesLocalHeap) {
        this.cacheManager.getCache(AUTHENTIFIZIERUNGEN_CACHE_NAME).getCacheConfiguration()
            .setMaxEntriesLocalHeap(maxEntriesLocalHeap);
    }

    public boolean isCacheAktiviert() {
        return this.cacheAktiviert;
    }

    public void setCacheAktiviert(boolean cacheAktiviert) {
        this.cacheAktiviert = cacheAktiviert;
    }

    /**
     * Terminates the CacheManager.
     * <p>
     * This method should only be called in the {@code destroy()} method of the {@code DisposableBean}, which holds the
     * reference to the {@link CacheManager}.
     */
    public void shutdownManager() {
        this.cacheManager.shutdown();
        LOG.debug("Cache-Manager herunterfahren.");
    }
}
