package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.task.handler.FixedDateTimeHandler;
import de.bund.bva.isyfact.task.model.FixedDateTime;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.model.impl.FixedDateTimeImpl;

/**
 * Der FixedDateTimeHandler baut FixedDateTime-Instanzen auf.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class FixedDateTimeHandlerImpl implements FixedDateTimeHandler {
    private volatile ThreadLocal<Long> daysThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<Long> hoursThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<Long> minutesThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<Long> secondsThreadLocal = new ThreadLocal<>();

    /**
     *
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     */
    public FixedDateTimeHandlerImpl(long days, long hours, long minutes, long seconds) {
        this
                .setDays(days)
                .setHours(hours)
                .setMinutes(minutes)
                .setSeconds(seconds);

    }

    /**
     *
     * @param days
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setDays(long days) {
        this.daysThreadLocal.set(days);
        return this;
    }

    /**
     *
     * @param hours
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setHours(long hours) {
        this.hoursThreadLocal.set(hours);
        return this;
    }

    /**
     *
     * @param minutes
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setMinutes(long minutes) {
        this.minutesThreadLocal.set(minutes);
        return this;
    }

    /**
     *
     * @param seconds
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setSeconds(long seconds) {
        this.secondsThreadLocal.set(seconds);
        return this;
    }

    /**
     *
     * @return fixedDateTime
     */
    public synchronized FixedDateTime createFixedDateTime() {
        return new FixedDateTimeImpl(
                daysThreadLocal.get(),
                hoursThreadLocal.get(),
                minutesThreadLocal.get(),
                secondsThreadLocal.get());
    }


}
