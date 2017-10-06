package de.bund.bva.isyfact.task.model.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.handler.AusfuehrungsplanHandler.Ausfuehrungsplan;
import de.bund.bva.isyfact.task.handler.impl.AusfuehrungsplanHandlerImpl;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

/**
 * Ein Task entspricht einer auszuführenden Aufgabe.
 *
 *
 * Ein Objekt des Typs Task entspricht einer auszuführenden Aufgabe. Jede Aufgabe ruft fachliche Operationen auf.
 * Das bedeutet, dass sich alle aufgerufenen fachlichen Operationen eindeutig
 * einer bestimmten Ausführung einer Aufgabe zuordnen lassen.
 * Jeder auszuführende Task setzt seine id, seinen Namen und den Zeitpunkt der Ausführung.
 * Die Aufgaben, die erledigt werden sollen, werden in einer call- bzw. run-Methode einer CallableOperation implementiert.
 *
 * Alle aufgerufenen fachlichen Operationen lassen sich eindeutig einer bestimmten Aufgabe zuordnen.
 * Wenn ein Task erfolgreich durchlaufen wurde, wird dies notiert.
 * Gleichzeitig merkt sich der Task den Endzeitpunkt des Durchlaufs.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class TaskImpl implements Task {
    private volatile ThreadLocal<String> idThreadLocal = new ThreadLocal<>();

    private volatile ThreadLocal<String> korrelationsIdThreadLocal = new ThreadLocal<>();

    private volatile ThreadLocal<SecurityAuthenticator> securityAuthenticatorThreadLocal =
        new ThreadLocal<>();

    private volatile ThreadLocal<LocalDateTime> executionEndDateTimeThreadLocal = new ThreadLocal<>();

    private volatile ThreadLocal<Ausfuehrungsplan> ausfuehrungsplanThreadLocal = new ThreadLocal<>();

    private volatile ThreadLocal<Boolean> hasBeenExecutedSuccessfullyThreadLocal
        = new ThreadLocal<>();
    private volatile ThreadLocal<String> errorMessageThreadLocal
        = new ThreadLocal<>();

    private final Operation operation;

    /**
     * @param id
     * @param securityAuthenticator
     * @param executionDateTime
     * @param operation
     */
    public TaskImpl(String id, SecurityAuthenticator securityAuthenticator, Operation operation,
        AusfuehrungsplanHandlerImpl.Ausfuehrungsplan ausfuehrungsplan, LocalDateTime executionDateTime) {
        this.idThreadLocal.set(id);
        this.securityAuthenticatorThreadLocal.set(securityAuthenticator);
        this.ausfuehrungsplanThreadLocal.set(ausfuehrungsplan);
        this.operation = operation;
    }

    @Override
    public void run() {
        try {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());
            //securityAuthenticatorThreadLocal.get().login();

            operation.execute();

            //securityAuthenticatorThreadLocal.get().logout();
            MdcHelper.entferneKorrelationsId();

            setHasBeenExecutedSuccessfully(true);
            setErrorMessage(null);
        } catch (Exception e) {
            setHasBeenExecutedSuccessfully(false);
            setErrorMessage(e.getMessage());
        }
    }

    @Override
    public synchronized String getId() {
        return idThreadLocal.get();
    }

    @Override
    public synchronized void setId(String id) {
        this.idThreadLocal.set(id);
    }

    @Override
    public synchronized String getKorrelationsId() {
        return korrelationsIdThreadLocal.get();
    }

    @Override
    public synchronized void setKorrelationsId(String korrelationsId) {
        this.korrelationsIdThreadLocal.set(korrelationsId);
    }

    @Override
    public synchronized SecurityAuthenticator getSecurityAuthenticator() {
        return securityAuthenticatorThreadLocal.get();
    }

    @Override
    public synchronized void setSecurityAuthenticator(SecurityAuthenticator securityAuthenticator) {
        this.securityAuthenticatorThreadLocal.set(securityAuthenticator);
    }

    @Override
    public synchronized LocalDateTime getExecutionEndDateTime() {
        return executionEndDateTimeThreadLocal.get();
    }

    @Override
    public synchronized void setExecutionEndDateTime(LocalDateTime executionEndDateTime) {
        this.executionEndDateTimeThreadLocal.set(executionEndDateTime);
    }

    @Override
    public synchronized boolean getHasBeenExecutedSuccessfully() {
        return hasBeenExecutedSuccessfullyThreadLocal.get().booleanValue();
    }

    @Override
    public synchronized void setHasBeenExecutedSuccessfully(
        boolean hasBeenExecutedSuccessfully) {
        this.hasBeenExecutedSuccessfullyThreadLocal.set(hasBeenExecutedSuccessfully);
    }

    @Override
    public synchronized String getErrorMessage() {
        return errorMessageThreadLocal.get();
    }

    @Override
    public synchronized void setErrorMessage(String errorMessage) {
        this.errorMessageThreadLocal.set(errorMessage);
    }

    @Override
    public synchronized Operation getOperation() {
        return operation;
    }

    @Override
    public Ausfuehrungsplan getAusfuehrungsplan() {
        return this.ausfuehrungsplanThreadLocal.get();
    }

    @Override
    public void setAusfuehrungsplan(Ausfuehrungsplan ausfuehrungsplan) {
        this.ausfuehrungsplanThreadLocal.set(ausfuehrungsplan);
    }
}
