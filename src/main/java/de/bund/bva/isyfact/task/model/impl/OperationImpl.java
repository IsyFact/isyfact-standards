package de.bund.bva.isyfact.task.model.impl;

import de.bund.bva.isyfact.task.model.FixedDateTime;
import de.bund.bva.isyfact.task.model.Operation;

import java.time.LocalDateTime;

/**
 * Eine Operation enthält die Anweisungen, die erledigt werden sollen.
 * Diese werden in einer call-Methode oder in einer run-Methode implementiert.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public abstract class OperationImpl implements Operation {
    private volatile ThreadLocal<String> idThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<String> hostNameThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<LocalDateTime> executionDateTimeThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<FixedDateTime> fixedRateThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<FixedDateTime> fixedDelayThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<String> resultThreadLocal
            = new ThreadLocal<>();

    public OperationImpl() {}

    @Override
    public String getId() {
        return idThreadLocal.get();
    }

    @Override
    public void setId(String id) {
        this.idThreadLocal.set(id);
    }

    @Override
    public String getHostName() {
        return hostNameThreadLocal.get();
    }

    @Override
    public void setHostName(String hostName) {
        this.hostNameThreadLocal.set(hostName);
    }

    @Override
    public synchronized LocalDateTime getExecutionDateTime() {
        return this.executionDateTimeThreadLocal.get();
    }

    @Override
    public synchronized void setExecutionDateTime(LocalDateTime executionDateTime) {
        this.executionDateTimeThreadLocal.set(executionDateTime);
    }

    @Override
    public FixedDateTime getFixedRate() {
        return fixedRateThreadLocal.get();
    }

    @Override
    public void setFixedRate(FixedDateTime fixedDateTime) {
        this.fixedRateThreadLocal.set(fixedDateTime);
    }

    @Override
    public FixedDateTime getFixedDelay() {
        return fixedDelayThreadLocal.get();
    }

    @Override
    public void setFixedDelay(FixedDateTime fixedDelay) {
        this.fixedDelayThreadLocal.set(fixedDelay);
    }

    @Override
    public String get() {
        return resultThreadLocal.get();
    }

    @Override
    public void set(String result) {
        resultThreadLocal.set(result);
    }
}
