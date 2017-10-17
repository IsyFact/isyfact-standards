package de.bund.bva.isyfact.task.model.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.pliscommon.exception.PlisException;

/**
 * Ein TaskRunner entspricht einer auszuführenden Aufgabe.
 *
 *
 * Ein Objekt des Typs TaskRunner entspricht einer auszuführenden Aufgabe. Jede Aufgabe ruft fachliche Operationen auf.
 * Das bedeutet, dass sich alle aufgerufenen fachlichen Operationen eindeutig
 * einer bestimmten Ausführung einer Aufgabe zuordnen lassen.
 * Jeder auszuführende TaskRunner setzt seine id, seinen Namen und den Zeitpunkt der Ausführung.
 * Die Aufgaben, die erledigt werden sollen, werden in einer call- bzw. run-Methode einer CallableOperation implementiert.
 *
 * Alle aufgerufenen fachlichen Operationen lassen sich eindeutig einer bestimmten Aufgabe zuordnen.
 * Wenn ein TaskRunner erfolgreich durchlaufen wurde, wird dies notiert.
 * Gleichzeitig merkt sich der TaskRunner den Endzeitpunkt des Durchlaufs.
 */
public class TaskRunnerImpl implements TaskRunner {
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TaskRunnerImpl.class);

    private final String id;

    private final Authenticator authenticator;

    private final TaskKonfiguration.Ausfuehrungsplan ausfuehrungsplan;

    private final LocalDateTime executionDateTime;

    private final Duration initialDelay;

    private final Duration fixedRate;

    private final Duration fixedDelay;

    private final Task task;

    public TaskRunnerImpl(String id, Authenticator authenticator, Task task,
        TaskKonfiguration.Ausfuehrungsplan ausfuehrungsplan, LocalDateTime executionDateTime,
        Duration initialDelay, Duration fixedRate, Duration fixedDelay) {
        this.id = id;
        this.authenticator = authenticator;
        this.ausfuehrungsplan = ausfuehrungsplan;
        this.task = task;
        this.executionDateTime = executionDateTime;
        this.initialDelay = initialDelay;
        this.fixedRate = fixedRate;
        this.fixedDelay = fixedDelay;
    }

    @Override
    public void run() {
        if (task.isDeaktiviert()) {
            return;
        }

        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());
            authenticator.login();

            task.execute();

            task.zeichneErfolgreicheAusfuehrungAuf();
        } catch (Exception e) {
            task.zeichneFehlgeschlageneAusfuehrungAuf(e);

            if (e instanceof PlisException) {
                LOG.info(LogKategorie.JOURNAL,
                    "Während der Ausführung des Tasks ist eine Exception aufgetreten", (PlisException) e);
            } else {
                throw e;
            }
        } finally {
            authenticator.logout();
            MdcHelper.entferneKorrelationsId();
        }
    }

    @Override
    public synchronized String getId() {
        return id;
    }

    @Override
    public synchronized Task getTask() {
        return task;
    }

    @Override
    public synchronized TaskKonfiguration.Ausfuehrungsplan getAusfuehrungsplan() {
        return ausfuehrungsplan;
    }

    @Override
    public synchronized LocalDateTime getExecutionDateTime() {
        return executionDateTime;
    }

    @Override
    public synchronized Duration getInitialDelay() {
        return initialDelay;
    }

    @Override
    public synchronized Duration getFixedRate() {
        return fixedRate;
    }

    @Override
    public synchronized Duration getFixedDelay() {
        return fixedDelay;
    }

}
