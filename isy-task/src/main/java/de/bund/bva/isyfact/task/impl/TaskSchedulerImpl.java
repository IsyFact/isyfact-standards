package de.bund.bva.isyfact.task.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
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
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfigurationVerwalter;
import de.bund.bva.isyfact.task.konstanten.Ereignisschluessel;
import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.model.impl.TaskRunnerImpl;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Implementierung von {@link TaskScheduler}, bei der die Tasks als Spring-Beans bereitgestellt und per
 * {@link IsyTaskConfigurationProperties} konfiguriert werden.
 */
public class TaskSchedulerImpl implements TaskScheduler, ApplicationContextAware {
    private final IsyTaskConfigurationProperties configurationProperties;

    private final TaskKonfigurationVerwalter taskKonfigurationVerwalter;

    private final HostHandler hostHandler;

    private final ScheduledExecutorService scheduledExecutorService;

    private final List<TaskRunner> zuStartendeTasks = Collections.synchronizedList(new ArrayList<>());

    private final List<TaskRunner> laufendeTasks = Collections.synchronizedList(new ArrayList<>());

    private final Map<String, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskSchedulerImpl.class);

    private ApplicationContext applicationContext;

    /**
     * Erstelle eine {@link TaskScheduler}-Instanz.
     *
     * @param configurationProperties
     *            {@link IsyTaskConfigurationProperties} zur Konfiguration des TaskScheduler
     * @param taskKonfigurationVerwalter
     *            {@link TaskKonfigurationVerwalter} der die Konfiguration der Tasks bereitstellt
     * @param hostHandler
     *            {@link HostHandler} zur Überprüfung des Hosts, auf dem die Tasks ausgeführt werden sollen
     */
    public TaskSchedulerImpl(IsyTaskConfigurationProperties configurationProperties,
        TaskKonfigurationVerwalter taskKonfigurationVerwalter,
        HostHandler hostHandler) {
        this.configurationProperties = configurationProperties;
        this.taskKonfigurationVerwalter = taskKonfigurationVerwalter;
        this.hostHandler = hostHandler;
        int initialNumberOfThreads = configurationProperties.getDefault().getAmountOfThreads();

        scheduledExecutorService = Executors.newScheduledThreadPool(initialNumberOfThreads);
    }

    @Override
    public void starteKonfigurierteTasks() {
        for (String taskId : applicationContext.getBeanNamesForType(Task.class)) {
            try {
                addTask(createTask(applicationContext.getBean(taskId, Task.class),
                    taskKonfigurationVerwalter.getTaskKonfiguration(taskId)));
            } catch (HostNotApplicableException e) {
                LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, e);
            }
        }

        start();
    }

    private TaskRunner createTask(Task task, TaskKonfiguration taskKonfiguration)
        throws HostNotApplicableException {

        TaskRunner taskRunner = null;
        if (hostHandler.isHostApplicable(taskKonfiguration.getHostname())) {
            taskRunner = new TaskRunnerImpl(task, taskKonfiguration);
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
            switch (taskRunner.getTaskKonfiguration().getAusfuehrungsplan()) {
            case ONCE:
                schedule(taskRunner);
                break;
            case FIXED_RATE:
                scheduleAtFixedRate(taskRunner);
                break;
            case FIXED_DELAY:
                scheduleWithFixedDelay(taskRunner);
                break;
            }
            String id = taskRunner.getTaskKonfiguration().getTaskId();

            if (starteWatchdog && scheduledFutures.containsKey(id)) {
                new Thread(new CompletionWatchdog(id)).start();
            }

        }
        zuStartendeTasks.clear();
    }

    private synchronized void schedule(TaskRunner taskRunner) {
        ScheduledFuture<?> scheduledFuture = null;
        String taskId = taskRunner.getTaskKonfiguration().getTaskId();

        try {
            if (taskRunner.getTaskKonfiguration().getExecutionDateTime() != null) {
                Duration delay = Duration.between(DateTimeUtil.localDateTimeNow(),
                    taskRunner.getTaskKonfiguration().getExecutionDateTime());
                LOG.debug("Reihe TaskRunner {} ein (delay: {})", taskId, delay);
                scheduledFuture =
                    scheduledExecutorService.schedule(taskRunner, delay.toMillis(), MILLISECONDS);
            } else if (taskRunner.getTaskKonfiguration().getInitialDelay() != null) {
                LOG.debug("Reihe TaskRunner {} ein (delay: {})", taskId,
                    taskRunner.getTaskKonfiguration().getInitialDelay());
                scheduledFuture = scheduledExecutorService
                    .schedule(taskRunner, taskRunner.getTaskKonfiguration().getInitialDelay().toMillis(),
                        MILLISECONDS);
            }
            scheduledFutures.put(taskId, scheduledFuture);
            laufendeTasks.add(taskRunner);
        } catch (Exception e) {
            taskRunner.getTask().zeichneFehlgeschlageneAusfuehrungAuf(e);

            String msg = MessageSourceHolder
                .getMessage(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, taskId);
            LOG.error(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, msg, e);
        }
    }

    private synchronized void scheduleAtFixedRate(TaskRunner taskRunner) {
        Duration fixedRate = taskRunner.getTaskKonfiguration().getFixedRate();
        Duration initialDelay = taskRunner.getTaskKonfiguration().getInitialDelay();
        String taskId = taskRunner.getTaskKonfiguration().getTaskId();

        LOG.debug("Reihe TaskRunner {} ein (initial-delay: {}, fixed-rate: {})", taskId, initialDelay,
            fixedRate);
        ScheduledFuture<?> scheduledFuture = null;

        try {
            scheduledFuture = scheduledExecutorService
                .scheduleAtFixedRate(taskRunner, initialDelay.toMillis(), fixedRate.toMillis(), MILLISECONDS);
            scheduledFutures.put(taskId, scheduledFuture);
            laufendeTasks.add(taskRunner);
        } catch (Exception e) {
            taskRunner.getTask().zeichneFehlgeschlageneAusfuehrungAuf(e);

            String msg = MessageSourceHolder
                .getMessage(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, taskId);
            LOG.error(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, msg, e);
        }
    }

    private synchronized void scheduleWithFixedDelay(TaskRunner taskRunner) {
        Duration fixedDelay = taskRunner.getTaskKonfiguration().getFixedDelay();
        Duration initialDelay = taskRunner.getTaskKonfiguration().getInitialDelay();
        String taskId = taskRunner.getTaskKonfiguration().getTaskId();

        LOG.debug("Reihe TaskRunner {} ein (initial-delay: {}, fixed-delay: {})", taskId, initialDelay,
            fixedDelay);
        ScheduledFuture<?> scheduledFuture = null;
        try {
            scheduledFuture = scheduledExecutorService
                .scheduleWithFixedDelay(taskRunner, initialDelay.toMillis(), fixedDelay.toMillis(),
                    MILLISECONDS);
            scheduledFutures.put(taskId, scheduledFuture);
            laufendeTasks.add(taskRunner);
        } catch (Exception e) {
            taskRunner.getTask().zeichneFehlgeschlageneAusfuehrungAuf(e);

            String msg = MessageSourceHolder
                .getMessage(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, taskId);
            LOG.error(FehlerSchluessel.TASK_KONNTE_NICHT_EINGEREIHT_WERDEN, msg, e);
        }
    }

    @Override
    public boolean shutdownMitTimeout(long seconds) throws InterruptedException {
        scheduledExecutorService.shutdown();
        return scheduledExecutorService.awaitTermination(seconds, SECONDS);
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

                    entferneLaufendenTask(taskId);

                    stop = true;
                } catch (CancellationException e) {
                    String nachricht =
                        MessageSourceHolder.getMessage(Ereignisschluessel.TASK_WURDE_ABGEBROCHEN, taskId);
                    LOG.info(LogKategorie.JOURNAL, Ereignisschluessel.TASK_WURDE_ABGEBROCHEN,
                        DateTimeUtil.localDateTimeNow() + " " + nachricht);

                    entferneLaufendenTask(taskId);

                    stop = true;
                } catch (ExecutionException e) {
                    String nachricht = MessageSourceHolder
                        .getMessage(Ereignisschluessel.TASK_WURDE_FEHLERHAFT_BEENDET, taskId, e.getMessage());
                    LOG.warn(Ereignisschluessel.TASK_WURDE_FEHLERHAFT_BEENDET, nachricht);

                    if (scheduledExecutorService.isShutdown()) {
                        break;
                    }

                    addTask(taskId);
                    starteTasks(false);
                    taskFuture = scheduledFutures.get(taskId);

                    try {
                        SECONDS
                            .sleep(configurationProperties.getWatchdog().getRestartInterval().getSeconds());
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
                TaskSchedulerImpl.this.addTask(createTask(applicationContext.getBean(taskId, Task.class),
                    taskKonfigurationVerwalter.getTaskKonfiguration(id)));
            } catch (HostNotApplicableException hnae) {
                LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, hnae);
            }
        }

        private void entferneLaufendenTask(String taskId) {
            scheduledFutures.remove(taskId);
            laufendeTasks.removeIf(t -> t.getTaskKonfiguration().getTaskId().equals(taskId));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized List<TaskRunner> getLaufendeTasks() {
        return Collections.unmodifiableList(laufendeTasks);
    }
}
