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
package de.bund.bva.pliscommon.konfiguration.common.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;

/**
 * Diese Klasse kapselt einen typsicheren Zugriff auf ein Properties-Objekt.
 *
 *
 */
public class PropertyKonfiguration extends AbstractKonfiguration implements Konfiguration {

    private String namensSchema;

    /**
     * Gekapselte Properties.
     */
    private Properties properties;

    /**
     * Sortiert und Filtert die Liste an Dateinamen.
     */
    private DateinamenSortierer dateinamenSortiererUndFilterer;

    /**
     * Logger der Klasse.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(PropertyKonfiguration.class);

    /**
     * Erzeuge neue Instanz für angegebene Properties.
     * @param properties
     *            Die zu kapselnden Properties.
     * @param namensSchema
     *            das Schema, dem die Dateinamen entsprechen müssen.
     */

    public PropertyKonfiguration(Properties properties, String namensSchema) {

        this.namensSchema = namensSchema;
        this.properties = properties;
        this.dateinamenSortiererUndFilterer = new DateinamenSortierer();
    }

    /**
     * Erzeuge neue Instanz für angegebene Properties. Die angegebenen Property-Dateien werden relativ zum
     * Klassenpfad per {@link ClassLoader#getResourceAsStream(String)} geladen, wobei der {@link ClassLoader}
     * über die Klasse von {@link PropertyKonfiguration} anhand {@link Class#getClassLoader()} ermittelt wird.
     *
     * Alle angegebenen Property-Dateien werden zu einer gemeinsamen Konfiguration vereinigt.
     *
     * @param propertyLocation
     *            der Pfad zum Ordner der Properties-Dateien relativ zum Classpath. Im Gegensatz zu
     *            {@link ReloadablePropertyKonfiguration#ReloadablePropertyKonfiguration(String[])} dürfen die
     *            Pfade nicht mit "/" anfangen. Ein gültiger Pfad ist z.B. "config/". Der Pfad muss mit einem
     *            "/" enden.
     */
    public PropertyKonfiguration(String propertyLocation) {
        if (propertyLocation.endsWith("/")) {
            this.dateinamenSortiererUndFilterer = new DateinamenSortierer();
            this.properties = ladeMergedProperties(propertyLocation);
        } else {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_PROPERTY_ORDNER_PFAD,
                propertyLocation);
        }
    }

    /**
     * Erzeuge neue Instanz für angegebene Properties. Die angegebenen Property-Dateien werden relativ zum
     * Klassenpfad per {@link ClassLoader#getResourceAsStream(String)} geladen, wobei der {@link ClassLoader}
     * über die Klasse von {@link PropertyKonfiguration} anhand {@link Class#getClassLoader()} ermittelt wird.
     *
     * Alle angegebenen Property-Dateien werden zu einer gemeinsamen Konfiguration vereinigt.
     *
     * @param propertyLocation
     *            der Pfad zum Ordner der Properties-Dateien relativ zum Classpath. Im Gegensatz zu
     *            {@link ReloadablePropertyKonfiguration#ReloadablePropertyKonfiguration(String[])} dürfen die
     *            Pfade nicht mit "/" anfangen. Ein gültiger Pfad ist z.B. "config/". Der Pfad muss mit einem
     *            "/" enden.
     * @param namensSchema
     *            das Schema, dem die Dateinamen entsprechen müssen.
     */
    public PropertyKonfiguration(String propertyLocation, String namensSchema) {
        if (propertyLocation.endsWith("/")) {
            this.dateinamenSortiererUndFilterer = new DateinamenSortierer(namensSchema);
            this.properties = ladeMergedProperties(propertyLocation);
        } else {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_PROPERTY_ORDNER_PFAD,
                propertyLocation);
        }
    }

    /**
     * Erzeuge neue Instanz für angegebene Properties. Die angegebenen Property-Dateien werden relativ zum
     * Klassenpfad per {@link ClassLoader#getResourceAsStream(String)} geladen, wobei der {@link ClassLoader}
     * über die Klasse von {@link PropertyKonfiguration} anhand {@link Class#getClassLoader()} ermittelt wird.
     *
     * Alle angegebenen Property-Datei werden zu einer gemeinsamen Konfiguration vereinigt.
     *
     * @param propertyLocations
     *            die Pfade zu der Properties-Dateien relativ zum Classpath. Im Gegensatz zu
     *            {@link ReloadablePropertyKonfiguration#ReloadablePropertyKonfiguration(String[])} dürfen die
     *            Pfade nicht mit "/" anfangen. Ein gültiger Pfad ist z.B. "config/test.properties"
     */
    public PropertyKonfiguration(List<String> propertyLocations) {
        this.properties = ladeMergedProperties(propertyLocations);
    }

    /**
     * Lädt alle Properties von dem {@link ClassLoader} und fasst sie zu einem Properties-Objekt zusammen.
     *
     * @return die gemergten Properties.
     */
    private Properties ladeMergedProperties(List<String> propertyLocations) {
        Properties gesamtProperties = new Properties();
        for (String propertyLocation : propertyLocations) {
            if (RessourcenHelper.istOrdner(propertyLocation)) {
                if (propertyLocation.endsWith("/")) {
                    Set<String> propertyDateien =
                        RessourcenHelper.ladePropertiesAusOrdner(propertyLocation, this.namensSchema);
                    for (String propertyDatei : propertyDateien) {
                        gesamtProperties.putAll(ladeProperties(propertyDatei));
                    }
                } else {
                    LOG.info(LogKategorie.JOURNAL, NachrichtenSchluessel.ERR_PROPERTY_ORDNER_PFAD,
                        "Der Pfad des Property-Ordners \"{}\" muss mit einem \"/\" enden.", propertyLocation);
                }
            } else {
                gesamtProperties.putAll(ladeProperties(propertyLocation));
            }
        }
        return gesamtProperties;
    }

    /**
     * Lädt alle Properties von dem {@link ClassLoader} und fasst sie zu einem Properties-Objekt zusammen.
     *
     * @param propertyLocation
     *            der Pfad zum Ordner der Properties-Dateien relativ zum Classpath. Im Gegensatz zu
     *            {@link ReloadablePropertyKonfiguration#ReloadablePropertyKonfiguration(String[])} dürfen die
     *            Pfade nicht mit "/" anfangen. Ein gültiger Pfad ist z.B. "config/". Der Pfad muss mit einem
     *            "/" enden.
     * @return die gemergten Properties.
     */
    private Properties ladeMergedProperties(String propertyLocation) {
        Properties gesamtProperties = new Properties();
        if (RessourcenHelper.istOrdner(propertyLocation)) {
            Set<String> propertyDateien =
                RessourcenHelper.ladePropertiesAusOrdner(propertyLocation, this.namensSchema);

            List<String> propertyDateienList =
                this.dateinamenSortiererUndFilterer.sortiereUndFiltereDateinamenAusStringSet(propertyDateien);

            for (String propertyDatei : propertyDateienList) {
                gesamtProperties.putAll(ladeProperties(propertyDatei));
            }
        }
        return gesamtProperties;
    }

    /**
     * Lädt ein {@link Properties}-Objekt von dem {@link ClassLoader}.
     *
     * @return das geladene {@link Properties}-Objekt
     */
    private Properties ladeProperties(String propertyLocation) {
        InputStream propertyStream = PropertyKonfiguration.class.getResourceAsStream(propertyLocation);
        if (propertyStream == null) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_NICHT_GEFUNDEN,
                propertyLocation);
        }
        Properties properties = new Properties();
        try {
            properties.load(propertyStream);
        } catch (IOException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, propertyLocation);
        }
        return properties;
    }

    /**
     * Liefert das gekapselte Properties-Objekt.
     * @return Die gekapsleten Proeprties.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
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
