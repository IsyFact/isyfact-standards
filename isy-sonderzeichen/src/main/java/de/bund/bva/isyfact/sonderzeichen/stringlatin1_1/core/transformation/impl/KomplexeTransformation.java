package de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.Transformator;

/**
 * This class maps transformations in which more than one character is transformed or in which special
 * conditions must be met.
 *
 *  @deprecated This class is deprecated and will be removed in a future release.
 *  It is recommended to use {@link de.bund.bva.isyfact.sonderzeichen.dinspec91379} instead.
 */
@Deprecated
public class KomplexeTransformation {

    /**
     * This class describes a possible replacement with the rules that may be necessary for this.
     */
    private class Ersetzung {

        /**
         * The numbers of the rules that if met, the replacement can be applied.
         *
         * If more than one rule is specified, the replacement is used if at least one of them is met.
         * If no rule is given, the replacement can always be used.
         */
        int[] regeln = new int[0];

        /** The string to replace the original characters with. */
        String ersatz;
    }

    /** The transformer to which the transformation is assigned. */
    private Transformator transformator;

    /** Contains all possible replacements that begin with a specific character. */
    private Map ersetzungen = new HashMap();

    /** The length of the longest character combination to replace. */
    private int maxKeyLaenge = 0;

    /** Number of characters that would have been replaced with the last character replacement determined. */
    private int laengeLetzteErsetzung = 0;

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
     *
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
            if (position + laenge > text.length()) {
                continue;
            }
            String sub = text.substring(position, position + laenge);
            List varianten = (List) ersetzungen.get(sub);
            if (varianten == null) {
                continue;
            }
            for (int i = 0; i < varianten.size(); i++) {
                Ersetzung ersetzung = (Ersetzung) varianten.get(i);
                if (ersetzung.regeln.length > 0) {
                    for (int regel = 0; regel < ersetzung.regeln.length; regel++) {
                        if (transformator.werteRegelAus(ersetzung.regeln[regel], text, position, laenge)) {
                            laengeLetzteErsetzung = laenge;
                            return ersetzung.ersatz;
                        }
                    }
                } else {
                    laengeLetzteErsetzung = laenge;
                    return ersetzung.ersatz;
                }
            }
        }
        laengeLetzteErsetzung = 1;
        return "";
    }

    /**
     * Returns the number of characters that would have been replaced by the last replacement found.
     *
     * @return Number of replaced characters.
     */
    public int getLaengeLetzteErsetzung() {
        return laengeLetzteErsetzung;
    }

    private void addErsetzung(String ersetzenVon, Ersetzung ersetzung) {
        List varianten = (List) ersetzungen.get(ersetzenVon);
        if (varianten == null) {
            varianten = new ArrayList();
            ersetzungen.put(ersetzenVon, varianten);
            if (maxKeyLaenge < ersetzenVon.length()) {
                maxKeyLaenge = ersetzenVon.length();
            }
        }
        varianten.add(ersetzung);
    }

}
