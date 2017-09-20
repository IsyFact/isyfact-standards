package de.bund.bva.isyfact.task.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.TaskScheduler2;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte.DEFAULT_INITIAL_NUMBER_OF_THREADS;

/**
 * Task Scheduler auf Basis der Java Concurrent API. Wird als Spring Bean definiert.
 */
public class JavaUtilConcurrentTaskScheduler
    implements TaskScheduler2, ApplicationListener<ContextClosedEvent> {

    private static final IsyLogger LOGGER = IsyLoggerFactory.getLogger(JavaUtilConcurrentTaskScheduler.class);

    private final ApplicationContext applicationContext;

    private final ScheduledExecutorService executorService;

    private final Map<String, ScheduledFuture<?>> scheduledFutures;

    private final CompletionWatchdog completionWatchdog;

    public JavaUtilConcurrentTaskScheduler(Konfiguration konfiguration,
        ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        executorService = Executors.newScheduledThreadPool(konfiguration
            .getAsInteger(KonfigurationSchluessel.INITIAL_NUMBER_OF_THREADS,
                DEFAULT_INITIAL_NUMBER_OF_THREADS));
        scheduledFutures = new HashMap<>();
        completionWatchdog = new CompletionWatchdog();
    }

    @Override
    public void fuehreKonfigurierteTasksAus() {
        Arrays.stream(applicationContext.getBeanNamesForType(Task.class)).forEach(this::starteTask);
        new Thread(completionWatchdog).start();
    }

    private void starteTask(String taskName) {
        // TODO Tasks sind Spring Beans und werden bei Erstellung komplett konfiguiert!
        Task task = applicationContext.getBean(taskName, Task.class);
        switch (task.getAusfuehrungsplan()) {
        case FIXED_RATE:
            scheduledFutures.put(taskName, scheduleAtFixedRate(task));
        case FIXED_DELAY:
            scheduledFutures.put(taskName, scheduleWithFixedDelay(task));
            break;
        case FIXED_TIME:
            // TODO
        case ONCE:
            schedule(task);
            break;
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Task task) {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Task task) {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Task task) {
        return null;
    }


    private class CompletionWatchdog implements Runnable {

        boolean stop = false;

        @Override
        public void run() {
            while (!stop) {
                try {
                    // Entkoppelt Abfrage und Neustart der Tasks (sonst concurrent modification).
                    List<String> tasksNeuStarten = new ArrayList<>();

                    for (Map.Entry<String, ScheduledFuture<?>> entry : scheduledFutures.entrySet()) {
                        if (entry.getValue().isCancelled()) {
                            try {
                                entry.getValue().get();
                            } catch (CancellationException e) {
                                LOGGER.info(LogKategorie.JOURNAL, "ISYTA99999", "Task wurde abgebrochen.", e);
                            } catch (ExecutionException e) {
                                LOGGER.warn("ISYTA99999", "Task wurde fehlerhaft beendet.", e.getCause());
                                tasksNeuStarten.add(entry.getKey());
                            }
                        }
                    }
                    tasksNeuStarten.forEach(JavaUtilConcurrentTaskScheduler.this::starteTask);
                    // TODO Konfigurierbar machen!
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    stop = true;
                }
            }

        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        executorService.shutdownNow();
        completionWatchdog.stop = true;
    }
}
