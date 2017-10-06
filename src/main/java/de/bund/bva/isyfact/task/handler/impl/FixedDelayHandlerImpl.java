package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.task.handler.FixedDelayHandler;
import de.bund.bva.isyfact.task.handler.FixedRateHandler;
import de.bund.bva.isyfact.task.model.FixedDateTime;
import de.bund.bva.isyfact.task.model.impl.FixedDateTimeImpl;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

/**
 * Der FixedRateHandler baut FixedDateTime-Instanzen auf.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class FixedDelayHandlerImpl implements FixedDelayHandler {

    /**
     *
     * @return fixedDateTime
     */
    @Override
    public synchronized FixedDateTime getFixedDelay(String id, Konfiguration konfiguration) {
        long fixedDelayDays = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYDAYS);
        long fixedDelayHours = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYHOURS);
        long fixedDelayMinutes = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYMINUTES);
        long fixedDelaySeconds = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYSECONDS);

        return new FixedDateTimeImpl(
                fixedDelayDays,
                fixedDelayHours,
                fixedDelayMinutes,
                fixedDelaySeconds);
    }
}
