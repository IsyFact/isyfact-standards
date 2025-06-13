package de.bund.bva.isyfact.konfiguration.common.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.bund.bva.isyfact.konfiguration.common.Konfiguration;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

/**
 * Diese Klasse kapselt einen typsicheren Zugriff auf ein Properties-Objekt.
 * <p>
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
public class PropertyKonfiguration extends AbstractKonfiguration implements Konfiguration {

    /**
     * Schema, dem die Dateinamen entsprechen müssen.
     */
    private String namensSchema;

    /**
     * Gekapselte Properties.
     */
    private Properties properties;

    /**
     * Logger der Klasse.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(PropertyKonfiguration.class);

    /**
     * Erzeuge neue Instanz für angegebene Properties.
     * @param properties
     *            Die zu kapselnden Properties.
     */
    public PropertyKonfiguration(Properties properties) {
        this(properties, RessourcenHelper.DEFAULTNAMENSSCHEMA);
    }

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
        this(propertyLocation, RessourcenHelper.DEFAULTNAMENSSCHEMA);
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
            this.namensSchema = namensSchema;
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
        this(propertyLocations, RessourcenHelper.DEFAULTNAMENSSCHEMA);
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
     * @param namensSchema
     *            das Schema, dem die Dateinamen entsprechen müssen.
     */
    public PropertyKonfiguration(List<String> propertyLocations, String namensSchema) {
        this.namensSchema = namensSchema;
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
                    List<String> propertyDateien =
                        RessourcenHelper.ladePropertiesAusOrdner(propertyLocation, this.namensSchema);
                    for (String propertyDatei : propertyDateien) {
                        gesamtProperties.putAll(ladeProperties(propertyDatei));
                    }
                } else {
                    throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_PROPERTY_ORDNER_PFAD,
                        propertyLocation);
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
            List<String> propertyDateien =
                RessourcenHelper.ladePropertiesAusOrdner(propertyLocation, this.namensSchema);

            for (String propertyDatei : propertyDateien) {
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
        Properties loadedProperties = new Properties();
        try {
            loadedProperties.load(propertyStream);
        } catch (IOException e) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, propertyLocation);
        }
        return loadedProperties;
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
