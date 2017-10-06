package de.bund.bva.isyfact.task.handler.impl;

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
public class FixedRateHandlerImpl implements FixedRateHandler {

    /**
     *
     * @return fixedDateTime
     */
    public synchronized FixedDateTime getFixedRate(String id, Konfiguration konfiguration) {
        long fixedRateDays = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATEDAYS);
        long fixedRateHours = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATEHOURS);
        long fixedRateMinutes = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATEMINUTES);
        long fixedRateSeconds = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATESECONDS);

        return new FixedDateTimeImpl(
                fixedRateDays,
                fixedRateHours,
                fixedRateMinutes,
                fixedRateSeconds);
    }
}
