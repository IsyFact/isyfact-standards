package de.bund.bva.isyfact.bridges.integration.sst;

import de.bund.bva.isyfact.exception.service.TechnicalToException;

public class IsyTestToException extends TechnicalToException {

    /**
     * Einziger Konstruktor. Es ist notwendig die Nachricht direkt zu übergeben, da diese nicht
     * nachträglich gesetzt werden kann. Zusätzlich nimmt dieser Konstrukt noch die Ausnahme-ID und
     * die Unique-ID entgegen.
     *
     * @param message    Fehlertext.
     * @param ausnahmeId AusnahmeID
     * @param uniqueId
     */
    public IsyTestToException(String message, String ausnahmeId, String uniqueId) {
        super(message, ausnahmeId, uniqueId);
    }
}
