package de.bund.bva.isyfact.sonderzeichen.dinspec91379.core.transformation.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.sonderzeichen.dinspec91379.konstanten.TransformationsKonstanten;

public class TranskriptionTransformator extends  AbstractTransformator {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TranskriptionTransformator.class);;

    @Override
    protected String getStandardTransformationsTabelle() {
        return TransformationsKonstanten.TRANSFORMATIONS_TABELLE_TRANSCRIPTION;
    }

    @Override
    protected String getKategorieTabelle() {
        return TransformationsKonstanten.KATEGORIE_TABELLE;
    }

    @Override
    protected IsyLogger getLogger() {
        return LOG;
    }

    @Override
    public String transformiere(String zeichenkette, int maximaleLaenge) {
        throw new UnsupportedOperationException("Diese Funktion wird nicht unterstützt.");
    }
}
