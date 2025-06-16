package de.bund.bva.isyfact.batchrahmen.batch.konfiguration;

import java.io.InputStream;
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
 * The configuration for a {@link de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen}-call.
 * Which parameters are explicitly interpreted here is defined in the class comment {@link BatchLauncher}.
 */
public class BatchKonfiguration {

    /**
     * Property name prefix for the application's Spring files.
     */
    private static final String PROPERTY_ANWENDUNG_SPRING_DATEIEN = "Anwendung.SpringDateien.";

    /**
     * Property name prefix for the Spring files of the Rahmen.
     */
    private static final String PROPERTY_BATCHRAHMEN_SPRING_DATEIEN = "Batchrahmen.SpringDateien.";

    /**
     * Property name for the property (key) that contains the active Spring profiles.
     */
    private static final String PROPERTY_SPRINGPROFILES_PROPERTIES = "SpringProfiles";

    /**
     * Contents of the property file.
     */
    private Properties properties;

    /**
     * Configures the configuration with the specified command line.
     *
     * @param kommandoZeile the command line.
     */
    public BatchKonfiguration(String[] kommandoZeile) {
        KommandozeilenParser parser = new KommandozeilenParser();
        Map<String, String> kommandoZeilenParameter = parser.parse(kommandoZeile);
        String propertyDatei = kommandoZeilenParameter.get(KonfigurationSchluessel.KOMMANDO_PARAM_PROP_DATEI);
        if (propertyDatei == null || propertyDatei.isEmpty()) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_KEINE_CONFIG);
        }
        this.properties = ladePropertyDatei(propertyDatei);
        this.properties.putAll(kommandoZeilenParameter);
    }

    /**
     * Reads the file names for the Spring configuration of the Batchrahmen.
     * These filenames have the property key <tt>Batchrahmen.SpringDateien.&lt;N&gt;</tt>.
     *
     * @return list of filenames.
     */
    public List<String> getBatchRahmenSpringKonfigFiles() {
        return getNumberedPropertyValues(PROPERTY_BATCHRAHMEN_SPRING_DATEIEN);
    }

    /**
     * Reads the file names for the Spring configuration of the application.
     * These filenames have the property key <tt>Anwendung.SpringDateien.&lt;N&gt;</tt>.
     *
     * @return list of filenames.
     */
    public List<String> getAnwendungSpringKonfigFiles() {
        return getNumberedPropertyValues(PROPERTY_ANWENDUNG_SPRING_DATEIEN);
    }

    /**
     * Reads the configured Spring profiles.
     *
     * @return Array of active Spring profiles.
     */
    public String[] getSpringProfiles() {
        String profiles = properties.getProperty(PROPERTY_SPRINGPROFILES_PROPERTIES);
        if (profiles != null) {
            return profiles.trim().split(",");
        } else {
            return new String[0];
        }
    }

    /**
     * Reads a list of property values with the given prefix.
     * Numbers are appended to the prefix, from 1 upwards.
     * All values are returned up to the first missing number in the key.
     *
     * @param prefix the prefix to read.
     * @return the list of values.
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
     * Returns the specified configuration parameter as long.
     *
     * @param name The name of the configuration parameter as contained in the properties.
     * @return The configuration parameter as long.
     * @throws BatchrahmenKonfigurationException If the configuration parameter cannot be set or converted to a long.
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
     * Returns the specified configuration parameter as long.
     * If the parameter is not set, the default value is returned.
     *
     * @param name         The name of the configuration parameter as it is contained in the properties.
     * @param standardWert The default value which will be used if the value is not contained in the properties.
     * @return The configuration parameter as long.
     * @throws BatchrahmenKonfigurationException If the configuration parameter cannot be converted to a long.
     */
    public long getAsLong(String name, long standardWert) {
        if (!this.properties.containsKey(name)) {
            return standardWert;
        }
        return getAsLong(name);
    }

    /**
     * Returns the specified configuration parameter as a string.
     *
     * @param name The name of the configuration parameter as contained in the properties.
     * @return The configuration parameter as a string.
     * @throws BatchrahmenKonfigurationException If the configuration parameter is not set.
     */
    public String getAsString(String name) {
        if (!this.properties.containsKey(name)) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT, name);
        }
        return this.properties.getProperty(name);
    }

    /**
     * Returns the specified configuration parameter as a string.
     * If the parameter is not set, the default value is returned.
     *
     * @param name         The name of the configuration parameter as it is contained in the properties.
     * @param standardWert The default value which will be taken if the value is not contained in the properties.
     * @return The configuration parameter as a string.
     */
    public String getAsString(String name, String standardWert) {
        if (!this.properties.containsKey(name)) {
            return standardWert;
        }
        return getAsString(name);
    }

    /**
     * Reads the property file specified in argument propertyDateiname of the command line arguments.
     *
     * @param propertyDateiname name of the property file
     * @return the loaded properties.
     * @throws BatchrahmenKonfigurationException if the file cannot be read.
     */

    private Properties ladePropertyDatei(String propertyDateiname) {
        this.properties = new Properties();
        try (InputStream urlStream = BatchKonfiguration.class.getResource(propertyDateiname).openStream()) {
            this.properties.load(urlStream);
        } catch (Exception ex) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_DATEI_LESEN,
                    propertyDateiname, ex.getMessage());
        }
        boolean start = getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_START, false);
        boolean restart = getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART, false);

        if (start && restart) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_KONFLIKT,
                    KonfigurationSchluessel.KOMMANDO_PARAM_START,
                    KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART);
        }
        return this.properties;
    }

    /**
     * Returns the specified configuration parameter as a boolean.
     * If the parameter is not set, the default value is returned.
     *
     * @param name         The name of the configuration parameter as it is contained in the properties.
     * @param standardWert The default value which will be taken if the value is not contained in the properties.
     * @return The configuration parameter as a boolean.
     */
    public boolean getAsBoolean(String name, boolean standardWert) {
        if (!this.properties.containsKey(name)) {
            return standardWert;
        }
        return getAsBoolean(name);
    }

    /**
     * Returns the specified configuration parameter as boolean.
     *
     * @param name The name of the configuration parameter as contained in the properties.
     * @return The configuration parameter as a boolean.
     * @throws BatchrahmenKonfigurationException If the configuration parameter is not set.
     */
    public boolean getAsBoolean(String name) {
        if (!this.properties.containsKey(name)) {
            throw new BatchrahmenKonfigurationException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT, name);
        }
        String propValue = this.properties.getProperty(name);
        return Boolean.parseBoolean(propValue);
    }

    /**
     * Outputs the configured startup type.
     *
     * @return the start type.
     * @throws BatchrahmenKonfigurationException if both or no start type are specified.
     */
    public BatchStartTyp getStartTyp() {
        boolean start = getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_START, false);
        if (start) {
            return BatchStartTyp.START;
        } else {
            return BatchStartTyp.RESTART;
        }
    }

    /**
     * Returns the property object with all parameters.
     *
     * @return The property object with the parameters.
     */
    public Properties getProperties() {
        return this.properties;
    }

}
