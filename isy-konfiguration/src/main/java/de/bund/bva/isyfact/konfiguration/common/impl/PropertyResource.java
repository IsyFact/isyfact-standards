/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file ecept in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either epress or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.konfiguration.common.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.Resource;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.konstanten.NachrichtenSchluessel;

/**
 * Hilfsklasse für {@link ReloadablePropertyProvider}. Diese Klasse repräsentiert eine Properties-Datei.
 *
 */
class PropertyResource {
    /**
     * Aus Ressource geladene Properties.
     */
    private Properties properties;

    /**
     * ModificationTime der Datei, beim letzten Laden der Properties.
     */
    private long lastModified;

    private final Resource resource;

    PropertyResource(Resource resource) {
        this.resource = resource;
        reload();
    }

    /**
     * Getter für Properties.
     * @return Properties.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Liefert <code>true</code> wenn der ModificationTime der Datei neuer ist, als zu dem Zeitpunkt, als die
     * Datei gelesen wurde.
     * @return <code>true</code> Wenn eine Dateiversion verfügbar ist.
     */
    public boolean isNeueVersionVerfuegbar() {
        try {
            return this.resource.lastModified() != this.lastModified;
        } catch (IOException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, e,
                this.resource.toString());
        }
    }

    private Properties loadProperties() {
        try (InputStream is = this.resource.getInputStream()) {
            Properties resultProperties = new Properties();
            resultProperties.load(is);
            return resultProperties;
        } catch (FileNotFoundException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_NICHT_GEFUNDEN, e,
                this.resource.toString());
        } catch (IOException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, e,
                this.resource.toString());
        } catch (IllegalArgumentException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_FORMAT, e,
                this.resource.toString());
        }
    }

    /**
     * Lädt die Properties aus der Datei neu. Wenn dabei ein Fehler auftritt wird das gekapselte
     * Properties-Objekt nicht ersetzt und behält die vorherigen Einträge.
     * @throws KonfigurationDateiException
     *             Wenn die Datei nicht geladen werden konnte.
     */
    public void reload() {
        try {
            this.lastModified = this.resource.lastModified();
        } catch (IOException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, e,
                this.resource.toString());
        }
        this.properties = loadProperties();
    }

    /**
     * Liefert den Dateinamen.
     * @return Den Namen der Property-Datei.
     */
    public String getDescription() {
        return this.resource.getDescription();
    }

    /**
     * Prüft ob die gewrappte Resource existiert.
     * @return true, wenn die PropertyDatei existiert.
     * @see Resource#exists()
     */
    public boolean exists() {
        return this.resource.exists();
    }
}
