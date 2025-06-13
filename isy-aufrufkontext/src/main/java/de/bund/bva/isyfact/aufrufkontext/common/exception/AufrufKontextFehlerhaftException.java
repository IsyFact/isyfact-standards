package de.bund.bva.isyfact.aufrufkontext.common.exception;

/**
 * Der übergebene Parameter ist null oder leer.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security (OAuth2) and the isy-security library.
 */
@Deprecated
public class AufrufKontextFehlerhaftException extends AufrufKontextTechnicalRuntimeException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8687916866752337910L;

    /**
     * Der übergebene Parameter ist null oder leer.
     */
    public AufrufKontextFehlerhaftException() {
        super(AusnahmeId.UEBERGEBENER_PARAMETER_FALSCH, new String[] { "AufrufKontext" });
    }

}
