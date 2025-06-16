package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

public final class ZeichenKategorie {

    /**
     * Character of the category 'letter'.
     */
    public static final String LETTER = "LETTER";

    /**
     * Character of the category 'number'.
     */
    public static final String NUMBER = "NUMBER";

    /**
     * Character of the category 'other'.
     */
    public static final String OTHER = "OTHER";

    /**
     * Character of the category 'punctuation'.
     */
    public static final String PUNCTUATION = "PUNCTUATION";

    /**
     * Character of the category 'separator'.
     */
    public static final String SEPARATOR = "SEPARATOR";

    /**
     * Character of the category 'symbol'.
     */
    public static final String SYMBOL = "SYMBOL";

    /**
     * Character of the category 'all'.
     */
    public static final String ALLE = "ALLE";

    private ZeichenKategorie() {
    }

    public static String[] getAlleZeichenKategorien() {
        return new String[]{LETTER, NUMBER, OTHER, PUNCTUATION, SEPARATOR, SYMBOL, ALLE};
    }
}
