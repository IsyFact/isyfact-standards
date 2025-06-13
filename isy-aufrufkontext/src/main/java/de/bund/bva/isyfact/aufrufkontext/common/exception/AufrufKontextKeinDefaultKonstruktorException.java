package de.bund.bva.isyfact.aufrufkontext.common.exception;

/**
 * Der übergebene Parameter ist null oder leer.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public class AufrufKontextKeinDefaultKonstruktorException extends AufrufKontextTechnicalRuntimeException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -783395492626853012L;

    /**
     * Der übergebene Parameter ist null oder leer.
     */
    public AufrufKontextKeinDefaultKonstruktorException() {
        super(AusnahmeId.AUFRUFKONTEXT_KEIN_DEFAULT_KOSTRUKTOR, new String[0]);
    }

}
