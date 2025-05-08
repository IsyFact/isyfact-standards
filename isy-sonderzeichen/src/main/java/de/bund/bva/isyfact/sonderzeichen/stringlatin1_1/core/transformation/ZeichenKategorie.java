package de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation;

/**
 *  @deprecated This class is deprecated and will be removed in a future release.
 *  It is recommended to use {@link de.bund.bva.isyfact.sonderzeichen.dinspec91379} instead.
 */
@Deprecated
public final class ZeichenKategorie {
    
    public static final String LETTER = "LETTER";
    public static final String NUMBER = "NUMBER";
    public static final String OTHER = "OTHER";
    public static final String PUNCTUATION = "PUNCTUATION";
    public static final String SEPARATOR = "SEPARATOR";
    public static final String SYMBOL = "SYMBOL";
    public static final String ALLE = "ALLE";

    private ZeichenKategorie() {
    }

    public static String[] getAlleZeichenKategorien() {
        return new String[]{LETTER, NUMBER, OTHER, PUNCTUATION, SEPARATOR, SYMBOL, ALLE};
    }
}
