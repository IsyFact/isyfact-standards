package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.Transformator;

/**
 * This class maps transformations in which more than one character is transformed or in which special
 * conditions must be met.
 * 
 */
public class KomplexeTransformation {

    /**
     * This class describes a possible replacement with the rules that may be necessary for this.
     */
    private static class Ersetzung {

        /**
         * The numbers of the rules that if met, the replacement can be applied.
         * If more than one rule is specified, the replacement is used if at least one of them is met.
         * If no rule is given, the replacement can always be used.
         */
        private int[] regeln = new int[0];

        /** The string to replace the original characters with. */
        private String ersatz;
    }

    /** The transformer to which the transformation is assigned. */
    private final Transformator transformator;

    /** Contains all possible replacements that begin with a specific character. */
    private final Map<String, List<Ersetzung>> ersetzungen = new HashMap<>();

    /** The length of the longest character combination to replace. */
    private int maxKeyLaenge;

    /** Number of characters that would have been replaced with the last character replacement determined. */
    private int laengeLetzteErsetzung;

    /** Characters that would have been replaced with the last character replacement determined. */
    private String altesZeichenLetzteErsetzung;

    public KomplexeTransformation(Transformator transformator) {
        this.transformator = transformator;
    }

    /**
     * Adds a new replacement with no rules to the list of replacements.
     * 
     * @param ersetzenVon
     *            The string to be replaced.
     * @param ersetzenNach
     *            Character string to be replaced by.
     */
    public void addErsetzung(String ersetzenVon, String ersetzenNach) {
        Ersetzung ersetzung = new Ersetzung();
        ersetzung.ersatz = ersetzenNach;
        addErsetzung(ersetzenVon, ersetzung);
    }

    /**
     * Adds a new replacement with rules to the list of replacements.
     * 
     * @param ersetzenVon
     *            The string to be replaced.
     * @param ersetzenNach
     *            Character string to be replaced by.
     * @param regeln
     *            List of rules to consider for the new replacement.
     */
    public void addErsetzung(String ersetzenVon, String ersetzenNach, String[] regeln) {
        Ersetzung ersetzung = new Ersetzung();
        ersetzung.ersatz = ersetzenNach;
        ersetzung.regeln = new int[regeln.length];
        for (int i = 0; i < regeln.length; i++) {
            ersetzung.regeln[i] = Integer.parseInt(regeln[i]);
        }
        addErsetzung(ersetzenVon, ersetzung);
    }

    /**
     * Returns a character replacement for a position within a character string.
     * The character replacement is always determined by which as many characters as possible in the output
     * string are replaced.
     * 
     * @param text
     *            Character string in which characters are to be replaced.
     * @param position
     *            Position within the character string at which characters are to be replaced.
     * @return String with which the characters at the specified position are to be replaced.
     */
    public String getErsetzung(String text, int position) {
        for (int laenge = maxKeyLaenge; laenge > 0; laenge--) {
            // Go to the next loop iteration when the examined character would exceed the text.
            if (position + laenge <= text.length()) {
                String sub = text.substring(position, position + laenge);
                List<Ersetzung> varianten = ersetzungen.get(sub);
                // Go to the next loop iteration when no possible replacement exists.
                if (varianten != null) {
                    for (Ersetzung ersetzung : varianten) {
                        if (isAnwendbareErsetzung(ersetzung, text, position, laenge)) {
                            laengeLetzteErsetzung = laenge;
                            altesZeichenLetzteErsetzung = text.substring(position, position + laenge);
                            return ersetzung.ersatz;
                        }
                    }
                }
            }
        }
        laengeLetzteErsetzung = 1;
        return "";
    }

    /**
     * Determines if 'ersetzung' is a valid character replacement at the given position in the given text.
     *
     * @param ersetzung
     *            Candidate for a replacement
     * @param text
     *            Character string in which characters are to be replaced.
     * @param position
     *            Position within the character string at which characters are to be replaced.
     * @param laenge
     *            Length of the substring.
     * @return 'true' if replacement is valid, otherwise 'false'
     */
    private boolean isAnwendbareErsetzung(Ersetzung ersetzung, String text, int position, int laenge) {
        return ersetzung.regeln.length == 0 || Arrays.stream(ersetzung.regeln)
            .anyMatch(r -> transformator.werteRegelAus(r, text, position, laenge));
    }

    /**
     * Returns the number of characters that would have been replaced by the last replacement found.
     * 
     * @return Number of replaced characters.
     */
    public int getLaengeLetzteErsetzung() {
        return laengeLetzteErsetzung;
    }

    /**
     * Returns the characters that would have been replaced by the last replacement found.
     *
     * @return Replaced Characters.
     */
    public String getAltesZeichenLetzteErsetzung() {
        return altesZeichenLetzteErsetzung;
    }

    private void addErsetzung(String ersetzenVon, Ersetzung ersetzung) {
        List<Ersetzung> varianten = ersetzungen.get(ersetzenVon);
        if (varianten == null) {
            varianten = new ArrayList<>();
            ersetzungen.put(ersetzenVon, varianten);
            if (maxKeyLaenge < ersetzenVon.length()) {
                maxKeyLaenge = ersetzenVon.length();
            }
        }
        varianten.add(ersetzung);
    }

}
