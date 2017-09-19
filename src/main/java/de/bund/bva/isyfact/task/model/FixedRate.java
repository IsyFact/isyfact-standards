package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.model.impl.FixedRateImpl;

public interface FixedRate {
    FixedRateImpl setDays(long days);

    FixedRateImpl setHours(long hours);

    FixedRateImpl setMinutes(long minutes);

    FixedRateImpl setSeconds(long seconds);

    long toNanos();
}
