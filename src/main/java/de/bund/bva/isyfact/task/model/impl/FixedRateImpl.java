package de.bund.bva.isyfact.task.model.impl;

import de.bund.bva.isyfact.task.model.FixedRate;

import java.time.Duration;
import java.time.Period;

/**
 * Eine CallableOperation enthält die Anweisungen, die erledigt werden sollen.
 * Diese werden in einer call-Methode implementiert.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class FixedRateImpl implements FixedRate {
    private volatile ThreadLocal<Duration> days = new ThreadLocal<>();
    private volatile ThreadLocal<Duration> hours = new ThreadLocal<>();
    private volatile ThreadLocal<Duration> minutes = new ThreadLocal<>();
    private volatile ThreadLocal<Duration> seconds = new ThreadLocal<>();

    /**
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     */
    public FixedRateImpl(
            long days,
            long hours,
            long minutes,
            long seconds) {
        this.days.set(Duration.ofDays(days));
        this.hours.set(Duration.ofHours(hours));
        this.minutes.set(Duration.ofMinutes(minutes));
        this.seconds.set(Duration.ofSeconds(seconds));
    }

    @Override
    public synchronized FixedRateImpl setDays(long days) {
        this.days.set(Duration.ofDays(days));
        return this;
    }

    @Override
    public synchronized FixedRateImpl setHours(long hours) {
        this.hours.set(Duration.ofHours(hours));
        return this;
    }

    @Override
    public synchronized FixedRateImpl setMinutes(long minutes) {
        this.minutes.set(Duration.ofMinutes(minutes));
        return this;
    }

    @Override
    public synchronized FixedRateImpl setSeconds(long seconds) {
        this.seconds.set(Duration.ofSeconds(seconds));
        return this;
    }

    @Override
    public synchronized long toNanos() {
        Duration total = days.get()
                .plus(hours.get())
                .plus(minutes.get())
                .plus(seconds.get());
        return total.toNanos();
    }
}
