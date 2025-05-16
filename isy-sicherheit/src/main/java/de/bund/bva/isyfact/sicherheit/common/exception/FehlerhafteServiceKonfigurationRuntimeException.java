package de.bund.bva.isyfact.sicherheit.common.exception;

import de.bund.bva.isyfact.sicherheit.annotation.Gesichert;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Diese Exception wird geworfen, wenn eine Methode durch eine
 * {@link Gesichert}-Annotation gesichert wurde, aber die
 * erforderlichen Rechte in der Annotation nicht angegeben wurden.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class FehlerhafteServiceKonfigurationRuntimeException extends SicherheitTechnicalRuntimeException {

    /** Serial Version UId. */
    private static final long serialVersionUID = -3537929251814846397L;

    /**
     * Erstellt die Exception mit der AusnahmeId und Paramtern für den Fehlertext.
     */
    public FehlerhafteServiceKonfigurationRuntimeException() {
        super(SicherheitFehlerSchluessel.MSG_TAG_KONFIGURATION_FEHLERHAFT, new String[0]);
    }
}
