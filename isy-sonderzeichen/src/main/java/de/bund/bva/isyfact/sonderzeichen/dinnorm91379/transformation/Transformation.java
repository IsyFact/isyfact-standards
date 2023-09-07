package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

import java.util.List;

/**
 * Object containing a transformed text and the metadata of its transformation.
 */
public class Transformation {

    /** Text after transformation. */
    private String transformierterText;

    /** Metadata of the transformation. */
    private List<TransformationMetadaten> metadatenList;

    public Transformation() {
    }

    public Transformation(String transformierterText, List<TransformationMetadaten> metadatenList) {
        this.transformierterText = transformierterText;
        this.metadatenList = metadatenList;
    }

    public String getTransformierterText() {
        return transformierterText;
    }

    public void setTransformierterText(String transformierterText) {
        this.transformierterText = transformierterText;
    }

    public List<TransformationMetadaten> getMetadatenList() {
        return metadatenList;
    }

    public void setMetadatenList(List<TransformationMetadaten> metadatenList) {
        this.metadatenList = metadatenList;
    }
}
