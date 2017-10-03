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
    private volatile ThreadLocal<String> daysThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<String> hoursThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<String> minutesThreadLocal = new ThreadLocal<>();
    private volatile ThreadLocal<String> secondsThreadLocal = new ThreadLocal<>();

    /**
     *
     * @param taskData
     */
    public FixedDateTimeHandlerImpl(TaskData taskData) {
        this
                .setDays(taskData.getDays())
                .setHours(taskData.getHours())
                .setMinutes(taskData.getMinutes())
                .setSeconds(taskData.getSeconds());

    }

    /**
     *
     * @param days
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setDays(String days) {
        this.daysThreadLocal.set(days);
        return this;
    }

    /**
     *
     * @param hours
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setHours(String hours) {
        this.hoursThreadLocal.set(hours);
        return this;
    }

    /**
     *
     * @param minutes
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setMinutes(String minutes) {
        this.minutesThreadLocal.set(minutes);
        return this;
    }

    /**
     *
     * @param seconds
     * @return
     */
    public synchronized FixedDateTimeHandlerImpl setSeconds(String seconds) {
        this.secondsThreadLocal.set(seconds);
        return this;
    }

    /**
     *
     * @return
     */
    public synchronized FixedDateTime createFixedDateTime() {
        String sDays = this.daysThreadLocal.get();
        long days = (sDays == null) ? 0 : Long.parseLong(sDays);

        String sHours = this.hoursThreadLocal.get();
        long hours = (sHours == null) ? 0 : Long.parseLong(sHours);

        String sMinutes = this.minutesThreadLocal.get();
        long minutes = (sMinutes == null) ? 0 : Long.parseLong(sMinutes);

        String sSeconds = this.secondsThreadLocal.get();
        long seconds = (sSeconds == null) ? 0 : Long.parseLong(sSeconds);

        return new FixedDateTimeImpl(
                days,
                hours,
                minutes,
                seconds);
    }


}
