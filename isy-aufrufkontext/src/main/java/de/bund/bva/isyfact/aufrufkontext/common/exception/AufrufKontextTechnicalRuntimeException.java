package de.bund.bva.isyfact.aufrufkontext.common.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Für Exceptions, die durch den AufrufKontext entstehen können.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public abstract class AufrufKontextTechnicalRuntimeException extends TechnicalRuntimeException {
    /**
     * Serial Version Id.
     */
    private static final long serialVersionUID = 8442875838190661417L;

    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new AufrufKontextFehlertextProvider();

    protected AufrufKontextTechnicalRuntimeException(AusnahmeId ausnahmeId, String... parameter) {
        super(ausnahmeId.getCode(), FEHLERTEXT_PROVIDER, parameter);
    }

}
