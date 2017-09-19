package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.model.FixedRate;

/**
 * Der FixedRateHandler baut FixedRate-Objekte.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface FixedRateHandler {
    FixedRateHandler setDays(String days);

    FixedRateHandler setHours(String hours);

    FixedRateHandler setMinutes(String minutes);

    FixedRateHandler setSeconds(String seconds);

    FixedRate createFixedRate();
}
