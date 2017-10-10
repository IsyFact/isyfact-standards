package de.bund.bva.isyfact.task.model.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.handler.AusfuehrungsplanHandler.Ausfuehrungsplan;
import de.bund.bva.isyfact.task.handler.impl.AusfuehrungsplanHandlerImpl;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

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
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class TaskRunnerImpl implements TaskRunner {
    private final String id;

    private final SecurityAuthenticator securityAuthenticator;

    private final Ausfuehrungsplan ausfuehrungsplan;

    private final LocalDateTime executionDateTime;

    private final Duration initialDelay;

    private final Duration fixedRate;

    private final Duration fixedDelay;

    private final Task task;

    public TaskRunnerImpl(String id, SecurityAuthenticator securityAuthenticator, Task task,
        AusfuehrungsplanHandlerImpl.Ausfuehrungsplan ausfuehrungsplan, LocalDateTime executionDateTime,
        Duration initialDelay, Duration fixedRate, Duration fixedDelay) {
        this.id = id;
        this.securityAuthenticator = securityAuthenticator;
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
            //securityAuthenticatorThreadLocal.get().login();

            task.execute();

            //securityAuthenticatorThreadLocal.get().logout();
            MdcHelper.entferneKorrelationsId();

            task.zeichneErfolgreicheAusfuehrungAuf();
        } catch (Exception e) {
            task.zeichneFehlgeschlageneAusfuehrungAuf(e);
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
    public Ausfuehrungsplan getAusfuehrungsplan() {
        return ausfuehrungsplan;
    }

    @Override
    public LocalDateTime getExecutionDateTime() {
        return executionDateTime;
    }

    @Override
    public Duration getInitialDelay() {
        return initialDelay;
    }

    @Override
    public Duration getFixedRate() {
        return fixedRate;
    }

    @Override
    public Duration getFixedDelay() {
        return fixedDelay;
    }

}
