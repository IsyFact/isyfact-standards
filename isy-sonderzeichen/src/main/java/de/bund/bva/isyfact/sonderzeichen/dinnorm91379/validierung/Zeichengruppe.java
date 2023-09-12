package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung;

/**
 * Class representing a character group as defined by DIN Norm 91379.
 */
public enum Zeichengruppe {

    /**
     * Zeichengruppe: Latein. Buchstaben (normativ).
     * <p>
     * Character Group: Latin letters (normative).
     */
    N_LATEIN("Latein. Buchstaben (normativ)", "/resources/tabellen/dinnorm91379/zeichengruppen/latein_buchstaben_normativ.kat"),
    /**
     * Zeichengruppe: Nicht-Buchstaben N1 (normativ).
     * <p>
     * Character Group: Non-letters N1 (normative).
     */
    N1("Nicht-Buchstaben N1 (normativ)", "/resources/tabellen/dinnorm91379/zeichengruppen/nicht-buchstaben_N1_normativ.kat"),
    /**
     * Zeichengruppe: Nicht-Buchstaben N2 (normativ).
     * <p>
     * Character Group: Non-letters N2 (normative).
     */
    N2("Nicht-Buchstaben N2 (normativ)", "/resources/tabellen/dinnorm91379/zeichengruppen/nicht-buchstaben_N2_normativ.kat"),
    /**
     * Zeichengruppe: Nicht-Buchstaben N3 (normativ).
     * <p>
     * Character Group: Non-letters N3 (normative).
     */
    N3("Nicht-Buchstaben N3 (normativ)", "/resources/tabellen/dinnorm91379/zeichengruppen/nicht-buchstaben_N3_normativ.kat"),
    /**
     * Zeichengruppe: Nicht-Buchstaben N4 (normativ).
     * <p>
     * Character Group: Non-letters N4 (normative).
     */
    N4("Nicht-Buchstaben N4 (normativ)", "/resources/tabellen/dinnorm91379/zeichengruppen/nicht-buchstaben_N4_normativ.kat"),
    /**
     * Zeichengruppe: Komb. diakrit. Zeichen (normativ).
     * <p>
     * Character Group: Combined diacritic marks (normative).
     */
    N_KOMB_DIAKRIT("Komb. diakrit. Zeichen (normativ)", "/resources/tabellen/dinnorm91379/zeichengruppen/komb_diakrit_zeichen_normativ.kat"),
    /**
     * Zeichengruppe: Griech. Buchstaben (erweitert).
     * <p>
     * Character Group: Greek letters (extended).
     */
    E_GRIECH("Griech. Buchstaben (erweitert)", "/resources/tabellen/dinnorm91379/zeichengruppen/griech_buchstaben_erweitert.kat"),
    /**
     * Zeichengruppe: Kyrill. Buchstaben (erweitert).
     * <p>
     * Character Group: Cyrillic letters (extended).
     */
    E_KYRILL("Kyrill. Buchstaben (erweitert)", "/resources/tabellen/dinnorm91379/zeichengruppen/kyrill_buchstaben_erweitert.kat"),
    /**
     * Zeichengruppe: Nicht-Buchstaben E1 (erweitert).
     * <p>
     * Character Group: Non-letters E1 (extended).
     */
    E1("Nicht-Buchstaben E1 (erweitert)", "/resources/tabellen/dinnorm91379/zeichengruppen/nicht-buchstaben_E1_erweitert.kat");

    /**
     * Name of the character group.
     */
    private final String name;

    /**
     * Path where the category file containing the characters is located.
     */
    private final String pfad;

    Zeichengruppe(String name, String pfad) {
        this.name = name;
        this.pfad = pfad;
    }

    /**
     * Returns the name of this character group.
     *
     * @return the name of this character group
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path to the category file containing the characters of this group.
     *
     * @return path to the category file
     */
    public String getPfad() {
        return pfad;
    }

    /**
     * String representation of this character group. Identical to calling {@link #getName()}.
     *
     * @return string representation of the character group.
     */
    @Override
    public String toString() {
        return name;
    }

}
