package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.pliscommon.exception.FehlertextProvider;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.util.exception.MessageSourceFehlertextProvider;

/**
 * Die Exception wird geworfen, wenn eine Operations-Instanz nicht erzeugt werden konnte.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class CreateOperationInstanceException extends PlisTechnicalException {

    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new MessageSourceFehlertextProvider();

    public CreateOperationInstanceException(String operationName, Throwable cause) {
        super(FehlerSchluessel.OPERATION_KONNTE_NICHT_INSTANZIIERT_WERDEN, cause, FEHLERTEXT_PROVIDER, operationName);
    }
}
