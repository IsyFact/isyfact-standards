package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.pliscommon.exception.FehlertextProvider;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;
import de.bund.bva.pliscommon.util.exception.MessageSourceFehlertextProvider;

public class TaskKonfigurationInvalidException extends PlisTechnicalRuntimeException {

    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new MessageSourceFehlertextProvider();

    public TaskKonfigurationInvalidException(String nachricht) {
        super(FehlerSchluessel.TASK_KONFIGURATION_UNGUELTIG, FEHLERTEXT_PROVIDER, nachricht);
    }
}
