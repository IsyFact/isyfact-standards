package de.bund.bva.pliscommon.konfiguration.common.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;

/**
 * Diese Klasse erleichtert den Umgang mit Ordnern, die Property-Dateien enthalten.
 */
class RessourcenHelper {

    public final static String DEFAULTNAMENSSCHEMA = ".*[.]properties";

    /**
     * Sucht alle Properties-Dateien in einem bestimmten Ordner und liefert den relativen Pfad zu den Dateien
     * sortiert zurück. Die Property-Dateien müssen dem namensSchema entsprechen, alle anderen Dateien werden
     * ignoriert.
     * @param ordnerPfad
     *            Pfad zu dem Ordner, in dem nach Properties-Dateien gesucht werden sollen.
     * @param namensSchema
     *            das Schema, dem die Dateinamen entsprechen müssen.
     * @return Das Set mit den Pfaden zu allen Properties-Dateien.
     */
    public static Set<String> ladePropertiesAusOrdner(String ordnerPfad, String namensSchema) {
        Set<String> allePropertiesPfade = new HashSet<>();
        URI ordnerUri = getAbsoluterPfad(ordnerPfad);
        File ordner = new File(ordnerUri);
        File[] alleProperties = ordner.listFiles();
        Arrays.sort(alleProperties, new FileComparator());
        for (File property : alleProperties) {
            if (Pattern.matches(namensSchema, property.getName())) {
                String relativerPfad = ordnerPfad.concat(property.getName());
                allePropertiesPfade.add(relativerPfad);
            }
        }
        return allePropertiesPfade;
    }

    /**
     * Erzeugt den absoluten Pfad einer Datei die im Classpath abgelegt ist. Der Pfad wird per
     * {@link Class#getResource(String)} geladen.
     * @param relativerPfad
     *            Pfad zu einer Datei die im Classpath liegt.
     * @throws KonfigurationDateiException
     *             Wenn die Datei nicht geladen werden kann.
     * @return Absoluter Pfad, der aus dem relativen Pfad erzeugt wurde.
     */
    public static URI getAbsoluterPfad(String relativerPfad) {
        URL url = RessourcenHelper.class.getResource(relativerPfad);
        if (url == null) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_NICHT_GEFUNDEN,
                relativerPfad);
        }

        URI absoluterPfad;
        try {
            absoluterPfad = url.toURI();
        } catch (URISyntaxException ex) {
            throw new KonfigurationDateiException(NachrichtenSchluessel.ERR_DATEI_LESEN, ex, relativerPfad);
        }
        return absoluterPfad;
    }

    /**
     * Prüft, ob es sich bei dem angegebenen Pfad zu einer Ressource um einen Ordner oder eine Datei handelt.
     * @param relativerPfad
     *            Relativer Pfad zu einer Ressource.
     * @return true wenn die Ressource ein Ordner ist, false wenn es eine Datei ist.
     */
    public static boolean istOrdner(String relativerPfad) {
        URI absoluterPfad = getAbsoluterPfad(relativerPfad);
        return Files.isDirectory(Paths.get(absoluterPfad));
    }

    public static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

}
