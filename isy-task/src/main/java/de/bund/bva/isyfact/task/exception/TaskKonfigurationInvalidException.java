package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.util.exception.MessageSourceFehlertextProvider;

public class TaskKonfigurationInvalidException extends TechnicalRuntimeException {

    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new MessageSourceFehlertextProvider();

    public TaskKonfigurationInvalidException(String nachricht) {
        super(FehlerSchluessel.TASK_KONFIGURATION_UNGUELTIG, FEHLERTEXT_PROVIDER, nachricht);
    }
}
