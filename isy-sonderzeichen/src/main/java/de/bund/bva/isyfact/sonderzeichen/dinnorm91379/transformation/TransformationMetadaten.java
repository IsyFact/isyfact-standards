package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

/**
 * Metadata of a transformation.
 */
public class TransformationMetadaten {

    /** Character before transformation. */
    private String altesZeichen;

    /** Codepoints before transformation. */
    private String alteCodepoints;

    /** Character after transformation. */
    private String neuesZeichen;

    /** Codepoints after transformation. */
    private String neueCodepoints;

    /** Character's postion before transformation. */
    private int altePosition;

    /** Character's postion after transformation. */
    private int neuePosition;

    public TransformationMetadaten(String altesZeichen, String alteCodepoints, String neuesZeichen,
        String neueCodepoints, int altePosition, int neuePosition) {
        this.altesZeichen = altesZeichen;
        this.alteCodepoints = alteCodepoints;
        this.neuesZeichen = neuesZeichen;
        this.neueCodepoints = neueCodepoints;
        this.altePosition = altePosition;
        this.neuePosition = neuePosition;
    }

    @Override
    public String toString() {
        return "{" + "altesZeichen='" + altesZeichen + '\'' + ", alteCodepoints='"
            + alteCodepoints + '\'' + ", neuesZeichen='" + neuesZeichen + '\'' + ", neueCodepoints='"
            + neueCodepoints + '\'' + ", altePosition=" + altePosition + ", neuePosition=" + neuePosition
            + '}';
    }

    public String getAltesZeichen() {
        return altesZeichen;
    }

    public void setAltesZeichen(String altesZeichen) {
        this.altesZeichen = altesZeichen;
    }

    public String getAlteCodepoints() {
        return alteCodepoints;
    }

    public void setAlteCodepoints(String alteCodepoints) {
        this.alteCodepoints = alteCodepoints;
    }

    public String getNeuesZeichen() {
        return neuesZeichen;
    }

    public void setNeuesZeichen(String neuesZeichen) {
        this.neuesZeichen = neuesZeichen;
    }

    public String getNeueCodepoints() {
        return neueCodepoints;
    }

    public void setNeueCodepoints(String neueCodepoints) {
        this.neueCodepoints = neueCodepoints;
    }

    public int getAltePosition() {
        return altePosition;
    }

    public void setAltePosition(int altePosition) {
        this.altePosition = altePosition;
    }

    public int getNeuePosition() {
        return neuePosition;
    }

    public void setNeuePosition(int neuePosition) {
        this.neuePosition = neuePosition;
    }
}
