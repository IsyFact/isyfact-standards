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
 * Provides common methods for all transformers.
 *
 *  @deprecated This class is deprecated and will be removed in a future release.
 *  It is recommended to use {@link de.bund.bva.isyfact.sonderzeichen.dinspec91379} instead.
 */
@Deprecated
public abstract class AbstractTransformator implements Transformator {

    /** The regular expression for spaces in the middle of a string. */
    protected static final Pattern REG_EX_LEERZEICHEN = Pattern.compile("[  ]{2,}");

    /** The metacharacters of a regular expression. */
    private static final char[] REG_EX_META_CHARACTER = new char[] { '[', ']', '\\', '^', '$', '.', '|', '?',
        '*', '+', '-', (char) (Integer.valueOf("002D", 16).intValue()), '(', ')', '<', '>', '{', '}' };

    /** Transformation table: Character -> StringBuffer. */
    protected Map transformationsTabelle = new HashMap();

    /**
     * The category table with the valid characters of the transformer String(ZeichenKategorie) -> String[].
     */
    protected Map kategorieGueltigeZeichenTabelle = new HashMap();

    /** A map that sorts valid characters based on their length: Integer -> (String) ArrayList. */
    protected Map laengeGueltigeZeichenMap = new HashMap();

    /** The standard replacement (if no entry is found in the transformation table). */
    protected String standardErsetzung;

    /** The maximum length of valid characters (is set during initialization). */
    protected int maximaleGueltigeZeichenlaenge = 0;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String transformiereOhneTrim(String zeichenkette) {

        if (zeichenkette == null) {
            return null;
        }

        // Step 1: Transform characters into strings
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

        // Step 1: Transform characters into strings
        String transformiert = transformiereZeichenInZeichenkette(zeichenkette);

        // Step 2: Remove spaces at the beginning and at the end
        StringBuffer filterBuffer = new StringBuffer(transformiert.trim());

        // Step 3: replace multiple spaces into one
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

        // Build regular expression
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

        // Load file
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        // Parse each entry
        Iterator propertyIterator = properties.keySet().iterator();
        while (propertyIterator.hasNext()) {

            Object key = propertyIterator.next();

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
                    // A simple transformation replaces one character with one or more others and has no
                    // special rules
                    this.transformationsTabelle.put(linksKey, new StringBuffer(rechtsString.toString()));
                    getLogger().debug("Transformation " + linksChar + " (" + links + ") -> "
                        + rechtsString.toString() + " (" + rechts + ") geladen.");
                } else {
                    // A complex transformation replaces several characters at once and / or has additional
                    // rules as to when the transformation is to be used.
                    KomplexeTransformation transformation;
                    if (tabelleneintrag == null) {
                        // New creation, if not already available
                        transformation = new KomplexeTransformation(this);
                        this.transformationsTabelle.put(linksKey, transformation);
                    } else if (tabelleneintrag instanceof StringBuffer) {
                        // There is already a simple transformation -> convert to complex transformation
                        StringBuffer einfacheErsetzung = (StringBuffer) tabelleneintrag;
                        transformation = new KomplexeTransformation(this);
                        transformation.addErsetzung(linksKey.toString(), einfacheErsetzung.toString());
                        this.transformationsTabelle.put(linksKey, transformation);
                    } else {
                        // A complex transformation already exists
                        transformation = (KomplexeTransformation) tabelleneintrag;
                    }
                    // Extend complex transformation with another replacement
                    transformation.addErsetzung(new String(linksSplittedChar), rechtsString.toString(),
                        regeln);
                    getLogger().debug("Transformation " + new String(linksSplittedChar) + " (" + links
                        + ") -> " + rechtsString.toString() + " (" + rechts + ") geladen.");
                }
            }

        }

    }

    private void ladeInKategorieTabelle(InputStream inputStream) throws IOException {

        // Load file
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        String[] alleZeichenKategorien = ZeichenKategorie.getAlleZeichenKategorien();

        // Make a list of all character categories
        for (int it = 0; it < alleZeichenKategorien.length; it++) {

            String kategorie = alleZeichenKategorien[it];

            Set<String> zeichenketteListe = new HashSet<>();
            Iterator gueltigeZeichenIterator = properties.keySet().iterator();
            while (gueltigeZeichenIterator.hasNext()) {

                String gueltigerCharacter = (String) gueltigeZeichenIterator.next();

                if (kategorie.equals(ZeichenKategorie.ALLE)
                        || kategorie.equals(properties.getProperty(gueltigerCharacter))) {
                    // Parsing the data
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

                // Update length table
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

    private boolean containsChar(char[] charArray, char c) {

        for (char value : charArray) {
            if (c == value) {
                return true;
            }
        }

        return false;
    }
}
