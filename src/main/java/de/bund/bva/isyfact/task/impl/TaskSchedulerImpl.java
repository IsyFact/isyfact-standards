package de.bund.bva.isyfact.task.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.TaskScheduler;
import de.bund.bva.isyfact.task.handler.ExecutorHandler;
import de.bund.bva.isyfact.task.handler.impl.ExecutorHandlerImpl;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
public class TaskSchedulerImpl implements TaskScheduler {
    private volatile ThreadLocal<Konfiguration> konfiguration
            = new ThreadLocal<>();
    private volatile ThreadLocal<SecurityAuthenticator> securityAuthenticator
            = new ThreadLocal<>();
    private volatile ThreadLocal<LocalDateTime> startTime
            = new ThreadLocal<>();
    private final AtomicLong counter = new AtomicLong();

    private volatile ThreadLocal<ScheduledExecutorService> scheduledExecutorService
            = new ThreadLocal<>();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskSchedulerImpl.class);

    /**
     *
     * @param konfiguration
     * @param securityAuthenticator
     */
    public TaskSchedulerImpl(
            Konfiguration konfiguration,
            SecurityAuthenticator securityAuthenticator) {
        this.konfiguration.set(konfiguration);
        this.securityAuthenticator.set(securityAuthenticator);

        this.startTime.set(LocalDateTime.now());

        ExecutorHandler executorHandler = new ExecutorHandlerImpl();
        ScheduledExecutorService executorService = executorHandler.createScheduledExecutorService(
                        this.konfiguration.get()
                                .getAsInteger(
                                        KonfigurationSchluessel.INITIAL_NUMBER_OF_THREADS,
                                        DEFAULT_INITIAL_NUMBER_OF_THREADS));
        this.scheduledExecutorService.set(executorService);
    }

    /**
     * Plant und führt einen Task aus, der zu einem bestimmten Zeitpunkt ausgeführt wird.
     *
     * @param task
     * @return ScheduledFuture/<String/>
     */
    @Override
    public synchronized ScheduledFuture<String> schedule(Task task) {
        LOG.debug("schedule: ", " executionDateTime: " + task.getExecutionDateTime());
        ScheduledFuture<String> scheduledFuture = null;
        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

            // TODO: securityAuthenticator muss in der Spring Bean erst noch injiziert werden
            securityAuthenticator.get().login();

            scheduledExecutorService.get().schedule(
                    task.getOperation(),
                    Duration.between(LocalDateTime.now(), task.getExecutionDateTime()).toNanos(),
                    TimeUnit.NANOSECONDS);
            counter.incrementAndGet();

        } catch (Exception e) {
            task.getOperation().setErrorMessage(e.getMessage());
            task.getOperation().setHasBeenExecutedSuccessfully(false);

            //TODO: Wie funktionieren die Meldungsschlüssel
            // String msg = MessageSourceHolder.getMessage(FehlerSchluessel.MEL_FEHLER_TASK00001);
            // LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.MEL_FEHLER_TASK00001, msg);

        } finally {
            MdcHelper.entferneKorrelationsId();

            // TODO: securityAuthenticator muss in der Spring Bean erst noch injiziert werden
            //securityAuthenticator.logout();
        }
        return scheduledFuture;
    }

    /**
     * Plant einen zeitgesteuerten und immer wiederkehrenden Task.
     *
     *
     * @param task
     * @return
     * @throws NoSuchMethodException
     * @throws Exception
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Task task) throws NoSuchMethodException {
        LOG.debug("schedule: ", " executionDateTime: " + task.getExecutionDateTime());
        ScheduledFuture<?> scheduledFuture = null;
        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

            // TODO: securityAuthenticator muss in der Spring Bean erst noch injiziert werden
            // securityAuthenticator.login();

            scheduledFuture = scheduledExecutorService.get().scheduleAtFixedRate(
                    task.getOperation(),
                    Duration.between(LocalDateTime.now(), task.getExecutionDateTime()).toNanos(),
                    task.getOperation().getFixedRate().toNanos(),
                    TimeUnit.NANOSECONDS);
            counter.incrementAndGet();

        } catch (Exception e) {
            task.getOperation().setErrorMessage(e.getMessage());
            task.getOperation().setHasBeenExecutedSuccessfully(false);

            //TODO: Wie funktionieren die Meldungsschlüssel
            // String msg = MessageSourceHolder.getMessage(FehlerSchluessel.MEL_FEHLER_TASK00001);
            // LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.MEL_FEHLER_TASK00001, msg);

        } finally {
            MdcHelper.entferneKorrelationsId();

            // TODO: securityAuthenticator muss in der Spring Bean erst noch injiziert werden
            //securityAuthenticator.logout();
        }
        return scheduledFuture;
    }

    /**
     * @param task
     * @return
     * @throws NoSuchMethodException
     * @throws Exception
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Task task) throws NoSuchMethodException {
        LOG.debug("schedule: ", " executionDateTime: " + task.getExecutionDateTime());
        ScheduledFuture<?> scheduledFuture = null;
        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

            // TODO: securityAuthenticator muss in der Spring Bean erst noch injiziert werden
            // securityAuthenticator.login();

            scheduledFuture = scheduledExecutorService.get().scheduleWithFixedDelay(
                    task.getOperation(),
                    Duration.between(LocalDateTime.now(), task.getExecutionDateTime()).toNanos(),
                    task.getOperation().getFixedRate().toNanos(),
                    TimeUnit.NANOSECONDS);
            counter.incrementAndGet();

        } catch (Exception e) {
            task.getOperation().setErrorMessage(e.getMessage());
            task.getOperation().setHasBeenExecutedSuccessfully(false);

            //TODO: Wie funktionieren die Meldungsschlüssel
            // String msg = MessageSourceHolder.getMessage(FehlerSchluessel.MEL_FEHLER_TASK00001);
            // LOG.info(LogKategorie.JOURNAL, FehlerSchluessel.MEL_FEHLER_TASK00001, msg);

        } finally {
            MdcHelper.entferneKorrelationsId();

            // TODO: securityAuthenticator muss in der Spring Bean erst noch injiziert werden
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
     *
     *
     * @return
     */
    @Override
    public boolean isTerminated() {
        LOG.debug("isTerminated ", " counter: " + counter.get());
        return scheduledExecutorService.get().isTerminated();
    }

}
