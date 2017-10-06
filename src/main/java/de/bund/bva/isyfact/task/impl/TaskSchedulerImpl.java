package de.bund.bva.isyfact.task.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.TaskScheduler;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte.DEFAULT_INITIAL_NUMBER_OF_THREADS;

/**
 * Der TaskScheduler bietet die Möglichkeit, dass Tasks zu bestimmten Zeitpunkten ausgeführt werden können.
 *
 * Intern arbeitet TaskScheduler mit Thread-Local-gesicherten ScheduledExecutorServices, sodass auch
 * ein TaskScheduler Thread-sicher von mehreren Threads Thread-sicher durchlaufen werden kann.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class TaskSchedulerImpl implements TaskScheduler, Runnable
{
    private volatile ThreadLocal<Konfiguration> konfiguration
            = new ThreadLocal<>();
    private volatile ThreadLocal<SecurityAuthenticator> securityAuthenticator
            = new ThreadLocal<>();
    private volatile ThreadLocal<LocalDateTime> startTime
            = new ThreadLocal<>();
    private volatile ThreadLocal<ScheduledExecutorService> scheduledExecutorService
            = new ThreadLocal<>();
    private final Map<String, Task> tasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskSchedulerImpl.class);
    private final AtomicLong counter = new AtomicLong();

    private boolean stop = false;

    /**
     * @param konfiguration
     * @param securityAuthenticator
     */
    public TaskSchedulerImpl(
            Konfiguration konfiguration,
            SecurityAuthenticator securityAuthenticator) {
        this.konfiguration.set(konfiguration);
        this.securityAuthenticator.set(securityAuthenticator);
        this.startTime.set(LocalDateTime.now());

        int initialNumberOfThreads = DEFAULT_INITIAL_NUMBER_OF_THREADS;
        if (konfiguration != null) {
            initialNumberOfThreads = this.konfiguration.get()
                    .getAsInteger(
                            KonfigurationSchluessel.INITIAL_NUMBER_OF_THREADS);
        }

        ScheduledExecutorService executorService =
                Executors.newScheduledThreadPool(initialNumberOfThreads);

        this.scheduledExecutorService.set(executorService);
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void start() throws NoSuchMethodException {
        for(Task task : tasks.values()) {
            ScheduledFuture<?> scheduledFuture = null;
            switch (task.getAusfuehrungsplan()) {
                case ONCE:
                    scheduledFuture = schedule(task);
                    break;
                case FIXED_RATE:
                    scheduledFuture = schedule(task);
                    break;
                case FIXED_DELAY:
                    scheduledFuture = schedule(task);
                    break;
            }
            String id = task.getId();
            scheduledFutures.put(id, scheduledFuture);
        }
    }

    /**
     * Plant und führt einen Task aus, der zu einem bestimmten Zeitpunkt ausgeführt wird.
     *
     * @param task
     * @return ScheduledFuture/<String/>
     */
    private synchronized ScheduledFuture<?> schedule(Task task) {
        ScheduledFuture<?> scheduledFuture = null;
        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

            // TODO: Frage an Bjoern - Wie wird der SecurityAuthenticator injiziert
            // securityAuthenticator.get().login();

            scheduledFuture = scheduledExecutorService.get().schedule(
                    task.getOperation(),
                    Duration.between(LocalDateTime.now(), task.getOperation().getExecutionDateTime()).toNanos(),
                    TimeUnit.NANOSECONDS);
            scheduledFutures.put(task.getId(), scheduledFuture);
            counter.incrementAndGet();

        } catch (Exception e) {
            task.getOperation().setErrorMessage(e.getMessage());
            task.getOperation().setHasBeenExecutedSuccessfully(false);

            //TODO: Frage an Bjoern - Wie funktionieren die Meldungsschlüssel
            // String msg = MessageSourceHolder.getMessage(FehlerSchluessel.MEL_FEHLER_TASK00001);
            // LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.MEL_FEHLER_TASK00001, msg);

        } finally {
            MdcHelper.entferneKorrelationsId();
            // TODO: Frage an Bjoern - Wie wird der SecurityAuthenticator injiziert
            //securityAuthenticator.logout();
        }
        return scheduledFuture;
    }

    /**
     * Plant einen zeitgesteuerten und immer wiederkehrenden Task.
     *
     * @param task
     * @return
     * @throws NoSuchMethodException
     * @throws Exception
     */
    private synchronized ScheduledFuture<?> scheduleAtFixedRate(Task task) throws NoSuchMethodException {
        LOG.debug("schedule: ", " executionDateTime: " + task.getOperation().getExecutionDateTime());
        ScheduledFuture<?> scheduledFuture = null;
        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

            // TODO: Frage an Bjoern - Wie wird der SecurityAuthenticator injiziert
            // securityAuthenticator.login();

            scheduledFuture = scheduledExecutorService.get().scheduleAtFixedRate(
                    task.getOperation(),
                    Duration.between(LocalDateTime.now(), task.getOperation().getExecutionDateTime()).toNanos(),
                    task.getOperation().getFixedRate().toNanos(),
                    TimeUnit.NANOSECONDS);
            scheduledFutures.put(task.getId(), scheduledFuture);
            counter.incrementAndGet();

        } catch (Exception e) {
            task.getOperation().setErrorMessage(e.getMessage());
            task.getOperation().setHasBeenExecutedSuccessfully(false);

            //TODO: Frage an Bjoern - Wie funktionieren die Meldungsschlüssel
            // String msg = MessageSourceHolder.getMessage(FehlerSchluessel.MEL_FEHLER_TASK00001);
            // LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.MEL_FEHLER_TASK00001, msg);

        } finally {
            MdcHelper.entferneKorrelationsId();
            // TODO: Frage an Bjoern - Wie wird der SecurityAuthenticator injiziert
            //securityAuthenticator.logout();
        }
        return scheduledFuture;
    }

    /**
     * @param task
     * @throws NoSuchMethodException
     * @throws Exception
     */
    private synchronized ScheduledFuture<?> scheduleWithFixedDelay(Task task) throws NoSuchMethodException {
        LOG.debug("schedule: ", " executionDateTime: " + task.getOperation().getExecutionDateTime());
        ScheduledFuture<?> scheduledFuture = null;
        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());
            // TODO: Frage an Bjoern - Wie wird der SecurityAuthenticator injiziert
            // securityAuthenticator.login();

            scheduledFuture = scheduledExecutorService.get().scheduleWithFixedDelay(
                    task.getOperation(),
                    Duration.between(LocalDateTime.now(), task.getOperation().getExecutionDateTime()).toNanos(),
                    task.getOperation().getFixedDelay().toNanos(),
                    TimeUnit.NANOSECONDS);
            scheduledFutures.put(task.getId(), scheduledFuture);
            counter.incrementAndGet();

        } catch (Exception e) {
            task.getOperation().setErrorMessage(e.getMessage());
            task.getOperation().setHasBeenExecutedSuccessfully(false);

            //TODO: Frage an Bjoern - Wie funktionieren die Meldungsschlüssel
            // String msg = MessageSourceHolder.getMessage(FehlerSchluessel.MEL_FEHLER_TASK00001);
            // LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.MEL_FEHLER_TASK00001, msg);

        } finally {
            MdcHelper.entferneKorrelationsId();
            // TODO: Frage an Bjoern - Wie wird der SecurityAuthenticator injiziert
            //securityAuthenticator.logout();
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
        while(!stop) {
            try {
                List<String> tasksNeuStarten = new ArrayList<>();
               for (Map.Entry<String, ScheduledFuture<?>> entry : scheduledFutures.entrySet()) {
                    if (entry.getValue().isCancelled()) {
                        try {
                            entry.getValue().get();
                        } catch (CancellationException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
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
}