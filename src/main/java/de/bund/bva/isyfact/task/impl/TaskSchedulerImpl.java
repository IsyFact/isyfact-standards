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
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konstanten.Ereignisschluessel;
import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.model.impl.TaskRunnerImpl;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte.DEFAULT_INITIAL_NUMBER_OF_THREADS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Der TaskScheduler bietet die Möglichkeit, Tasks zu bestimmten Zeitpunkten auszuführen.
 *
 * Intern arbeitet TaskScheduler mit einem ScheduledExecutorService.
 */
public class TaskSchedulerImpl implements TaskScheduler, ApplicationContextAware {
    private final Konfiguration konfiguration;

    private final TaskKonfiguration taskKonfiguration;

    private final HostHandler hostHandler;

    private ScheduledExecutorService scheduledExecutorService;

    private final List<TaskRunner> zuStartendeTasks = new ArrayList<>();

    private final Map<String, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskSchedulerImpl.class);

    private ApplicationContext applicationContext;

    public TaskSchedulerImpl(Konfiguration konfiguration, TaskKonfiguration taskKonfiguration,
        HostHandler hostHandler) {
        this.konfiguration = konfiguration;
        this.taskKonfiguration = taskKonfiguration;
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
                addTask(
                    createTask(taskId, applicationContext.getBean(taskId, Task.class), taskKonfiguration));
            } catch (HostNotApplicableException e) {
                LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, e);
            }
        }

        start();
    }

    private TaskRunner createTask(String taskId, Task task, TaskKonfiguration taskKonfiguration)
        throws HostNotApplicableException {

        TaskRunner taskRunner = null;
        if (hostHandler.isHostApplicable(taskKonfiguration.getHostname(taskId))) {

            Authenticator authenticator = taskKonfiguration.getSecurityAuthenticator(taskId);

            TaskKonfiguration.Ausfuehrungsplan ausfuehrungsplan =
                taskKonfiguration.getAusfuehrungsplan(taskId);

            LocalDateTime executionDateTime = taskKonfiguration.getExecutionDateTime(taskId);

            taskRunner = new TaskRunnerImpl(taskId, authenticator, task, ausfuehrungsplan, executionDateTime,
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

    private synchronized ScheduledFuture<?> schedule(TaskRunner taskRunner) {

        ScheduledFuture<?> scheduledFuture = null;
        try {
            if (taskRunner.getExecutionDateTime() != null) {
                Duration delay =
                    Duration.between(DateTimeUtil.localDateTimeNow(), taskRunner.getExecutionDateTime());
                LOG.debug("Reihe TaskRunner {} ein (delay: {})", taskRunner.getId(), delay);
                scheduledFuture = scheduledExecutorService.schedule(taskRunner, delay.getSeconds(), SECONDS);
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


    @Override
    public void stopNachTimeout(long seconds) throws InterruptedException {
        scheduledExecutorService.shutdown();
        scheduledExecutorService.awaitTermination(seconds, SECONDS);
    }

    @Override
    public void warteAufTerminierung(long sekunden) throws InterruptedException {
        scheduledExecutorService.awaitTermination(sekunden, SECONDS);
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
                    String nachricht =
                        MessageSourceHolder.getMessage(Ereignisschluessel.TASK_WURDE_ABGEBROCHEN, taskId);
                    LOG.info(LogKategorie.JOURNAL, Ereignisschluessel.TASK_WURDE_ABGEBROCHEN, nachricht);
                    stop = true;
                } catch (ExecutionException e) {
                    String nachricht = MessageSourceHolder
                        .getMessage(Ereignisschluessel.TASK_WURDE_FEHLERHAFT_BEENDET, taskId, e.getMessage());
                    LOG.warn(Ereignisschluessel.TASK_WURDE_FEHLERHAFT_BEENDET, nachricht);
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
                TaskSchedulerImpl.this.addTask(
                    createTask(id, applicationContext.getBean(taskId, Task.class), taskKonfiguration));
            } catch (HostNotApplicableException hnae) {
                LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, hnae);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}