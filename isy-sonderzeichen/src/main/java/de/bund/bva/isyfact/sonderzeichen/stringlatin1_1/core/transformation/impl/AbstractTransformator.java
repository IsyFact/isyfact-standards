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
package de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.Transformator;
import de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.ZeichenKategorie;
import de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.konstanten.TransformationsKonstanten;

/**
 * Stellt gemeinsame Methoden für alle Transformatoren zur Verfügung.
 *
 */
public abstract class AbstractTransformator implements Transformator {

    /** Der reguläre Ausdruck für Leerzeichen in der Mitte eines Strings. */
    protected static final Pattern REG_EX_LEERZEICHEN = Pattern.compile("[  ]{2,}");

    /** Die Metazeichen eines regulären Ausdruckes. */
    private static final char[] REG_EX_META_CHARACTER = new char[] { '[', ']', '\\', '^', '$', '.', '|', '?',
        '*', '+', '-', (char) (Integer.valueOf("002D", 16).intValue()), '(', ')', '<', '>', '{', '}' };

    /** Die Transformationstabelle: Character -> StringBuffer. */
    protected Map transformationsTabelle = new HashMap();

    /**
     * Die Kategorietabelle mit den gueltigen Zeichen des Transformators String(ZeichenKategorie) -> String[].
     */
    protected Map kategorieGueltigeZeichenTabelle = new HashMap();

    /** Eine Map, welche gueltige Zeichen anhand ihrer Laenge sortiert: Integer -> (String) ArrayList. */
    protected Map laengeGueltigeZeichenMap = new HashMap();

    /** Die Standardersetzung (falls kein Eintrag in der Transformationstabelle gefunden wird). */
    protected String standardErsetzung;

    /** Die maximale Laenge von gueltigen Zeichen (wird bei Initialisierung gesetzt). */
    protected int maximaleGueltigeZeichenlaenge = 0;

    /**
     * Liefert die Transformationstabelle des Transformators zurück.
     * @return die Transformationstabelle des Transformators
     */
    protected abstract String getStandardTransformationsTabelle();

    /**
     * Liefert die Kategorietabelle des Transformators zurück.
     * @return die Kategorietabelle des Transformators
     */
    protected abstract String getKategorieTabelle();

    /**
     * Gibt den aktuellen Logger der implementierenden Klasse zurück.
     * @return den aktuellen Logger
     */
    protected abstract IsyLogger getLogger();

    /**
     * Initialisiert den Transformator. Optional kann eine zusätzliche Transformationstabelle übergeben
     * werden, welche zusätzlich geladen wird und existierende Einträge überschreibt.
     * @param zusaetzlicheTransformationsTabelle
     *            Der Pfad zur zusätzlichen Tabelle, <code>null</code> falls keine zusätzliche Tabelle geladen
     *            werden muss
     */
    public void initialisiere(String zusaetzlicheTransformationsTabelle) {

        getLogger().info(LogKategorie.JOURNAL, EreignisSchluessel.TRANSFORMATION,
            "Initialisiere Transformator.");

        try {
            // Schritt 1: Standard-Transformations-Tabelle laden.
            getLogger().info(LogKategorie.JOURNAL, EreignisSchluessel.TRANSFORMATION,
                "Lade Transformationstabelle: {}", getStandardTransformationsTabelle());
            ladeInTabelle(getClass().getResourceAsStream(getStandardTransformationsTabelle()));

            // Schritt 2: Zusaetzliche-Transformations-Tabelle laden, wenn vorhanden
            if (zusaetzlicheTransformationsTabelle != null) {
                getLogger().info(LogKategorie.JOURNAL, EreignisSchluessel.TRANSFORMATION,
                    "Lade Transformationstabelle: {}", zusaetzlicheTransformationsTabelle);
                ladeInTabelle(getClass().getResourceAsStream(zusaetzlicheTransformationsTabelle));
            }

            // Schritt 3: Zeichen in ihre Kategorien laden
            ladeInKategorieTabelle(getClass().getResourceAsStream(getKategorieTabelle()));

        } catch (IOException e) {
            getLogger().error(EreignisSchluessel.TRANSFORMATION,
                "Fehler beim Laden der Transformationstabelle => Abbruch", e);
            throw new RuntimeException();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transformiereOhneTrim(String zeichenkette) {

        if (zeichenkette == null) {
            return null;
        }

        // Schritt 1: Transformiere Zeichen in Zeichenkette
        return transformiereZeichenInZeichenkette(zeichenkette);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transformiere(String zeichenkette) {

        if (zeichenkette == null) {
            return null;
        }

        // Schritt 1: Transformiere Zeichen in Zeichenkette
        String transformiert = transformiereZeichenInZeichenkette(zeichenkette);

        // Schritt 2: Leerzeichen am Anfang und am Ende entfernen
        StringBuffer filterBuffer = new StringBuffer(transformiert.trim());

        // Schritt 3: Mehrfache Leerzeichen zu einem ersetzen
        Matcher matcher = REG_EX_LEERZEICHEN.matcher(filterBuffer);

        return matcher.replaceAll(TransformationsKonstanten.STRING_SPACE);
    }

    @Override
    public String[] getGueltigeZeichen(String kategorie) {
        return (String[]) this.kategorieGueltigeZeichenTabelle.get(kategorie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRegulaererAusdruck(String[] kategorieListe) {

        // Baue regulären Ausdruck
        StringBuffer regulaererAusdruck = new StringBuffer();
        boolean first = true;
        regulaererAusdruck.append("(");
        for (int i = 0; i < kategorieListe.length; i++) {
            String kategorie = kategorieListe[i];
            String[] strings = (String[]) this.kategorieGueltigeZeichenTabelle.get(kategorie);
            for (int j = 0; j < strings.length; j++) {
                String s = strings[j];
                if (!first) {
                    regulaererAusdruck.append("|");
                }
                first = false;
                for (int k = 0; k < s.length(); k++) {
                    char c = s.charAt(k);
                    if (containsChar(REG_EX_META_CHARACTER, c)) {
                        regulaererAusdruck.append("\\");
                    }
                    regulaererAusdruck.append(c);
                }
            }
        }
        regulaererAusdruck.append(")*");

        return regulaererAusdruck.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGueltigerString(String zeichenkette, String[] kategorieListe) {

        // Gueltige Zeichen der Kategorie ermitteln
        Set<String> gueltigeZeichenSet = new HashSet<>();
        for (String s : kategorieListe) {
            gueltigeZeichenSet.addAll(Arrays.asList(getGueltigeZeichen(s)));
        }

        // Durch Zeichenkette iterieren
        for (int zeichenketteIteration = 0; zeichenketteIteration < zeichenkette
            .length(); zeichenketteIteration++) {

            // Schrittweise über Laenge möglicher Zeichen iterieren, Länge der abbildbaren Zeichen muss
            // beachtet werden
            boolean treffer = false;
            for (int laengeIteration =
                this.maximaleGueltigeZeichenlaenge; laengeIteration > 0; laengeIteration--) {
                if (zeichenketteIteration + laengeIteration <= zeichenkette.length()) {
                    // Genügende Zeichen übrig
                    ArrayList gueltigeZeichenArrayList =
                        (ArrayList) this.laengeGueltigeZeichenMap.get(laengeIteration);
                    Iterator gueltigeZeichenIterator = gueltigeZeichenArrayList.iterator();
                    while (gueltigeZeichenIterator.hasNext()) {
                        String gueltigesZeichen = (String) gueltigeZeichenIterator.next();
                        if (gueltigeZeichenSet.contains(gueltigesZeichen)) {
                            boolean komplettesZeichenTreffer = false;
                            for (int gueltigesZeichenIteration =
                                0; gueltigesZeichenIteration < gueltigesZeichen
                                    .length(); gueltigesZeichenIteration++) {
                                if (gueltigesZeichen.charAt(gueltigesZeichenIteration) == zeichenkette
                                    .charAt(zeichenketteIteration + gueltigesZeichenIteration)) {
                                    komplettesZeichenTreffer = true;
                                } else {
                                    komplettesZeichenTreffer = false;
                                    break;
                                }
                            }

                            if (komplettesZeichenTreffer) {
                                treffer = true;
                                zeichenketteIteration = zeichenketteIteration + laengeIteration - 1;
                                break;
                            }
                        }
                    }
                    if (treffer) {
                        break;
                    }
                }
            }

            // Falls ein Zeichen nicht gemappt werden konnte, ist dies ein Fehler
            if (!treffer) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Defaultimplementierung für Transformatoren, die keine Regeln verwenden.
     */
    @Override
    public boolean werteRegelAus(int regel, String text, int position, int laenge) {
        return true;
    }

    private String transformiereZeichenInZeichenkette(String zeichenkette) {

        // Schritt 1: Chars der Zeichenkette schrittweise auf Eintrag in Mapping Tabelle überprüfen
        StringBuffer filtered = new StringBuffer();

        for (int i = 0; i < zeichenkette.length(); i++) {
            Object object = this.transformationsTabelle.get(zeichenkette.charAt(i));
            if (object == null || object instanceof StringBuffer) {
                if (object != null) {
                    filtered.append(object);
                } else {
                    filtered.append(this.standardErsetzung);
                }
            } else {
                filtered.append(((KomplexeTransformation) object).getErsetzung(zeichenkette, i));
                i += ((KomplexeTransformation) object).getLaengeLetzteErsetzung() - 1;
            }
        }
        return filtered.toString();
    }

    private void ladeInTabelle(InputStream inputStream) throws IOException {

        // Lade Datei
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        // Parse jeden Eintrag
        Iterator propertyIterator = properties.keySet().iterator();
        while (propertyIterator.hasNext()) {

            Object key = propertyIterator.next();

            // Linke Seite
            String links = (String) key;
            int linksHexChar;
            char linksChar = 0;
            String[] linksSplitted = new String[0];
            char[] linksSplittedChar = new char[0];
            String[] regeln = new String[0];
            if (!TransformationsKonstanten.EINTRAG_STANDARD.equals(links)) {
                String[] linksRegel = links.split("[|]");
                if (linksRegel.length > 1) {
                    regeln = linksRegel[1].split(",");
                }
                linksSplitted = linksRegel[0].split("[+]");
                if (linksSplitted.length == 1) {
                    linksHexChar = Integer.parseInt(links, 16);
                    linksChar = (char) linksHexChar;
                    linksSplittedChar = new char[] { linksChar };
                } else {
                    linksHexChar = Integer.parseInt(linksSplitted[0], 16);
                    linksChar = (char) linksHexChar;
                    linksSplittedChar = new char[linksSplitted.length];
                    for (int i = 0; i < linksSplitted.length; i++) {
                        linksSplittedChar[i] = (char) Integer.parseInt(linksSplitted[i], 16);
                    }
                }
            }

            // Rechte Seite
            String rechts = properties.getProperty(links);
            StringBuffer rechtsString = new StringBuffer();
            if (!"".equals(rechts)) {
                String[] rechtsSplitted = rechts.split("[+]");
                for (int i = 0; i < rechtsSplitted.length; i++) {
                    String rechtsTeil = rechtsSplitted[i];
                    rechtsTeil = rechtsTeil.trim();
                    int rechtsHexChar = Integer.parseInt(rechtsTeil, 16);
                    char rechtsTeilChar = (char) rechtsHexChar;
                    rechtsString.append(rechtsTeilChar);
                }
            }

            if (TransformationsKonstanten.EINTRAG_STANDARD.equals(links)) {
                this.standardErsetzung = rechtsString.toString();
                getLogger().debug("Transformation " + links + " -> " + rechtsString.toString() + " (" + rechts
                    + ") geladen.");
            } else if ("".equals(rechts)) {
                Object tabelleneintrag = this.transformationsTabelle.get(linksChar);
                if (tabelleneintrag == null) {
                    this.transformationsTabelle.put(linksChar,
                        new StringBuffer(TransformationsKonstanten.ZEICHEN_ENTFERNE));
                } else {
                    KomplexeTransformation transformation = (KomplexeTransformation) tabelleneintrag;
                    transformation.addErsetzung(Character.toString(linksChar),
                        TransformationsKonstanten.ZEICHEN_ENTFERNE);
                }
                getLogger()
                    .debug("Transformation " + linksChar + " (" + links + ") -> <entferneZeichen> geladen.");
            } else {
                Character linksKey = linksChar;
                Object tabelleneintrag = this.transformationsTabelle.get(linksKey);
                if (linksSplitted.length == 1 && regeln.length == 0 && tabelleneintrag == null) {
                    // Eine einfache Transformation ersetzt ein Zeichen durch ein oder mehrere andere und hat
                    // keine besonderen Regeln
                    this.transformationsTabelle.put(linksKey, new StringBuffer(rechtsString.toString()));
                    getLogger().debug("Transformation " + linksChar + " (" + links + ") -> "
                        + rechtsString.toString() + " (" + rechts + ") geladen.");
                } else {
                    // Eine komplexe Transformation ersetzt mehrere Zeichen auf einmal und/oder hat
                    // zusätzliche Regeln, wann die Transformation anzuwenden ist.
                    KomplexeTransformation transformation;
                    if (tabelleneintrag == null) {
                        // Neuanlage, falls noch nicht vorhanden
                        transformation = new KomplexeTransformation(this);
                        this.transformationsTabelle.put(linksKey, transformation);
                    } else if (tabelleneintrag instanceof StringBuffer) {
                        // Es ist schon eine einfache Transformation vorhanden -> umwandeln in komplexe
                        // Transformation
                        StringBuffer einfacheErsetzung = (StringBuffer) tabelleneintrag;
                        transformation = new KomplexeTransformation(this);
                        transformation.addErsetzung(linksKey.toString(), einfacheErsetzung.toString());
                        this.transformationsTabelle.put(linksKey, transformation);
                    } else {
                        // Es ist bereits eine komplexe Transformation vorhanden
                        transformation = (KomplexeTransformation) tabelleneintrag;
                    }
                    // Komplexe Transformation um eine weitere Ersetzung erweitern
                    transformation.addErsetzung(new String(linksSplittedChar), rechtsString.toString(),
                        regeln);
                    getLogger().debug("Transformation " + new String(linksSplittedChar) + " (" + links
                        + ") -> " + rechtsString.toString() + " (" + rechts + ") geladen.");
                }
            }

        }

    }

    private void ladeInKategorieTabelle(InputStream inputStream) throws IOException {

        // Lade Datei
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        // Stelle eine Liste für alle Zeichen-Kategorien zusammen
        for (int it = 0; it < ZeichenKategorie.ALLE_ZEICHEN_KATEGORIEN.length; it++) {

            String kategorie = ZeichenKategorie.ALLE_ZEICHEN_KATEGORIEN[it];

            Set<String> zeichenketteListe = new HashSet<>();
            Iterator gueltigeZeichenIterator = properties.keySet().iterator();
            while (gueltigeZeichenIterator.hasNext()) {

                String gueltigerCharacter = (String) gueltigeZeichenIterator.next();

                boolean lade = false;
                if (kategorie.equals(ZeichenKategorie.ALLE)) {
                    lade = true;
                } else if (kategorie.equals(properties.getProperty(gueltigerCharacter))) {
                    lade = true;
                }

                if (lade) {
                    // Parsen der Daten
                    char[] zeichen = ladeCharArrayAusProperty(gueltigerCharacter);
                    String newString = new String(zeichen);
                    zeichenketteListe.add(newString);
                }
            }

            String[] zeichenketteArray = new String[zeichenketteListe.size()];
            int i = 0;
            Iterator zeichenketteIterator = zeichenketteListe.iterator();
            while (zeichenketteIterator.hasNext()) {
                String zeichenkette = (String) zeichenketteIterator.next();

                zeichenketteArray[i] = zeichenkette;
                i++;

                // Debug
                getLogger().debug("Zeichen: " + zeichenkette + " in Kategorie " + kategorie + " geladen.");

                // Laengentabelle aktualisieren
                int zeichenketteLaenge = zeichenkette.length();
                if (zeichenkette.length() > this.maximaleGueltigeZeichenlaenge) {
                    this.maximaleGueltigeZeichenlaenge = zeichenketteLaenge;
                }

                ArrayList existierendeLaengeArray =
                    (ArrayList) this.laengeGueltigeZeichenMap.get(zeichenketteLaenge);
                if (existierendeLaengeArray == null) {
                    existierendeLaengeArray = new ArrayList();
                    this.laengeGueltigeZeichenMap.put(zeichenketteLaenge,
                        existierendeLaengeArray);
                }
                existierendeLaengeArray.add(zeichenkette);

            }

            this.kategorieGueltigeZeichenTabelle.put(kategorie, zeichenketteArray);

        }
    }

    private char[] ladeCharArrayAusProperty(String zeichenkette) {

        // Parst die zeichenkette auf die definierten chars

        String[] zeichenketteSplitted = zeichenkette.split("[+]");

        if (zeichenketteSplitted.length == 0) {
            return null;
        }

        char[] toReturn = new char[zeichenketteSplitted.length];

        for (int i = 0; i < zeichenketteSplitted.length; i++) {
            String teil = zeichenketteSplitted[i];
            teil = teil.trim();
            int hexChar = Integer.parseInt(teil, 16);
            char toChar = (char) hexChar;
            toReturn[i] = toChar;
        }

        return toReturn;
    }

    private boolean containsChar(char[] charArray, char c) {

        for (char value : charArray) {
            if (c == value) {
                return true;
            }
        }

        return false;
    }
}
