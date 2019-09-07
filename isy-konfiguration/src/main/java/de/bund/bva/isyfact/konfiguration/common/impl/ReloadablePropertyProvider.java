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
package de.bund.bva.isyfact.konfiguration.common.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.konfiguration.common.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

/**
 * Implementiert das Laden und Mergen von Property-Dateien. Über die Methode {@link #checkAndUpdate()} werden
 * geänderte Property-Dateien bei Bedarf neu eingelesen.
 *
 *
 */
public class ReloadablePropertyProvider {

    private final String[] locationPatterns;

    /**
     * Logger der Klasse.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ReloadablePropertyProvider.class);

    /**
     * Liste der von dieser Klasse verwalteten Property-Dateien.
     */
    private Map<Resource, PropertyResource> propertyResources = new HashMap<>();

    /**
     * Aus {@link #propertyResources} resultierende Properties.
     */
    private Properties properties;

    private final PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();

    /**
     * Siehe {@link PathMatchingResourcePatternResolver} für Format der <code>locationPatterns</code>
     *
     * @param locationPatterns
     *            Spring Location-Patterns, aus denen Properties gelesen werden sollen.
     */
    public ReloadablePropertyProvider(String... locationPatterns) {
        this.locationPatterns = locationPatterns;
        checkAndUpdate();
    }

    /**
     * @see PathMatchingResourcePatternResolver
     */
    private boolean loadPropertyResources() {
        try {
            boolean createdNewPropertyFile = false;
            Map<Resource, PropertyResource> neuePropertyResources = new HashMap<>();
            for (String locationPattern : this.locationPatterns) {
                for (Resource resource : this.pmrpr.getResources(locationPattern)) {
                    PropertyResource propertyFile = this.propertyResources.get(resource);
                    if (null == propertyFile) {
                        propertyFile = new PropertyResource(resource);
                        createdNewPropertyFile = true;
                    }
                    neuePropertyResources.put(resource, propertyFile);
                }
            }

            // Wenn eine neue PropertyResource angelegt wurde oder es weniger sind als zuvor, so hat sich was
            // geändert.
            boolean somethingChanged =
                createdNewPropertyFile || neuePropertyResources.size() < this.propertyResources.size();

            this.propertyResources = neuePropertyResources;

            return somethingChanged;
        } catch (IOException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, e);
        }
    }

    /**
     * Fasst alle Properties zu einem Properties-Objekt zusammen.
     * @return Die gemergten Properties.
     */
    private Properties mergeProperties() {
        Properties gesamtProperties = new Properties();

        for (PropertyResource propertyResource : this.propertyResources.values()) {
            gesamtProperties.putAll(propertyResource.getProperties());
        }
        return gesamtProperties;
    }

    /**
     * Liefert das zusammgenfaßte Properties-Objekt.
     * @return Das Properties-Objekt.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Prüft ob eine der Property-Resources modifiziert wurde und lädt dann die Properties neu.
     * @return <code>true</code> falls eine der Properties-Resources geändert wurde.
     */
    public synchronized boolean checkAndUpdate() {
        boolean propertyResourceMengeGeaendert = loadPropertyResources();
        boolean propertyResourceInhaltGeaendert = false;

        for (PropertyResource propertyResource : this.propertyResources.values()) {
            if (propertyResource.isNeueVersionVerfuegbar()) {
                LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.KONFIGURATION_DATEI_NEU_GELADEN,
                    "Die Konfigurationsdatei {} wird neu geladen.", propertyResource.getDescription());
                propertyResourceInhaltGeaendert = true;
                propertyResource.reload();
            }
        }
        if (propertyResourceMengeGeaendert || propertyResourceInhaltGeaendert) {
            this.properties = mergeProperties();
            return true;
        }
        return false;
    }
}
