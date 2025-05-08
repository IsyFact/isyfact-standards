package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.CharacterUtil;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.konstanten.TransformationsKonstanten;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.Transformation;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.TransformationMetadaten;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.Transformator;
import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.ZeichenKategorie;

/**
 * Provides common methods for all transformers.
 *
 */
public abstract class AbstractTransformator implements Transformator {

    /** The regular expression for spaces in the middle of a string. */
    protected static final Pattern REG_EX_LEERZEICHEN = Pattern.compile("[ ]{2,}");

    /** The metacharacters of a regular expression. */
    private static final char[] REG_EX_META_CHARACTER = { '[', ']', '\\', '^', '$', '.', '|', '?',
        '*', '+', '-', '(', ')', '<', '>', '{', '}' };

    /**
     * Transformation table: Character -> Object, where the Object is typically a StringBuilder or a
     * KomplexeTransformation.
     */
    protected final Map<Character, Object> transformationsTabelle = new HashMap<>();

    /**
     * The category table with the valid characters of the transformer String(ZeichenKategorie) -> String[].
     */
    protected final Map<String, String[]> kategorieGueltigeZeichenTabelle = new HashMap<>();

    /** The standard replacement (if no entry is found in the transformation table). */
    protected String standardErsetzung;

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
     *            The path to the additional table, {@code null} if no additional table needs to be loaded
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
            throw new RuntimeException(e);
        }

    }

    @Override
    public String transformiereOhneTrim(String zeichenkette) {

        return transformiereOhneTrimMitMetadaten(zeichenkette).getTransformierterText();
    }

    @Override
    public String transformiere(String zeichenkette) {

        return transformiereMitMetadaten(zeichenkette).getTransformierterText();
    }

    @Override
    public Transformation transformiereOhneTrimMitMetadaten(String zeichenkette) {

        if (zeichenkette == null) {
            return new Transformation(null, new ArrayList<>());
        }

        // Step 1: Transform characters into strings
        return transformiereZeichenInZeichenkette(zeichenkette);
    }

    @Override
    public Transformation transformiereMitMetadaten(String zeichenkette) {

        if (zeichenkette == null) {
            return new Transformation(null, new ArrayList<>());
        }

        // Step 1: Transform characters into strings
        Transformation transformation = transformiereZeichenInZeichenkette(zeichenkette);
        String transformiert = transformation.getTransformierterText();

        // Step 2: Remove spaces at the beginning and at the end
        int laengeTransformiert = transformiert.length();
        transformiert = transformiert.replaceAll("^\\s+", "");
        int fuehrendeLeerzeichen = laengeTransformiert - transformiert.length();
        transformiert = transformiert.replaceAll("\\s+$", "");
        StringBuilder filterBuffer = new StringBuilder(transformiert);

        // Adjust the new positions in the metadata after deleting leading and trailing spaces
        transformation.getMetadatenList().forEach(e -> {
            // Adjust metadata for deleted leading characters
            int neuePosition = Math.max(-1, (e.getNeuePosition() - fuehrendeLeerzeichen));
            // Adjust metadata for deleted trailing characters
            if (neuePosition >= filterBuffer.length()) {
                neuePosition  = -2;
            }
            e.setNeuePosition(neuePosition);
        });

        // Step 3: replace multiple consecutive spaces with a single space
        Matcher matcher = REG_EX_LEERZEICHEN.matcher(filterBuffer);
        while (matcher.find()) {
            int spacesStart = matcher.start();
            int spacesEnd = matcher.end();
            transformiert = matcher.replaceFirst(TransformationsKonstanten.STRING_SPACE);
            matcher = REG_EX_LEERZEICHEN.matcher(transformiert);
            // Adjust new positions in the metadata after replacing multiple consecutive spaces
            transformation.getMetadatenList().forEach(e -> {
                if (e.getNeuePosition() > spacesStart) {
                    if (e.getNeuePosition() < spacesEnd) {
                        e.setNeuePosition(spacesStart);
                    } else {
                        e.setNeuePosition(e.getNeuePosition() - (spacesEnd - spacesStart - 1));
                    }
                }
            });
        }

        transformation.setTransformierterText(matcher.replaceAll(TransformationsKonstanten.STRING_SPACE));
        return transformation;
    }

    @Override
    public String[] getGueltigeZeichen(String kategorie) {
        return kategorieGueltigeZeichenTabelle.get(kategorie);
    }

    @Override
    public String getRegulaererAusdruck(String[] kategorieListe) {
        return Arrays.stream(kategorieListe)
                .map(kategorieGueltigeZeichenTabelle::get)
                .flatMap(Arrays::stream)
                .map(AbstractTransformator::escapeRegexMetaChars)
                .collect(Collectors.joining("|", "(", ")*"));
    }

    private static String escapeRegexMetaChars(String s) {
        StringBuilder result = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (isRegexMetaChar(c)) {
                result.append('\\');
            }
            result.append(c);
        }
        return result.toString();
    }

    private static boolean isRegexMetaChar(char c) {
        for (char metaChar : REG_EX_META_CHARACTER) {
            if (c == metaChar) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isGueltigerString(String zeichenkette, String[] kategorieListe) {
        // Determine valid characters of the category
        Set<String> gueltigeZeichenSet = Arrays.stream(kategorieListe)
                .map(this::getGueltigeZeichen)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());

        return CharacterUtil.containsOnlyCharsFromSet(zeichenkette, gueltigeZeichenSet);
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

    /**
     * Transforms characters in a string.
     * @param zeichenkette String to transform
     * @return Transformation object containing the transformed string and the metadata of the transformation
     */
    private Transformation transformiereZeichenInZeichenkette(String zeichenkette) {

        ArrayList<TransformationMetadaten> metadaten = new ArrayList<>();
        int verschiebung = 0;

        // Step 1: Check the characters of the character string step by step for entries in the mapping table
        StringBuilder filtered = new StringBuilder(zeichenkette.length());

        for (int i = 0; i < zeichenkette.length(); i++) {
            Object object = transformationsTabelle.get(zeichenkette.charAt(i));
            if (object == null) {
                filtered.append(standardErsetzung);
                String ersetzung = standardErsetzung;
                if (ersetzung == null) {
                    ersetzung = "null";
                }
                String altesZeichen = String.valueOf(zeichenkette.charAt(i));
                metadaten.add(new TransformationMetadaten(
                    altesZeichen, getCodepoint(altesZeichen), ersetzung, getCodepoint(standardErsetzung), i, i + verschiebung));
                verschiebung += ersetzung.length() - altesZeichen.length();
            } else if (object instanceof StringBuilder) {
                filtered.append(object);
                String ersetzung = object.toString();
                String altesZeichen = String.valueOf(zeichenkette.charAt(i));
                if (!altesZeichen.equals(ersetzung)) {
                    metadaten.add(new TransformationMetadaten(
                        altesZeichen, getCodepoint(altesZeichen), object.toString(), getCodepoint(object.toString()), i, i + verschiebung));
                    verschiebung += ersetzung.length() - altesZeichen.length();
                }
            } else {
                final KomplexeTransformation komplexeTransformation = (KomplexeTransformation) object;
                String ersetzung = komplexeTransformation.getErsetzung(zeichenkette, i);
                String altesZeichen = komplexeTransformation.getAltesZeichenLetzteErsetzung();
                filtered.append(ersetzung);
                if (!altesZeichen.equals(ersetzung)) {
                    metadaten.add(new TransformationMetadaten(
                        altesZeichen, getCodepoint(altesZeichen), ersetzung, getCodepoint(ersetzung), i, i + verschiebung));
                }
                verschiebung += ersetzung.length() - altesZeichen.length();
                i += komplexeTransformation.getLaengeLetzteErsetzung() - 1;
            }
        }
        return new Transformation(filtered.toString(), metadaten);
    }

    /**
     * Returns unicode codepoints of the characters in a string. Multiple Codepoints are seperated by " + ".
     * @param text Characters whose codepoints are returned.
     * @return Codepoints
     */
    private String getCodepoint(String text) {
        if (text == null) {
            return "null".codePoints()
                .mapToObj(e -> String.format("%04X", e))
                .collect(Collectors.joining(" + "));
        }
        return text.codePoints()
            .mapToObj(e -> String.format("%04X", e))
            .collect(Collectors.joining(" + "));
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
                int linksHexChar;
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
            StringBuilder rechtsString = new StringBuilder(0);
            if (rechts != null && !rechts.isEmpty()) {
                String[] rechtsSplitted = rechts.split("[+]");
                for (String s : rechtsSplitted) {
                    String rechtsTeil = s.trim();
                    int rechtsHexChar = Integer.parseInt(rechtsTeil, 16);
                    char rechtsTeilChar = (char) rechtsHexChar;
                    rechtsString.append(rechtsTeilChar);
                }
            }

            if (TransformationsKonstanten.EINTRAG_STANDARD.equals(links)) {
                standardErsetzung = rechtsString.toString();
                getLogger().debug("Transformation {} -> {} ({}) geladen.", links, rechtsString, rechts);
            } else if (rechts != null && rechts.isEmpty()) {
                Object tabelleneintrag = transformationsTabelle.get(linksChar);
                if (tabelleneintrag == null) {
                    transformationsTabelle.put(linksChar,
                        new StringBuilder(TransformationsKonstanten.ZEICHEN_ENTFERNE));
                } else {
                    KomplexeTransformation transformation = (KomplexeTransformation) tabelleneintrag;
                    transformation.addErsetzung(Character.toString(linksChar),
                        TransformationsKonstanten.ZEICHEN_ENTFERNE);
                }
                getLogger().debug("Transformation {} ({}) -> <entferneZeichen> geladen.", linksChar, links);
            } else {
                Character linksKey = linksChar;
                Object tabelleneintrag = transformationsTabelle.get(linksKey);
                if (linksSplitted.length == 1 && regeln.length == 0 && tabelleneintrag == null) {
                    // A simple transformation replaces one character with one or more others and has no
                    // special rules
                    transformationsTabelle.put(linksKey, new StringBuilder(rechtsString.toString()));
                    getLogger().debug("Transformation {} ({}) -> {} ({}) geladen.",
                            linksChar, links, rechtsString, rechts);
                } else {
                    // A complex transformation replaces several characters at once and / or has additional
                    // rules as to when the transformation is to be used.
                    KomplexeTransformation transformation;
                    if (tabelleneintrag == null) {
                        // New creation, if not already available
                        transformation = new KomplexeTransformation(this);
                        transformationsTabelle.put(linksKey, transformation);
                    } else if (tabelleneintrag instanceof StringBuilder) {
                        // There is already a simple transformation -> convert to complex transformation
                        StringBuilder einfacheErsetzung = (StringBuilder) tabelleneintrag;
                        transformation = new KomplexeTransformation(this);
                        transformation.addErsetzung(linksKey.toString(), einfacheErsetzung.toString());
                        transformationsTabelle.put(linksKey, transformation);
                    } else {
                        // A complex transformation already exists
                        transformation = (KomplexeTransformation) tabelleneintrag;
                    }
                    // Extend complex transformation with another replacement
                    transformation
                        .addErsetzung(new String(linksSplittedChar), rechtsString.toString(), regeln);
                    getLogger().debug("Transformation {} ({}) -> {} ({}) geladen.",
                            linksSplittedChar, links, rechtsString, rechts);
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
        for (String kategorie : ZeichenKategorie.getAlleZeichenKategorien()) {

            Set<String> zeichenketteSet = new HashSet<>();
            for (Object o : properties.keySet()) {

                String gueltigerCharacter = (String) o;

                boolean lade = kategorie.equals(ZeichenKategorie.ALLE)
                    || kategorie.equals(properties.getProperty(gueltigerCharacter));

                if (lade) {
                    // Parsing the data
                    char[] zeichen = CharacterUtil.parseString(gueltigerCharacter);
                    if (zeichen != null) {
                        String newString = new String(zeichen);
                        zeichenketteSet.add(newString);

                        // Debug
                        getLogger().debug("Zeichen: {} in Kategorie {} geladen.", newString, kategorie);
                    }
                }
            }

            kategorieGueltigeZeichenTabelle.put(kategorie, zeichenketteSet.toArray(new String[0]));
        }
    }

}
