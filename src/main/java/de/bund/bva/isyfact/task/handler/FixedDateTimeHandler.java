package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.model.FixedDateTime;

/**
 * Der FixedDateTimeHandler baut FixedDateTime-Objekte.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface FixedDateTimeHandler {
    FixedDateTimeHandler setDays(String days);

    FixedDateTimeHandler setHours(String hours);

    FixedDateTimeHandler setMinutes(String minutes);

    FixedDateTimeHandler setSeconds(String seconds);

    FixedDateTime createFixedDateTime();
}
