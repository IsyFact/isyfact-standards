package de.bund.bva.isyfact.task.model.impl;

import de.bund.bva.isyfact.task.model.FixedRate;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

import java.time.LocalDateTime;

/**
 * Ein Task entspricht einer auszuführenden Aufgabe.
 *
 *
 * Ein Objekt des Typs Task entspricht einer auszuführenden Aufgabe. Jede Aufgabe ruft fachliche Operationen auf.
 * Das bedeutet, dass sich alle aufgerufenen fachlichen Operationen eindeutig
 * einer bestimmten Ausführung einer Aufgabe zuordnen lassen.
 * Jeder auszuführende Task setzt seine id, seinen Namen und den Zeitpunkt der Ausführung.
 * Die Aufgaben, die erledigt werden sollen, werden in einer call- bzw. run-Methode einer CallableOperation implementiert.

 * Alle aufgerufenen fachlichen Operationen lassen sich eindeutig einer bestimmten Aufgabe zuordnen.
 * Wenn ein Task erfolgreich durchlaufen wurde, wird dies notiert.
 * Gleichzeitig merkt sich der Task den Endzeitpunkt des Durchlaufs.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class TaskImpl implements Task {
    private volatile ThreadLocal<String> id
            = new ThreadLocal<>();
    private volatile ThreadLocal<String> korrelationsId
            = new ThreadLocal<>();
    private volatile ThreadLocal<SecurityAuthenticator> securityAuthenticator
            = new ThreadLocal<>();
    private volatile ThreadLocal<LocalDateTime> executionDateTime
            = new ThreadLocal<>();
    private volatile ThreadLocal<LocalDateTime> executionEndDateTime
            = new ThreadLocal<>();
    private volatile ThreadLocal<Operation> operation
            = new ThreadLocal<>();

    /**
     *
     * @param id
     * @param securityAuthenticator
     * @param executionDateTime
     * @param operation
     */
    public TaskImpl(
            String id,
            SecurityAuthenticator securityAuthenticator,
            LocalDateTime executionDateTime,
            Operation operation
    ) {
        this.id.set(id);
        this.securityAuthenticator.set(securityAuthenticator);
        this.executionDateTime.set(executionDateTime);
        this.operation.set(operation);
    }

    @Override
    public synchronized String getId() {
        return id.get();
    }

    @Override
    public synchronized void setId(String id) {
        this.id.set(id);
    }

    @Override
    public synchronized String getKorrelationsId() {
        return korrelationsId.get();
    }

    @Override
    public synchronized void setKorrelationsId(String korrelationsId) {
        this.korrelationsId.set(korrelationsId);
    }

    @Override
    public synchronized SecurityAuthenticator getSecurityAuthenticator() {
        return securityAuthenticator.get();
    }

    @Override
    public synchronized void setSecurityAuthenticator(SecurityAuthenticator securityAuthenticator) {
        this.securityAuthenticator.set(securityAuthenticator);
    }

    @Override
    public synchronized LocalDateTime getExecutionDateTime() {
        return this.executionDateTime.get();
    }

    @Override
    public synchronized void setExecutionDateTime(LocalDateTime executionDateTime) {
        this.executionDateTime.set(executionDateTime);
    }

    @Override
    public synchronized LocalDateTime getExecutionEndDateTime() {
        return executionEndDateTime.get();
    }

    @Override
    public synchronized void setExecutionEndDateTime(LocalDateTime executionEndDateTime) {
        this.executionEndDateTime.set(executionEndDateTime);
    }

    @Override
    public synchronized Operation getOperation() {
        return this.operation.get();
    }

    @Override
    public synchronized void setOperation(Operation operation) {
        this.operation.set(operation);
    }

}
