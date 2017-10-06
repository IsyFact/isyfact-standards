package de.bund.bva.isyfact.task.model.impl;

import de.bund.bva.isyfact.task.model.FixedDateTime;

import java.time.Duration;

/**
 * Der Typ FixedDateTime kapselt den Zeitpunkt für einen FixedRate und einen FixedDelay.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class FixedDateTimeImpl implements FixedDateTime {
    private volatile ThreadLocal<Duration> daysThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<Duration> hoursThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<Duration> minutesThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<Duration> secondsThreadLocal = new ThreadLocal<>();

    /**
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     */
    public FixedDateTimeImpl(
            long days,
            long hours,
            long minutes,
            long seconds) {
        this.daysThreadLocal.set(Duration.ofDays(days));
        this.hoursThreadLocal.set(Duration.ofHours(hours));
        this.minutesThreadLocal.set(Duration.ofMinutes(minutes));
        this.secondsThreadLocal.set(Duration.ofSeconds(seconds));
    }

    @Override
    public synchronized FixedDateTime setDays(long days) {
        this.daysThreadLocal.set(Duration.ofDays(days));
        return this;
    }

    @Override
    public synchronized FixedDateTime setHours(long hours) {
        this.hoursThreadLocal.set(Duration.ofHours(hours));
        return this;
    }

    @Override
    public synchronized FixedDateTime setMinutes(long minutes) {
        this.minutesThreadLocal.set(Duration.ofMinutes(minutes));
        return this;
    }

    @Override
    public synchronized FixedDateTime setSeconds(long seconds) {
        this.secondsThreadLocal.set(Duration.ofSeconds(seconds));
        return this;
    }

    @Override
    public synchronized long toNanos() {
        Duration total = daysThreadLocal.get()
                .plus(hoursThreadLocal.get())
                .plus(minutesThreadLocal.get())
                .plus(secondsThreadLocal.get());
        return total.toNanos();
    }
}
