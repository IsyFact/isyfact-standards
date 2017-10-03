package de.bund.bva.isyfact.task.model;

public interface FixedDateTime {
    FixedDateTime setDays(long days);

    FixedDateTime setHours(long hours);

    FixedDateTime setMinutes(long minutes);

    FixedDateTime setSeconds(long seconds);

    long toNanos();
}
