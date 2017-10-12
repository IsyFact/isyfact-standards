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

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.TaskScheduler;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.handler.AusfuehrungsplanHandler;
import de.bund.bva.isyfact.task.handler.ExecutionDateTimeHandler;
import de.bund.bva.isyfact.task.handler.HostHandler;
import de.bund.bva.isyfact.task.handler.impl.AusfuehrungsplanHandlerImpl;
import de.bund.bva.isyfact.task.handler.impl.ExecutionDateTimeHandlerImpl;
import de.bund.bva.isyfact.task.konfiguration.DurationUtil;
import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.model.impl.TaskRunnerImpl;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.isyfact.task.security.SecurityAuthenticatorFactory;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte.DEFAULT_INITIAL_NUMBER_OF_THREADS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Der TaskScheduler bietet die Möglichkeit, Tasks zu bestimmten Zeitpunkten auszuführen.
 *
 * Intern arbeitet TaskScheduler mit Thread-Local-gesicherten ScheduledExecutorServices, sodass auch
 * ein TaskScheduler Thread-sicher von mehreren Threads Thread-sicher durchlaufen werden kann.
 *
 */
public class TaskSchedulerImpl implements TaskScheduler, ApplicationContextAware {
    private final Konfiguration konfiguration;

    private final SecurityAuthenticatorFactory securityAuthenticatorFactory;

    private final HostHandler hostHandler;

    private ScheduledExecutorService scheduledExecutorService;

    private final List<TaskRunner> zuStartendeTaks = new ArrayList<>();

    private final Map<String, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskSchedulerImpl.class);

    private ApplicationContext applicationContext;

    public TaskSchedulerImpl(Konfiguration konfiguration,
        SecurityAuthenticatorFactory securityAuthenticatorFactory, HostHandler hostHandler) {
        this.konfiguration = konfiguration;
        this.securityAuthenticatorFactory = securityAuthenticatorFactory;
        this.hostHandler = hostHandler;

        int initialNumberOfThreads = DEFAULT_INITIAL_NUMBER_OF_THREADS;
        if (konfiguration != null) {
            initialNumberOfThreads =
                this.konfiguration.getAsInteger(KonfigurationSchluessel.INITIAL_NUMBER_OF_THREADS);
        }

        scheduledExecutorService = Executors.newScheduledThreadPool(initialNumberOfThreads);
    }

    @Override
    public void starteKonfigurierteTasks() {
        for (String taskId : applicationContext.getBeanNamesForType(Task.class)) {
            try {
                addTask(createTask(taskId, konfiguration, applicationContext));
            } catch (HostNotApplicableException e) {
                // TODO INFO-LOG
            }
        }

        start();
    }

    private TaskRunner createTask(String id, Konfiguration konfiguration,
        ApplicationContext applicationContext) throws HostNotApplicableException {

        TaskRunner taskRunner = null;
        if (hostHandler.isHostApplicable(id, konfiguration)) {

            SecurityAuthenticator securityAuthenticator =
                securityAuthenticatorFactory.getSecurityAuthenticator(id);

            Task task = applicationContext.getBean(id, Task.class);

            AusfuehrungsplanHandler ausfuehrungsplanHandler = new AusfuehrungsplanHandlerImpl();
            AusfuehrungsplanHandlerImpl.Ausfuehrungsplan ausfuehrungsplan =
                ausfuehrungsplanHandler.getAusfuehrungsplan(id, konfiguration);

            ExecutionDateTimeHandler executionDateTimeHandler = new ExecutionDateTimeHandlerImpl();
            LocalDateTime executionDateTime =
                executionDateTimeHandler.getExecutionDateTime(id, konfiguration);

            Duration initialDelay = DurationUtil.leseInitialDelay(id, konfiguration);
            Duration fixedRate = DurationUtil.leseFixedRate(id, konfiguration);
            Duration fixedDelay = DurationUtil.leseFixedDelay(id, konfiguration);

            taskRunner = new TaskRunnerImpl(id, securityAuthenticator, task, ausfuehrungsplan, executionDateTime,
                initialDelay, fixedRate, fixedDelay);
        }
        return taskRunner;
    }

    public synchronized void addTask(TaskRunner taskRunner) {
        zuStartendeTaks.add(taskRunner);
    }

    @Override
    public synchronized void start() {
        starteTasks(true);
    }

    private void starteTasks(boolean starteWatchdog) {
        for (TaskRunner taskRunner : zuStartendeTaks) {
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
            if (starteWatchdog) {
                new Thread(new CompletionWatchdog(id)).start();
            }

        }
        zuStartendeTaks.clear();
    }

    /**
     * Plant und führt einen TaskRunner aus, der zu einem bestimmten Zeitpunkt ausgeführt wird.
     *
     * @param taskRunner
     * @return ScheduledFuture/<String/>
     */
    private synchronized ScheduledFuture<?> schedule(TaskRunner taskRunner) {
        LOG.debug("Reihe TaskRunner {} ein (delay: {})", taskRunner.getId(),
            Duration.between(DateTimeUtil.localDateTimeNow(), taskRunner.getExecutionDateTime()));
        ScheduledFuture<?> scheduledFuture = null;
        try {
            scheduledFuture = scheduledExecutorService.schedule(taskRunner,
                Duration.between(DateTimeUtil.localDateTimeNow(), taskRunner.getExecutionDateTime())
                    .getSeconds(), SECONDS);
            scheduledFutures.put(taskRunner.getId(), scheduledFuture);

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
            scheduledFuture = scheduledExecutorService
                .scheduleAtFixedRate(taskRunner, taskRunner.getInitialDelay().getSeconds(),
                    taskRunner.getFixedRate().getSeconds(), SECONDS);
            scheduledFutures.put(taskRunner.getId(), scheduledFuture);

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
            scheduledFuture = scheduledExecutorService
                .scheduleWithFixedDelay(taskRunner, taskRunner.getInitialDelay().getSeconds(),
                    taskRunner.getFixedDelay().getSeconds(), SECONDS);
            scheduledFutures.put(taskRunner.getId(), scheduledFuture);

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
        LOG.debug("awaitTerminationInSeconds");
        scheduledExecutorService.awaitTermination(seconds, SECONDS);
    }

    /**
     * Mit dieser Methode wird veranlasst, dass der ScheduledThreadPoolExecutor
     * keine neuen Tasks mehr übernimmt.
     * Die im bereits zugewiesenen Tasks werden aber noch abgearbeitet.
     */
    @Override
    public void shutDown() {
        LOG.debug("shutDown");
        scheduledExecutorService.shutdown();
    }

    /**
     * Mit dieser Methode wird bei allen aktiven Tasks der Interrupted-Status auf interrupted gesetzt.
     * Die aktiven Threads sind selbst dafür verantwortlich, hierauf zu reagieren.
     *
     * @return
     */
    @Override
    public List<Runnable> shutDownNow() {
        LOG.debug("shutDownNow");
        return scheduledExecutorService.shutdownNow();
    }

    /**
     * @return
     */
    @Override
    public boolean isTerminated() {
        LOG.debug("isTerminated");
        return scheduledExecutorService.isTerminated();
    }

    private class CompletionWatchdog implements Runnable {

        private boolean stop = false;

        private final String taskId;

        CompletionWatchdog(String taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            ScheduledFuture<?> taskFuture = scheduledFutures.get(taskId);

            System.out.println("Watchdog für " + taskId + " gestartet");

            do {
                try {
                    taskFuture.get();
                    stop = true;
                } catch (CancellationException e) {
                    LOG.info(LogKategorie.JOURNAL, "ISYTA99999", "Task wurde abgebrochen.", e);
                    stop = true;
                } catch (ExecutionException e) {
                    LOG.warn("ISYTA99999", "Task wurde fehlerhaft beendet.", e.getCause());
                    taskFuture.cancel(true);
                    addTask(taskId);
                    starteTasks(false);
                    taskFuture = scheduledFutures.get(taskId);

                    try {
                        SECONDS.sleep(
                            konfiguration.getAsInteger(KonfigurationSchluessel.WATCHDOG_RESTART_INTERVAL, 1));
                    } catch (InterruptedException ie) {
                        stop = true;
                    }
                } catch (InterruptedException e) {
                    stop = true;
                }
            } while (!stop);

            System.out.println("Watchdog für " + taskId + " beendet");
        }

        private void addTask(String id) {
            try {
                TaskSchedulerImpl.this.addTask(createTask(id, konfiguration, applicationContext));
            } catch (HostNotApplicableException hnae) {
                // TODO log
            }
        }


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}