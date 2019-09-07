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
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.konstanten.NachrichtenSchluessel;

/**
 * Diese Klasse kapselt einen typsicheren Zugriff auf ein Properties-Objekt.
 *
 */
public class PropertyKonfiguration extends AbstractKonfiguration {

    /**
     * Gekapselte Properties.
     */
    private Properties properties;

    private final PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();

    /**
     * Erzeuge neue Instanz für angegebene Properties.
     * @param properties
     *            Die zu kapselnden Properties.
     */
    public PropertyKonfiguration(Properties properties) {
        this.properties = properties;
    }

    /**
     * Erzeuge eine PropertyKonfiguration aus den gesammelten Properties aus mehreren Spring
     * Location-Patterns.
     *
     * Es wird {@link PathMatchingResourcePatternResolver} verwendet, um die <code>locationPatterns</code>
     * aufzulösen. Dies erlaubt Präfixe wie <code>file:</code> und <code>classpath:</code>, sowie Ant-Globs
     * wie <code>config/*.properties</code>. Siehe {@link PathMatchingResourcePatternResolver} für eine genaue
     * Beschreibung.
     *
     * @see PathMatchingResourcePatternResolver
     * @see DefaultResourceLoader
     */
    public PropertyKonfiguration(String... locationPatterns) {
        this.properties = ladeMergedProperties(locationPatterns);
    }

    private Properties ladeMergedProperties(String... locationPatterns) {
        Properties gesamtProperties = new Properties();
        for (String propertyLocation : locationPatterns) {
            try {
                for (Resource resource : this.pmrpr.getResources(propertyLocation)) {
                    gesamtProperties.putAll(ladeProperties(resource));
                }
            } catch (IOException e) {
                throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_PARAMETERWERT_UNGUELTIG,
                    propertyLocation);
            }
        }
        return gesamtProperties;
    }

    private Properties ladeProperties(Resource propertyResource) {
        try (InputStream is = propertyResource.getInputStream();) {
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN,
                propertyResource.getDescription());
        }
    }

    /**
     * Liefert das gekapselte Properties-Objekt.
     * @return Die gekapselte Properties.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Set<String> getSchluessel() {
        return (Set) this.properties.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean containsKey(String schluessel) {
        return this.properties.containsKey(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValue(String schluessel) {
        return this.properties.getProperty(schluessel);
    }
}
