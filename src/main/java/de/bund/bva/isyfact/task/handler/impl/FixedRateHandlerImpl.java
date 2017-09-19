package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.task.handler.FixedRateHandler;
import de.bund.bva.isyfact.task.model.FixedRate;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.model.impl.FixedRateImpl;

/**
 * Der FixedRateHandler baut FixedRate-Instanzen auf.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class FixedRateHandlerImpl implements FixedRateHandler {
    private volatile ThreadLocal<String> days = new ThreadLocal<>();
    private volatile ThreadLocal<String> hours = new ThreadLocal<>();
    private volatile ThreadLocal<String> minutes = new ThreadLocal<>();
    private volatile ThreadLocal<String> seconds = new ThreadLocal<>();

    /**
     *
     * @param taskData
     */
    public FixedRateHandlerImpl(TaskData taskData) {
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
    public synchronized FixedRateHandlerImpl setDays(String days) {
        this.days.set(days);
        return this;
    }

    /**
     *
     * @param hours
     * @return
     */
    public synchronized FixedRateHandlerImpl setHours(String hours) {
        this.hours.set(hours);
        return this;
    }

    /**
     *
     * @param minutes
     * @return
     */
    public synchronized FixedRateHandlerImpl setMinutes(String minutes) {
        this.minutes.set(minutes);
        return this;
    }

    /**
     *
     * @param seconds
     * @return
     */
    public synchronized FixedRateHandlerImpl setSeconds(String seconds) {
        this.seconds.set(seconds);
        return this;
    }

    /**
     *
     * @return
     */
    public synchronized FixedRate createFixedRate() {
        String sDays = this.days.get();
        long days = (sDays == null) ? 0 : Long.parseLong(sDays);

        String sHours = this.hours.get();
        long hours = (sHours == null) ? 0 : Long.parseLong(sHours);

        String sMinutes = this.minutes.get();
        long minutes = (sMinutes == null) ? 0 : Long.parseLong(sMinutes);

        String sSeconds = this.seconds.get();
        long seconds = (sSeconds == null) ? 0 : Long.parseLong(sSeconds);

        return new FixedRateImpl(
                days,
                hours,
                minutes,
                seconds);
    }


}
