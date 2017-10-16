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
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.model.impl.TaskRunnerImpl;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
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

    private final TaskKonfiguration taskKonfiguration;

    private ScheduledExecutorService scheduledExecutorService;

    private final List<TaskRunner> zuStartendeTasks = new ArrayList<>();

    private final Map<String, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskSchedulerImpl.class);

    private ApplicationContext applicationContext;

    public TaskSchedulerImpl(Konfiguration konfiguration, TaskKonfiguration taskKonfiguration) {
        this.konfiguration = konfiguration;
        this.taskKonfiguration = taskKonfiguration;
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
                addTask(createTask(taskId, taskKonfiguration, applicationContext));
            } catch (HostNotApplicableException e) {
                // TODO INFO-LOG
            }
        }

        start();
    }

    private TaskRunner createTask(String taskId, TaskKonfiguration taskKonfiguration,
        ApplicationContext applicationContext) throws HostNotApplicableException {

        TaskRunner taskRunner = null;
        if (taskKonfiguration.getHostHandler().isHostApplicable(taskId, konfiguration)) {

            SecurityAuthenticator securityAuthenticator = taskKonfiguration.getSecurityAuthenticator(taskId);

            Task task = applicationContext.getBean(taskId, Task.class);

            TaskKonfiguration.Ausfuehrungsplan ausfuehrungsplan =
                taskKonfiguration.getAusfuehrungsplan(taskId);

            LocalDateTime executionDateTime = taskKonfiguration.getExecutionDateTime(taskId);

            taskRunner =
                new TaskRunnerImpl(taskId, securityAuthenticator, task, ausfuehrungsplan, executionDateTime,
                    taskKonfiguration.getInitialDelay(taskId), taskKonfiguration.getFixedRate(taskId),
                    taskKonfiguration.getFixedDelay(taskId));
        }
        return taskRunner;
    }

    public synchronized void addTask(TaskRunner taskRunner) {
        zuStartendeTasks.add(taskRunner);
    }

    @Override
    public synchronized void start() {
        starteTasks(true);
    }

    private void starteTasks(boolean starteWatchdog) {
        for (TaskRunner taskRunner : zuStartendeTasks) {
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
        zuStartendeTasks.clear();
    }

    /**
     * Plant und führt einen TaskRunner aus, der zu einem bestimmten Zeitpunkt ausgeführt wird.
     *
     * @param taskRunner
     * @return ScheduledFuture/<String/>
     */
    private synchronized ScheduledFuture<?> schedule(TaskRunner taskRunner) {

        ScheduledFuture<?> scheduledFuture = null;
        try {
            if (taskRunner.getExecutionDateTime() != null) {
                LOG.debug("Reihe TaskRunner {} ein (delay: {})", taskRunner.getId(),
                    Duration.between(DateTimeUtil.localDateTimeNow(), taskRunner.getExecutionDateTime()));
                scheduledFuture = scheduledExecutorService.schedule(taskRunner,
                    Duration.between(DateTimeUtil.localDateTimeNow(), taskRunner.getExecutionDateTime())
                        .getSeconds(), SECONDS);
            } else if (taskRunner.getInitialDelay() != null) {
                LOG.debug("Reihe TaskRunner {} ein (delay: {})", taskRunner.getId(),
                    taskRunner.getInitialDelay());
                scheduledFuture = scheduledExecutorService
                    .schedule(taskRunner, taskRunner.getInitialDelay().getSeconds(), SECONDS);
            }
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

            do {
                try {
                    taskFuture.get();
                    stop = true;
                } catch (CancellationException e) {
                    LOG.info(LogKategorie.JOURNAL, "ISYTA99999", "Task wurde abgebrochen.", e);
                    stop = true;
                } catch (ExecutionException e) {
                    LOG.warn("ISYTA99999", "Task " + taskId + " wurde fehlerhaft beendet.", e.getCause());
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
        }

        private void addTask(String id) {
            try {
                TaskSchedulerImpl.this.addTask(createTask(id, taskKonfiguration, applicationContext));
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