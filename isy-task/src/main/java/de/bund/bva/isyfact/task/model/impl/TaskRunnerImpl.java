package de.bund.bva.isyfact.task.model.impl;

import java.util.UUID;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;

public class TaskRunnerImpl implements TaskRunner {
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
