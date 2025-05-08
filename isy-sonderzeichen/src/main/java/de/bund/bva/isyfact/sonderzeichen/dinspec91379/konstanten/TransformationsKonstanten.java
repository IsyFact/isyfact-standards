package de.bund.bva.isyfact.sonderzeichen.dinspec91379.konstanten;

/**
 * This class contains constants for the resources of the respective transformation table.
 *
 *  @deprecated This class is deprecated and will be removed in a future release.
 *  It is recommended to use {@link de.bund.bva.isyfact.sonderzeichen.dinnorm91379} instead.
 */
@Deprecated
public final class TransformationsKonstanten {

    /** The string that describes the characters to remove. */
    public static final String ZEICHEN_ENTFERNE = "";

    /** The entry in a transformation table that describes the standard case. */
    public static final String EINTRAG_STANDARD = "standard";

    /** String representing the space. */
    public static final String STRING_SPACE = " ";

    /** The path to the transformation table identical. */
    public static final String TRANSFORMATIONS_TABELLE_IDENTISCH =
            "/resources/tabellen/transformation_dinspec91379.transform";

    /** The path to the table identical category. */
    public static final String KATEGORIE_TABELLE = "/resources/tabellen/kategorie_dinspec91379.kat";

    /** The path to the transformation transcription table. */
    public static final String TRANSFORMATIONS_TABELLE_TRANSKRIPTION =
            "/resources/tabellen/transformation_din91379_transkription.transform";

    /**
     * The path to the legacy transcription table, transforming DIN SPEC 91379 characters to StringLatin1.1.
     */
    public static final String TRANSFORMATIONS_TABELLE_LEGACY =
            "/resources/tabellen/transformation_dinspec91379_zu_1_1.transform";

    /**
     * The path to the search form (Suchform) table, transforming normative DIN SPEC 91379 characters into the basic
     * letters of the Latin alphabet (A-Z).
     */
    public static final String TRANSFORMATIONS_TABELLE_SUCHFORM =
            "/resources/tabellen/transformation_dinspec91379_zu_suchform.transform";

    private TransformationsKonstanten() {
    }

}
