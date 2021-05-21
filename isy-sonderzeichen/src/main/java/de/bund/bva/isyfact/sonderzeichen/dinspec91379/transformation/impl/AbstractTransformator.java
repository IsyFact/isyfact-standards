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
package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.Transformator;
import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.ZeichenKategorie;
import de.bund.bva.isyfact.sonderzeichen.dinspec91379.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.sonderzeichen.dinspec91379.konstanten.TransformationsKonstanten;

/**
 * Provides common methods for all transformers.
 *
 */
public abstract class AbstractTransformator implements Transformator {

    /** The regular expression for spaces in the middle of a string. */
    protected static final Pattern REG_EX_LEERZEICHEN = Pattern.compile("[ ]{2,}");

    /** The metacharacters of a regular expression. */
    private static final char[] REG_EX_META_CHARACTER = new char[] { '[', ']', '\\', '^', '$', '.', '|', '?',
        '*', '+', '-', (char) (Integer.valueOf("002D", 16).intValue()), '(', ')', '<', '>', '{', '}' };

    /**
     * Transformation table: Character -> Object, where the Object is typically a StringBuilder or a
     * KomplexeTransformation.
     */
    protected final Map<Character, Object> transformationsTabelle = new HashMap<>();

    /**
     * The category table with the valid characters of the transformer String(ZeichenKategorie) -> String[].
     */
    protected final Map<String, String[]> kategorieGueltigeZeichenTabelle = new HashMap<>();

    /** A map that sorts valid characters based on their length: Integer -> (String) ArrayList. */
    protected final Map<Integer, ArrayList<String>> laengeGueltigeZeichenMap = new HashMap<>();

    /** The standard replacement (if no entry is found in the transformation table). */
    protected String standardErsetzung;

    /** The maximum length of valid characters (is set during initialization). */
    protected int maximaleGueltigeZeichenlaenge;

    /**
     * Returns the transformation table of the transformer.
     * @return the transformation table of the transformer
     */
    protected abstract String getStandardTransformationsTabelle();

    /**
     * Returns the category table of the transformer.
     * @return the category table of the transformer
     */
    protected abstract String getKategorieTabelle();

    /**
     * Returns the current logger of the implementing class.
     * @return the current logger
     */
    protected abstract IsyLogger getLogger();

    /**
     * Initializes the transformer. Optionally, an additional transformation table can be transferred, which is
     * also loaded and overwrites existing entries.
     * @param zusaetzlicheTransformationsTabelle
     *            The path to the additional table, <code> null </code> if no additional table needs to be loaded
     */
    public void initialisiere(String zusaetzlicheTransformationsTabelle) {

        getLogger().info(LogKategorie.JOURNAL, EreignisSchluessel.TRANSFORMATION,
            "Initialisiere Transformator.");

        try {
            // Step 1: Load the standard transformation table.
            getLogger().info(LogKategorie.JOURNAL, EreignisSchluessel.TRANSFORMATION,
                "Lade Transformationstabelle: {}", getStandardTransformationsTabelle());
            ladeInTabelle(getClass().getResourceAsStream(getStandardTransformationsTabelle()));

            // Step 2: Load additional transformation table, if available
            if (zusaetzlicheTransformationsTabelle != null) {
                getLogger().info(LogKategorie.JOURNAL, EreignisSchluessel.TRANSFORMATION,
                    "Lade Transformationstabelle: {}", zusaetzlicheTransformationsTabelle);
                ladeInTabelle(getClass().getResourceAsStream(zusaetzlicheTransformationsTabelle));
            }

            // Step 3: load characters into their categories
            ladeInKategorieTabelle(getClass().getResourceAsStream(getKategorieTabelle()));

        } catch (IOException e) {
            getLogger().error(EreignisSchluessel.TRANSFORMATION,
                "Fehler beim Laden der Transformationstabelle => Abbruch", e);
            throw new RuntimeException();
        }

    }

    @Override
    public String transformiereOhneTrim(String zeichenkette) {

        if (zeichenkette == null) {
            return null;
        }

        // Step 1: Transform characters into strings
        return transformiereZeichenInZeichenkette(zeichenkette);

    }

    @Override
    public String transformiere(String zeichenkette) {

        if (zeichenkette == null) {
            return null;
        }

        // Step 1: Transform characters into strings
        String transformiert = transformiereZeichenInZeichenkette(zeichenkette);

        // Step 2: Remove spaces at the beginning and at the end
        StringBuilder filterBuffer = new StringBuilder(transformiert.trim());

        // Step 3: replace multiple spaces into one
        Matcher matcher = REG_EX_LEERZEICHEN.matcher(filterBuffer);

        return matcher.replaceAll(TransformationsKonstanten.STRING_SPACE);
    }

    @Override
    public String[] getGueltigeZeichen(String kategorie) {
        return this.kategorieGueltigeZeichenTabelle.get(kategorie);
    }

    @Override
    public String getRegulaererAusdruck(String[] kategorieListe) {

        // Build regular expression
        StringBuilder regulaererAusdruck = new StringBuilder();
        boolean first = true;
        regulaererAusdruck.append("(");
        for (String kategorie : kategorieListe) {
            String[] strings = this.kategorieGueltigeZeichenTabelle.get(kategorie);
            for (String s : strings) {
                if (!first) {
                    regulaererAusdruck.append("|");
                }
                first = false;
                for (char c : s.toCharArray()) {
                    if (new String(REG_EX_META_CHARACTER).contains(Character.toString(c))) {
                        regulaererAusdruck.append("\\");
                    }
                    regulaererAusdruck.append(c);
                }
            }
        }
        regulaererAusdruck.append(")*");

        return regulaererAusdruck.toString();
    }

    @Override
    public boolean isGueltigerString(String zeichenkette, String[] kategorieListe) {

        // Determine valid characters of the category
        Set<String> gueltigeZeichenSet = new HashSet<>();
        for (String s : kategorieListe) {
            gueltigeZeichenSet.addAll(Arrays.asList(getGueltigeZeichen(s)));
        }

        // Iterate through string
        for (int zeichenketteIteration = 0; zeichenketteIteration < zeichenkette
            .length(); zeichenketteIteration++) {

            // Iterate step by step over the length of possible characters, length of the mappable characters
            // must be taken into account
            boolean treffer = false;
            for (int laengeIteration =
                this.maximaleGueltigeZeichenlaenge; laengeIteration > 0; laengeIteration--) {
                if (zeichenketteIteration + laengeIteration <= zeichenkette.length()) {
                    // Enough characters left
                    List<String> gueltigeZeichenArrayList =
                        this.laengeGueltigeZeichenMap.get(laengeIteration);
                    for (String gueltigesZeichen : gueltigeZeichenArrayList) {
                        if (gueltigeZeichenSet.contains(gueltigesZeichen)) {
                            boolean komplettesZeichenTreffer = !gueltigesZeichen.isEmpty();
                            for (int gueltigesZeichenIteration = 0;
                                 gueltigesZeichenIteration < gueltigesZeichen.length();
                                 gueltigesZeichenIteration++) {
                                if (gueltigesZeichen.charAt(gueltigesZeichenIteration) !=
                                    zeichenkette.charAt(zeichenketteIteration + gueltigesZeichenIteration)) {
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

            // If a character could not be mapped, this is an error
            if (!treffer) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Default implementation for transformers that do not use rules.
     */
    @Override
    public boolean werteRegelAus(int regel, String text, int position, int laenge) {
        return true;
    }

    private String transformiereZeichenInZeichenkette(String zeichenkette) {

        // Step 1: Check the characters of the character string step by step for entries in the mapping table
        StringBuilder filtered = new StringBuilder();

        for (int i = 0; i < zeichenkette.length(); i++) {
            Object object = this.transformationsTabelle.get(zeichenkette.charAt(i));
            if (object == null) {
                filtered.append(this.standardErsetzung);
            } else if (object instanceof StringBuilder) {
                filtered.append(object);
            } else {
                filtered.append(((KomplexeTransformation) object).getErsetzung(zeichenkette, i));
                i += ((KomplexeTransformation) object).getLaengeLetzteErsetzung() - 1;
            }
        }
        return filtered.toString();
    }

    private void ladeInTabelle(InputStream inputStream) throws IOException {

        // Load file
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        // Parse each entry
        for (Object key : properties.keySet()) {
            // Left side
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

            // Right side
            String rechts = properties.getProperty(links);
            StringBuilder rechtsString = new StringBuilder();
            if (!"".equals(rechts)) {
                String[] rechtsSplitted = rechts.split("[+]");
                for (String s : rechtsSplitted) {
                    s = s.trim();
                    int rechtsHexChar = Integer.parseInt(s, 16);
                    char rechtsTeilChar = (char) rechtsHexChar;
                    rechtsString.append(rechtsTeilChar);
                }
            }

            if (TransformationsKonstanten.EINTRAG_STANDARD.equals(links)) {
                this.standardErsetzung = rechtsString.toString();
                getLogger().debug("Transformation " + links + " -> " + rechtsString + " (" + rechts
                    + ") geladen.");
            } else if ("".equals(rechts)) {
                Object tabelleneintrag = this.transformationsTabelle.get(linksChar);
                if (tabelleneintrag == null) {
                    this.transformationsTabelle.put(linksChar,
                        new StringBuilder(TransformationsKonstanten.ZEICHEN_ENTFERNE));
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
                    // A simple transformation replaces one character with one or more others and has no
                    // special rules
                    this.transformationsTabelle.put(linksKey, new StringBuilder(rechtsString.toString()));
                    getLogger().debug("Transformation " + linksChar + " (" + links + ") -> "
                        + rechtsString + " (" + rechts + ") geladen.");
                } else {
                    // A complex transformation replaces several characters at once and / or has additional
                    // rules as to when the transformation is to be used.
                    KomplexeTransformation transformation;
                    if (tabelleneintrag == null) {
                        // New creation, if not already available
                        transformation = new KomplexeTransformation(this);
                        this.transformationsTabelle.put(linksKey, transformation);
                    } else if (tabelleneintrag instanceof StringBuilder) {
                        // There is already a simple transformation -> convert to complex transformation
                        StringBuilder einfacheErsetzung = (StringBuilder) tabelleneintrag;
                        transformation = new KomplexeTransformation(this);
                        transformation.addErsetzung(linksKey.toString(), einfacheErsetzung.toString());
                        this.transformationsTabelle.put(linksKey, transformation);
                    } else {
                        // A complex transformation already exists
                        transformation = (KomplexeTransformation) tabelleneintrag;
                    }
                    // Extend complex transformation with another replacement
                    transformation
                        .addErsetzung(new String(linksSplittedChar), rechtsString.toString(), regeln);
                    getLogger().debug("Transformation " + new String(linksSplittedChar) +
                        " (" + links + ") -> " + rechtsString + " (" + rechts + ") geladen.");
                }
            }

        }

    }

    private void ladeInKategorieTabelle(InputStream inputStream) throws IOException {

        // Load file
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        // Make a list of all character categories
        for (String kategorie : ZeichenKategorie.ALLE_ZEICHEN_KATEGORIEN) {

            Set<String> zeichenketteSet = new HashSet<>();
            for (Object o : properties.keySet()) {

                String gueltigerCharacter = (String) o;

                boolean lade = kategorie.equals(ZeichenKategorie.ALLE)
                    || kategorie.equals(properties.getProperty(gueltigerCharacter));

                if (lade) {
                    // Parsing the data
                    char[] zeichen = ladeCharArrayAusProperty(gueltigerCharacter);
                    if (zeichen != null) {
                        String newString = new String(zeichen);
                        zeichenketteSet.add(newString);
                    }
                }
            }

            List<String> zeichenketteList = new ArrayList<>();
            for (String zeichenkette : zeichenketteSet) {

                zeichenketteList.add(zeichenkette);


                // Debug
                getLogger().debug("Zeichen: " + zeichenkette + " in Kategorie " + kategorie + " geladen.");

                // Update length table
                int zeichenketteLaenge = zeichenkette.length();
                if (zeichenkette.length() > this.maximaleGueltigeZeichenlaenge) {
                    this.maximaleGueltigeZeichenlaenge = zeichenketteLaenge;
                }

                List<String> existierendeLaengeArray =
                    this.laengeGueltigeZeichenMap.computeIfAbsent(zeichenketteLaenge, k -> new ArrayList<>());
                existierendeLaengeArray.add(zeichenkette);

            }

            this.kategorieGueltigeZeichenTabelle.put(kategorie, zeichenketteList.toArray(new String[0]));

        }
    }

    private char[] ladeCharArrayAusProperty(String zeichenkette) {

        // Parses the string on the defined chars

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
}
