package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.model.FixedDateTime;

/**
 * Der FixedDateTimeHandler baut FixedDateTime-Objekte.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface FixedDateTimeHandler {
    FixedDateTimeHandler setDays(long days);

    FixedDateTimeHandler setHours(long hours);

    FixedDateTimeHandler setMinutes(long minutes);

    FixedDateTimeHandler setSeconds(long seconds);

    FixedDateTime createFixedDateTime();
}
