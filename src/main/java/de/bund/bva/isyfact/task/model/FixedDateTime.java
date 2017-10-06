package de.bund.bva.isyfact.task.model;

/**
 * Der Typ FixedDateTime kapselt den Zeitpunkt für einen FixedRate und einen FixedDelay.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface FixedDateTime {
    FixedDateTime setDays(long days);

    FixedDateTime setHours(long hours);

    FixedDateTime setMinutes(long minutes);

    FixedDateTime setSeconds(long seconds);

    long toNanos();
}
