package de.bund.bva.isyfact.task.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.TaskScheduler;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.handler.TaskHandler;
import de.bund.bva.isyfact.task.handler.impl.TaskHandlerImpl;
import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte.DEFAULT_INITIAL_NUMBER_OF_THREADS;

/**
 * Der TaskScheduler bietet die Möglichkeit, dass Tasks zu bestimmten Zeitpunkten ausgeführt werden können.
 *
 * Intern arbeitet TaskScheduler mit Thread-Local-gesicherten ScheduledExecutorServices, sodass auch
 * ein TaskScheduler Thread-sicher von mehreren Threads Thread-sicher durchlaufen werden kann.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class TaskSchedulerImpl implements TaskScheduler, ApplicationContextAware, Runnable {
    private volatile ThreadLocal<Konfiguration> konfiguration = new ThreadLocal<>();

    private volatile ThreadLocal<LocalDateTime> startTime = new ThreadLocal<>();

    private volatile ThreadLocal<ScheduledExecutorService> scheduledExecutorService = new ThreadLocal<>();

    private final Map<String, TaskRunner> tasks = new HashMap<>();

    private final Map<String, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskSchedulerImpl.class);

    private final AtomicLong counter = new AtomicLong();

    private ApplicationContext applicationContext;

    private boolean stop = false;

    /**
     * @param konfiguration
     */
    public TaskSchedulerImpl(Konfiguration konfiguration) {
        this.konfiguration.set(konfiguration);
        this.startTime.set(LocalDateTime.now());

        int initialNumberOfThreads = DEFAULT_INITIAL_NUMBER_OF_THREADS;
        if (konfiguration != null) {
            initialNumberOfThreads =
                this.konfiguration.get().getAsInteger(KonfigurationSchluessel.INITIAL_NUMBER_OF_THREADS);
        }

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(initialNumberOfThreads);

        this.scheduledExecutorService.set(executorService);
    }

    @Override
    public void starteKonfigurierteTasks() {
        TaskHandler taskHandler = new TaskHandlerImpl();

        for (String taskId : applicationContext.getBeanNamesForType(Task.class)) {
            try {
                addTask(taskHandler.createTask(taskId, konfiguration.get(), applicationContext));
            } catch (HostNotApplicableException e) {
                // TODO INFO-LOG
            }
        }

        start();
    }

    public void addTask(TaskRunner taskRunner) {
        tasks.put(taskRunner.getId(), taskRunner);
    }

    @Override
    public void start() {
        for (TaskRunner taskRunner : tasks.values()) {
            ScheduledFuture<?> scheduledFuture = null;
            switch (taskRunner.getAusfuehrungsplan()) {
            case ONCE:
                scheduledFuture = schedule(taskRunner);
                break;
            case FIXED_RATE:
                scheduledFuture = scheduleAtFixedRate(taskRunner);
                break;
            case FIXED_DELAY:
                scheduledFuture = scheduleWithFixedDelay(taskRunner);
                break;
            }
            String id = taskRunner.getId();
            scheduledFutures.put(id, scheduledFuture);
        }
    }

    /**
     * Plant und führt einen TaskRunner aus, der zu einem bestimmten Zeitpunkt ausgeführt wird.
     *
     * @param taskRunner
     * @return ScheduledFuture/<String/>
     */
    private synchronized ScheduledFuture<?> schedule(TaskRunner taskRunner) {
        LOG.debug("Reihe TaskRunner {} ein (delay: {})", taskRunner.getId(),
            Duration.between(LocalDateTime.now(), taskRunner.getExecutionDateTime()));
        ScheduledFuture<?> scheduledFuture = null;
        try {
            scheduledFuture = scheduledExecutorService.get().schedule(taskRunner,
                Duration.between(LocalDateTime.now(), taskRunner.getExecutionDateTime()).getSeconds(),
                TimeUnit.SECONDS);
            scheduledFutures.put(taskRunner.getId(), scheduledFuture);
            counter.incrementAndGet();

        } catch (Exception e) {
            taskRunner.getTask().zeichneFehlgeschlageneAusfuehrungAuf(e);

            String msg = MessageSourceHolder
                .getMessage(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, taskRunner.getId());
            LOG.error(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, msg, e);
        }
        return scheduledFuture;
    }

    /**
     * Plant einen zeitgesteuerten und immer wiederkehrenden TaskRunner.
     *
     * @param taskRunner
     * @return
     * @throws NoSuchMethodException
     * @throws Exception
     */
    private synchronized ScheduledFuture<?> scheduleAtFixedRate(TaskRunner taskRunner) {
        LOG.debug("Reihe TaskRunner {} ein (initial-delay: {}, fixed-rate: {})", taskRunner.getId(),
            taskRunner.getInitialDelay(), taskRunner.getFixedRate());
        ScheduledFuture<?> scheduledFuture = null;
        try {
            scheduledFuture = scheduledExecutorService.get()
                .scheduleAtFixedRate(taskRunner, taskRunner.getInitialDelay().getSeconds(),
                    taskRunner.getFixedRate().getSeconds(), TimeUnit.SECONDS);
            scheduledFutures.put(taskRunner.getId(), scheduledFuture);
            counter.incrementAndGet();

        } catch (Exception e) {
            taskRunner.getTask().zeichneFehlgeschlageneAusfuehrungAuf(e);

            String msg = MessageSourceHolder
                .getMessage(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, taskRunner.getId());
            LOG.error(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, msg, e);
        }
        return scheduledFuture;
    }

    /**
     * @param taskRunner
     * @throws NoSuchMethodException
     * @throws Exception
     */
    private synchronized ScheduledFuture<?> scheduleWithFixedDelay(TaskRunner taskRunner) {
        LOG.debug("Reihe TaskRunner {} ein (initial-delay: {}, fixed-delay: {})", taskRunner.getId(),
            taskRunner.getInitialDelay(), taskRunner.getFixedDelay());
        ScheduledFuture<?> scheduledFuture = null;
        try {
            scheduledFuture = scheduledExecutorService.get()
                .scheduleWithFixedDelay(taskRunner, taskRunner.getInitialDelay().getSeconds(),
                    taskRunner.getFixedDelay().getSeconds(), TimeUnit.SECONDS);
            scheduledFutures.put(taskRunner.getId(), scheduledFuture);
            counter.incrementAndGet();

        } catch (Exception e) {
            taskRunner.getTask().zeichneFehlgeschlageneAusfuehrungAuf(e);

            String msg = MessageSourceHolder
                .getMessage(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, taskRunner.getId());
            LOG.error(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, msg, e);
        }
        return scheduledFuture;
    }

    /**
     * Diese Methode blockiert das Beenden des Programms für eine festgelegte Anzahl an Sekunden.
     * Die Methode wird genutzt, damit die Tasks noch weiterlaufen.
     *
     * @param seconds
     * @throws InterruptedException
     */
    @Override
    public void awaitTerminationInSeconds(long seconds) throws InterruptedException {
        LOG.debug("awaitTerminationInSeconds ", " counter: " + counter.get());
        scheduledExecutorService.get().awaitTermination(seconds, TimeUnit.SECONDS);
    }

    /**
     * Mit dieser Methode wird veranlasst, dass der ScheduledThreadPoolExecutor
     * keine neuen Tasks mehr übernimmt.
     * Die im bereits zugewiesenen Tasks werden aber noch abgearbeitet.
     */
    @Override
    public void shutDown() {
        LOG.debug("shutDown ", " counter: " + counter.get());
        scheduledExecutorService.get().shutdown();
    }

    /**
     * Mit dieser Methode wird bei allen aktiven Tasks der Interrupted-Status auf interrupted gesetzt.
     * Die aktiven Threads sind selbst dafür verantwortlich, hierauf zu reagieren.
     *
     * @return
     */
    @Override
    public List<Runnable> shutDownNow() {
        LOG.debug("shutDownNow ", " counter: " + counter.get());
        return scheduledExecutorService.get().shutdownNow();
    }

    /**
     * @return
     */
    @Override
    public boolean isTerminated() {
        LOG.debug("isTerminated ", " counter: " + counter.get());
        return scheduledExecutorService.get().isTerminated();
    }

    /**
     * Eine interne Methode, die laufend den Status des isy-timertasks überprüft.
     */
    @Override
    public void run() {
        while (!stop) {
            try {
                List<String> tasksNeuStarten = new ArrayList<>();
                for (Map.Entry<String, ScheduledFuture<?>> entry : scheduledFutures.entrySet()) {
                    if (entry.getValue().isCancelled()) {
                        try {
                            entry.getValue().get();
                        } catch (CancellationException e) {
                            // TODO INFO-Log
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            // TODO WARN-Log
                            e.printStackTrace();
                            tasksNeuStarten.add(entry.getKey());
                        }
                    }
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                stop = true;
            }
        }
    }

    /**
     *
     */
    @Override
    public void stop() {
        stop = true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}