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
package de.bund.bva.isyfact.batchrahmen.batch.konfiguration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenKonfigurationException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;

/**
 * Die Konfiguration fuer einen Batchrahmen-Aufruf. Welche Parameter hier explizit interpretiert werden, wird
 * im Klassenkommentar {@link BatchLauncher} definiert.
 *
 *
 */
public class BatchKonfiguration {

    /** Property-Namenprefix fuer die Spring-Dateien der Anwendung. */
    private static final String PROPERTY_ANWENDUNG_SPRING_DATEIEN = "Anwendung.SpringDateien.";

    /** Property-Namenprefix fuer die Spring-Dateien des Rahmens. */
    private static final String PROPERTY_BATCHRAHMEN_SPRING_DATEIEN = "Batchrahmen.SpringDateien.";

    /** Property-Name für die Property (Schlüssel), welche die aktiven Spring-Profile beinhaltet. */
    private static final String PROPERTY_SPRINGPROFILES_PROPERTIES = "SpringProfiles";

    /** Inhalt der Property-Datei. */
    private Properties properties;

    /**
     * Konfiguriert die Konfiguration mit der angegebenen Kommandozeile.
     *
     * @param kommandoZeile
     *            die Kommandozeile.
     */
    public BatchKonfiguration(String[] kommandoZeile) {
        KommandozeilenParser parser = new KommandozeilenParser();
        Map<String, String> kommandoZeilenParameter = parser.parse(kommandoZeile);
        String propertyDatei = kommandoZeilenParameter.get(KonfigurationSchluessel.KOMMANDO_PARAM_PROP_DATEI);
        if (propertyDatei == null) {
            throw new BatchrahmenParameterException(
                NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_KEINE_CONFIG);
        }
        this.properties = ladePropertyDatei(propertyDatei);
        this.properties.putAll(kommandoZeilenParameter);
    }

    /**
     * liest die Dateinamen fuer die Spring-Konfiguration des Batchrahmens. Diese Dateinamen haben den
     * Property-Schlüssel <tt>Batchrahmen.SpringDateien.&lt;N&gt;</tt>.
     *
     * @return Liste der Dateinamen.
     */
    public List<String> getBatchRahmenSpringKonfigFiles() {
        return getNumberedPropertyValues(PROPERTY_BATCHRAHMEN_SPRING_DATEIEN);
    }

    /**
     * liest die Dateinamen fuer die Spring-Konfiguration der Anwendung. Diese Dateinamen haben den
     * Property-Schlüssel <tt>Anwendung.SpringDateien.&lt;N&gt;</tt>.
     * @return Liste der Dateinamen.
     */
    public List<String> getAnwendungSpringKonfigFiles() {
        return getNumberedPropertyValues(PROPERTY_ANWENDUNG_SPRING_DATEIEN);
    }

    /**
     * Liest die konfigurierten Spring-Profile.
     *
     * @return Liste der aktiven Spring-Profile.
     */
    public String[] getSpringProfiles() {
        String profiles = properties.getProperty(PROPERTY_SPRINGPROFILES_PROPERTIES);
        if (profiles != null) {
            return properties.getProperty(PROPERTY_SPRINGPROFILES_PROPERTIES).trim().split(",");
        } else {
            return new String[] {};
        }
    }

    /**
     * liest eine Liste von Property-Werten mit dem gegebenen Prefix. An den Prefix werden Zahlen angehängt,
     * von 1 an aufwärts. Bis zur ersten fehlenden Zahl im Schlüssel werden alle Werte zurückgegeben.
     *
     * @param prefix
     *            der Prefix zum Auslesen.
     * @return die Liste der Werte.
     */
    private List<String> getNumberedPropertyValues(String prefix) {
        List<String> values = new ArrayList<>();
        int i = 1;
        while (this.properties.containsKey(prefix + i)) {
            values.add(this.properties.getProperty(prefix + i));
            i++;
        }
        return values;
    }

    /**
     * Liefert den angegebenen Konfigurationsparameter als long zurück.
     *
     * @param name
     *            Der Name des Konfigurationsparameters, wie er in den Properties enthalten ist.
     * @throws BatchrahmenKonfigurationException
     *             Wenn der Konfigurationsparameter nicht gesetzt oder in ein Integer konvertiert werden kann.
     * @return Den Konfigurationsparameter als int.
     */
    public long getAsLong(String name) {
        if (!this.properties.containsKey(name)) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT, name);
        }
        String propValue = this.properties.getProperty(name);
        try {
            return Long.parseLong(propValue);
        } catch (NumberFormatException ex) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_PARAMETER_UNGUELTIG,
                propValue, name);
        }
    }

    /**
     * Liefert den angegebenen Konfigurationsparameter als int zurück. Falls der Parameter nicht gesetzt ist,
     * wird der Default-Wert zurückgegeben.
     *
     * @param name
     *            Der Name des Konfigurationsparameters, wie er in den Properties enthalten ist.
     * @param standardWert
     *            Der Standardwert, welcher übernommen wird, falls der Wert nicht in den Properties enthalten
     *            ist.
     * @throws BatchrahmenKonfigurationException
     *             Wenn der Konfigurationsparameter nicht in ein Integer konvertiert werden kann.
     * @return Den Konfigurationsparameter als int.
     */
    public long getAsLong(String name, long standardWert) {
        if (!this.properties.containsKey(name)) {
            return standardWert;
        }
        return getAsLong(name);
    }

    /**
     * Liefert den angegebenen Konfigurationsparameter als String zurück.
     *
     * @param name
     *            Der Name des Konfigurationsparameters, wie er in den Properties enthalten ist.
     * @throws BatchrahmenKonfigurationException
     *             Wenn der Konfigurationsparameter nicht gesetzt ist.
     * @return Den Konfigurationsparameter als String.
     */
    public String getAsString(String name) {
        if (!this.properties.containsKey(name)) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT, name);
        }
        return this.properties.getProperty(name);
    }

    /**
     * Liefert den angegebenen Konfigurationsparameter als String zurück. Falls der Parameter nicht gesetzt
     * ist, wird der Default-Wert zurückgegeben.
     *
     * @param name
     *            Der Name des Konfigurationsparameters, wie er in den Properties enthalten ist.
     * @param standardWert
     *            Der Standardwert, welcher übernommen wird, falls der Wert nicht in den Properties enthalten
     *            ist.
     * @throws BatchrahmenKonfigurationException
     *             Wenn der Konfigurationsparameter nicht in ein String konvertiert werden kann.
     * @return Den Konfigurationsparameter als String.
     */
    public String getAsString(String name, String standardWert) {
        if (!this.properties.containsKey(name)) {
            return standardWert;
        }
        return getAsString(name);
    }

    /**
     * liest die in Argument propertyDateiname der Kommandozeilen-Argumente angegebene Property Datei.
     *
     * @param propertyDateiname
     *            name der Propertydatei
     * @throws BatchrahmenKonfigurationException
     *             falls die Datei nicht gelesen werden kann.
     * @return die geladenen Properties.
     */

    private Properties ladePropertyDatei(String propertyDateiname) {
        try {
            URL url = BatchKonfiguration.class.getResource(propertyDateiname);
            this.properties = new Properties();
            this.properties.load(url.openStream());
            return this.properties;
        } catch (Throwable ex) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_DATEI_LESEN,
                propertyDateiname, ex.getMessage());
        }
    }

    /**
     * Liefert den angegebenen Konfigurationsparameter als Boolean zurück. Falls der Parameter nicht gesetzt
     * ist, wird der Default-Wert zurückgegeben.
     *
     * @param name
     *            Der Name des Konfigurationsparameters, wie er in den Properties enthalten ist.
     * @param standardWert
     *            Der Standardwert, welcher übernommen wird, falls der Wert nicht in den Properties enthalten
     *            ist.
     * @throws BatchrahmenKonfigurationException
     *             Wenn der Konfigurationsparameter nicht in ein Boolean konvertiert werden kann.
     * @return Den Konfigurationsparameter als Boolean.
     */
    public boolean getAsBoolean(String name, boolean standardWert) {
        if (!this.properties.containsKey(name)) {
            return standardWert;
        }
        return getAsBoolean(name);
    }

    /**
     * Liefert den angegebenen Konfigurationsparameter als int zurück.
     *
     * @param name
     *            Der Name des Konfigurationsparameters, wie er in den Properties enthalten ist.
     * @throws BatchrahmenKonfigurationException
     *             Wenn der Konfigurationsparameter nicht gesetzt oder in ein Boolean konvertiert werden kann.
     * @return Den Konfigurationsparameter als boolean.
     */
    public boolean getAsBoolean(String name) {
        if (!this.properties.containsKey(name)) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT, name);
        }
        String propValue = this.properties.getProperty(name);
        return Boolean.parseBoolean(propValue);
    }

    /**
     * Gibt den konfigurierten Start-Typ aus.
     *
     * @return der Start-Typ.
     * @throws BatchrahmenKonfigurationException
     *             falls beide oder kein Starttyp angegeben werden.
     */
    public BatchStartTyp getStartTyp() {
        boolean start = getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_START, false);
        boolean restart = getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART, false);

        if (start && restart) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_KONFLIKT,
                KonfigurationSchluessel.KOMMANDO_PARAM_START,
                KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART);
        }
        if (!start && !restart) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_UNGUELTIG,
                KonfigurationSchluessel.KOMMANDO_PARAM_START,
                KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART);
        }
        if (start) {
            return BatchStartTyp.START;
        } else {
            return BatchStartTyp.RESTART;
        }
    }

    /**
     * Liefert das Propertiesobjekt mit allen Parametern.
     * @return Das Propertiesobjekt mit den Parametern.
     */
    public Properties getProperties() {
        return this.properties;
    }

}
