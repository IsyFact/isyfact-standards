package de.bund.bva.isyfact.konfiguration.common.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.konfiguration.common.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

/**
 * Implementiert das Laden und Mergen von Property-Dateien. Über die Methode {@link #checkAndUpdate()} werden
 * geänderte Property-Dateien bei Bedarf neu eingelesen.
 * <p>
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
public class ReloadablePropertyProvider {

    /**
     * Schema, dem die Dateinamen entsprechen müssen.
     */
    private String namensSchema;

    /**
     * Logger der Klasse.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ReloadablePropertyProvider.class);

    /**
     * Liste der von dieser Klasse verwalteten Property-Dateien.
     */
    private List<PropertyDatei> propertyDateien;

    /**
     * Aus {@link #propertyDateien} resultierende Properties.
     */
    private Properties properties;

    /**
     * Liste mit allen Ordnern die Property-Dateien enthalten.
     */
    private List<String> propertyOrdner;

    /**
     * Ruft ladePropertyDateien() auf.
     *
     * @param propertyDateinamen
     *            Liste mit Property-Dateinamen.
     */
    public ReloadablePropertyProvider(String[] propertyDateinamen) {
        this(propertyDateinamen, RessourcenHelper.DEFAULTNAMENSSCHEMA);
    }

    /**
     * Ruft ladePropertyDateien() auf.
     *
     * @param propertyDateinamen
     *            Liste mit Property-Dateinamen.
     * @param namensSchema
     *            dem die Dateinamen entsprechen müssen.
     */
    public ReloadablePropertyProvider(String[] propertyDateinamen, String namensSchema) {
        this.namensSchema = namensSchema;
        ladePropertyDateien(propertyDateinamen);
    }

    /**
     * Lädt die angegebenen Property-Dateien aus dem Klassenpfad (siehe {@link Class#getResource(String)}),
     * welche dem NamensSchema entsprechen. Wird statt einer konkreten Property-Datei ein ganzes Verzeichnis
     * angegeben, werden alle dort befindlichen Property-Dateien ausgelesen. Die Properties-Dateien werden zu
     * einem einzelnen Properties-Objekt zusammengefasst. Bei gleichnamigen Parametern in den Properties wird
     * die zuletzt gelesene übernommen.
     *
     * @param propertyDateinamen
     *            Liste mit Property-Dateinamen.
     *
     */
    private void ladePropertyDateien(String[] propertyDateinamen) {
        this.propertyDateien = new ArrayList<>();
        this.propertyOrdner = new ArrayList<>();
        for (String dateiname : propertyDateinamen) {
            if (RessourcenHelper.istOrdner(dateiname)) {
                if (dateiname.endsWith("/")) {
                    for (String propertyInOrdner : RessourcenHelper.ladePropertiesAusOrdner(dateiname,
                        this.namensSchema)) {
                        this.propertyDateien.add(new PropertyDatei(propertyInOrdner));
                    }
                    this.propertyOrdner.add(dateiname);
                } else {
                    throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_PROPERTY_ORDNER_PFAD,
                        dateiname);
                }
            } else {
                this.propertyDateien.add(new PropertyDatei(dateiname));
            }
        }
        this.properties = mergeProperties();
    }

    /**
     * Fasst alle Properties zu einem Properties-Objekt zusammen.
     * @return Die gemergten Properties.
     */
    private Properties mergeProperties() {
        Properties gesamtProperties = new Properties();

        for (PropertyDatei propertyDatei : this.propertyDateien) {
            gesamtProperties.putAll(propertyDatei.getProperties());
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
     * Prüft ob eine der Property-Dateien modifziert wurde und lädt dann die Properties neu.
     * @return <code>true</code> fallse eine der Properties-Dateien geändert wurde.
     */
    public synchronized boolean checkAndUpdate() {
        boolean neueVersionGeladen = false;
        boolean propertyEntfernt = entferneGeloeschtePropertyDateien();
        boolean propertyHinzugefuegt = ladeNeuePropertyDateienAusOrdnern();

        for (PropertyDatei propertyDatei : this.propertyDateien) {
            if (propertyDatei.isNeueVersionVerfuegbar()) {
                LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.KONFIGURATION_DATEI_NEU_GELADEN,
                    "Die Konfigurationsdatei {} wird neu geladen.", propertyDatei.getDateiname());
                    neueVersionGeladen = true;
                    propertyDatei.neuLaden();
            }
        }
        boolean propertiesVeraendert = neueVersionGeladen || propertyEntfernt || propertyHinzugefuegt;
        if (propertiesVeraendert) {
            this.properties = mergeProperties();
        }
        return propertiesVeraendert;
    }

    /**
     * Sucht in allen Ordnern aus {@link #propertyOrdner} nach neuen Property-Dateien und fügt diese der
     * {@link #propertyDateien} Liste hinzu.
     * @return ture wenn mindestens eine neue Property-Datei existiert.
     */
    private boolean ladeNeuePropertyDateienAusOrdnern() {
        boolean propertyHinzugefuegt = false;
        for (String ordnerPfad : this.propertyOrdner) {
            if (ordnerPfad.endsWith("/")) {
                for (String dateiname : RessourcenHelper.ladePropertiesAusOrdner(ordnerPfad,
                    this.namensSchema)) {
                    boolean propertyIstNeu = true;
                    for (PropertyDatei existierendeProperty : this.propertyDateien) {
                        if (dateiname.equals(existierendeProperty.getDateiname())) {
                            propertyIstNeu = false;
                            break;
                        }
                    }

                    if (propertyIstNeu) {
                        propertyHinzugefuegt = true;
                        this.propertyDateien.add(new PropertyDatei(dateiname));
                    }
                }
            } else {
                throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_PROPERTY_ORDNER_PFAD,
                    ordnerPfad);
            }
        }
        return propertyHinzugefuegt;
    }

    /**
     * Entfernt gelöschte Property-Dateien aus der {@link #propertyDateien} Liste die sich in den Ordern aus
     * {@link #propertyOrdner} befinden.
     * @return true wenn mindestens eine Property-Datei gelöscht wurde.
     */
    private boolean entferneGeloeschtePropertyDateien() {
        boolean propertyGeloescht = false;
        Iterator<PropertyDatei> it = this.propertyDateien.iterator();
        while (it.hasNext()) {
            PropertyDatei propertyDatei = it.next();
            for (String ordnerPfad : this.propertyOrdner) {
                if (propertyDatei.getDateiname().startsWith(ordnerPfad)) {
                    if (!propertyDatei.existiert()) {
                        propertyGeloescht = true;
                        it.remove();
                    }
                }
            }
        }
        return propertyGeloescht;
    }
}
