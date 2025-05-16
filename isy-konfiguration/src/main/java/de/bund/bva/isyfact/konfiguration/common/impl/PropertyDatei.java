package de.bund.bva.isyfact.konfiguration.common.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.konstanten.NachrichtenSchluessel;

/**
 * Hilfsklasse für {@link ReloadablePropertyProvider}. Diese Klasse repräsentiert eine Properties-Datei.
 * 
 */
class PropertyDatei {
    /**
     * Dateiname inkl. Pfad (relativ zum Classpath).
     */
    private String dateiname;

    /**
     * File-Objekt für Property-Datei.
     */
    private File propertyFile;

    /**
     * In Datei enthaltene Properties.
     */
    private Properties properties;

    /**
     * ModificationTime der Datei, beim letzten Laden der Properties.
     */
    private long letzteAenderung;

    /**
     * Erzeugt eine neue Instanz für die angegebene Properties-Datei. Diese wird per
     * {@link RessourcenHelper#getAbsoluterPfad(String)} geladen.
     * 
     * @throws KonfigurationDateiException
     *             Wenn die Datei nicht geladen werden kann.
     * @param dateiname
     *            Dateiname für Properties-Datei.
     * @see RessourcenHelper#getAbsoluterPfad(String)
     */
    PropertyDatei(String dateiname) {
        this.dateiname = dateiname;
        try {
            this.propertyFile = new File(RessourcenHelper.getAbsoluterPfad(dateiname));
            this.letzteAenderung = this.propertyFile.lastModified();
            neuLaden();
        } catch (IllegalArgumentException ex) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_FORMAT, ex, dateiname);
        }
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
        return this.propertyFile.lastModified() != this.letzteAenderung;
    }

    /**
     * Lädt die Properties aus der Datei neu. Wenn dabei ein Fehler auftritt wird das gekapselte
     * Properties-Objekt nicht ersetzt und behält die vorherigen Einträge.
     * @throws KonfigurationDateiException
     *             Wenn die Datei nicht geladen werden konnte.
     */
    public void neuLaden() {
        Properties neueProperties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(this.propertyFile);
            neueProperties.load(inputStream);
            this.letzteAenderung = this.propertyFile.lastModified();
            inputStream.close();
            this.properties = neueProperties;
        } catch (FileNotFoundException ex) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_NICHT_GEFUNDEN, ex,
                this.dateiname);
        } catch (IOException ex) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, ex, this.dateiname);
        } catch (IllegalArgumentException ex) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_FORMAT, ex, this.dateiname);
        }
    }

    /**
     * Liefert den Dateinamen.
     * @return Den Namen der Property-Datei.
     */
    public String getDateiname() {
        return this.dateiname;
    }

    /**
     * Prüft ob die PropertyDatei existiert.
     * @return true, wenn die PropertyDatei existiert.
     * @see File#exists()
     */
    public boolean existiert() {
        return this.propertyFile.exists();
}

}
