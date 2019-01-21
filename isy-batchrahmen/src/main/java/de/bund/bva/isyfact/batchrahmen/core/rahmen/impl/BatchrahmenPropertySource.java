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
package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import de.bund.bva.isyfact.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.PropertySource;

/**
 * Die Klasse liest aus Property-Dateien und entsprechenden Schlüsseln die eingetragenen Spring-Profile aus.
 */
public class BatchrahmenPropertySource extends PropertySource<Properties> {

    /** Der Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(BatchrahmenPropertySource.class);

    /** Kommaseparierte Liste von aktiven Spring-Profilen. */
    private String springProfiles;

    /**
     * Erzeugt eine neue PropertySource für den Batchrahmen.
     *
     * @param propertyDateien Liste von Property-Dateien, welche Spring-Profil-Definitionen enthalten.
     * @param propertyKeys    Liste von Schlüsseln, welche in den Property-Dateien die Spring-Profile definieren.
     */
    public BatchrahmenPropertySource(String[] propertyDateien, String[] propertyKeys) {
        super("BatchrahmenPropertySource");
        if (propertyDateien == null || propertyDateien.length == 0 || propertyKeys == null
            || propertyKeys.length == 0) {
            return;
        }
        Properties properties = readProperties(Arrays.asList(propertyDateien));

        springProfiles = Arrays.stream(propertyKeys).map(properties::getProperty)
            .collect(Collectors.joining(","));

        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
            "Active spring profiles: {}", this.springProfiles);
    }

    private Properties readProperties(List<String> propertyDateien) {
        return propertyDateien.stream().map(this::loadProperty)
            .reduce(new Properties(), (properties, properties2) -> {
                properties.putAll(properties2);
                return properties;
            });
    }

    private Properties loadProperty(String path) {
        Properties p = new Properties();
        try {
            URL url = Thread.currentThread().getClass().getResource(path);
            URI uri = url.toURI();
            p.load(new FileInputStream(new File(uri)));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        return p;
    }

    @Override
    public Object getProperty(String name) {
        if (AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME.equals(name)) {
            return this.springProfiles;
        }
        return null;
    }
}
