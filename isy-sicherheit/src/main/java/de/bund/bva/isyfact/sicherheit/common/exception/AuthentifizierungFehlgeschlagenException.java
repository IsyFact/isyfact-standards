package de.bund.bva.isyfact.sicherheit.common.exception;

import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;

/**
 * Wird geworfen, wenn der {@link AccessManager} die Authentifizierung abgelehnt hat.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class AuthentifizierungFehlgeschlagenException extends SicherheitTechnicalRuntimeException {

    /**
     * Serial Uid.
     */
    private static final long serialVersionUID = 2428584102863876150L;

    /**
     * Erstellt eine Exception mit AusnahmeId und Parametern mit denen der Fehlertext parametrisiert wird.
     * 
     * @param detailMessage
     *            Den oder die Parameter für den Fehlertext
     */
    public AuthentifizierungFehlgeschlagenException(String detailMessage) {
        super(SicherheitFehlerSchluessel.MSG_AUTHENTIFIZIERUNG_FEHLGESCHLAGEN, detailMessage);
    }

}
