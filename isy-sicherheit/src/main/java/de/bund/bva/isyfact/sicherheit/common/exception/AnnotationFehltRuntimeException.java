package de.bund.bva.isyfact.sicherheit.common.exception;

import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Diese Annotation wird geworfen, wenn eine erwartete Annotation nicht gefunden wurde.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class AnnotationFehltRuntimeException extends SicherheitTechnicalRuntimeException {

    /** Serial Version UId. */
    private static final long serialVersionUID = 0L;

    /**
     * Erstellt die Exception mit der AusnahmeId und Paramtern für den Fehlertext.
     * 
     * @param parameter
     *            Die Parameter.
     */
    public AnnotationFehltRuntimeException(String... parameter) {
        super(SicherheitFehlerSchluessel.MSG_ANNOTATION_FEHLT_AN_METHODE, parameter);
    }
}
