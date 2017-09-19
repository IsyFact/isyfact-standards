package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.task.handler.ExecutorHandler;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte.DEFAULT_INITIAL_NUMBER_OF_THREADS;

/**
 * Der ExecutorHandler erstellt Executor-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class ExecutorHandlerImpl implements ExecutorHandler {
    @Override
    public ScheduledExecutorService createScheduledExecutorService(
            int initialNumberOfThreads
    ) {
        return Executors.newScheduledThreadPool(initialNumberOfThreads);
    }
}
