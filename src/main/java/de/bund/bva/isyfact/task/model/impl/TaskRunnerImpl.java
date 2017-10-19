package de.bund.bva.isyfact.task.model.impl;

import java.util.UUID;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;

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

    private final Task task;

    private final TaskKonfiguration taskKonfiguration;

    private final Authenticator authenticator;

    public TaskRunnerImpl(Task task, TaskKonfiguration taskKonfiguration) {
        this.task = task;
        this.taskKonfiguration = taskKonfiguration;
        authenticator = taskKonfiguration.getAuthenticator();
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
            throw e;
        } finally {
            authenticator.logout();
            MdcHelper.entferneKorrelationsId();
        }
    }



    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public synchronized TaskKonfiguration getTaskKonfiguration() {
        return taskKonfiguration;
    }

}
