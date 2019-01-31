package de.bund.bva.isyfact.task.model;

import java.time.LocalDateTime;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.pliscommon.exception.PlisException;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Monitor-Klasse zur Überwachung von Tasks mit JMX.
 */
@ManagedResource(description = "Überwacht eine zur wiederholten Ausführung geplante Aufgabe")
public class TaskMonitor {

    private volatile LocalDateTime letzteAusfuehrungAbgeschlossen;

    private volatile boolean letzteAusfuehrungErfolgreich;

    private volatile Exception letzterFehler;

    private volatile Task task;

    void setTask(Task task) {
        this.task = task;
    }

    public void zeichneErfolgreicheAusfuehrungAuf() {
        letzteAusfuehrungAbgeschlossen = DateTimeUtil.localDateTimeNow();
        letzteAusfuehrungErfolgreich = true;
        letzterFehler = null;
    }

    public void zeichneFehlgeschlageneAusfuehrungAuf(Exception letzterFehler) {
        letzteAusfuehrungAbgeschlossen = DateTimeUtil.localDateTimeNow();
        letzteAusfuehrungErfolgreich = false;
        this.letzterFehler = letzterFehler;
    }

    @ManagedAttribute(description = "Manuelle Deaktivierung des Tasks")
    public boolean isDeaktiviert() {
        return this.task.isDeaktiviert();
    }

    @ManagedOperation(description = "Deaktiviert den Task")
    public void deaktivieren() {
        this.task.deaktivieren();
    }

    @ManagedOperation(description = "Aktiviert den Task")
    public void aktivieren() {
        this.task.aktivieren();
    }

    @ManagedAttribute(description = "Ende der letzten Ausführung")
    public LocalDateTime getLetzteAusfuehrungAbgeschlossen() {
        return letzteAusfuehrungAbgeschlossen;
    }

    @ManagedAttribute(description = "Erfolg der letzten Ausführung")
    public boolean isLetzteAusfuehrungErfolgreich() {
        return letzteAusfuehrungErfolgreich;
    }

    @ManagedAttribute(description = "Bei Misserfolg: Ausnahme-ID des letzten Fehlers")
    public String getLetzterFehlerAusnahmeId() {
        if (letzterFehler instanceof PlisException) {
            return ((PlisException) letzterFehler).getAusnahmeId();
        } else {
            return null;
        }
    }

    @ManagedAttribute(description = "Bei Misserfolg: Nachricht des letzten Fehlers")
    public String getLetzterFehlerNachricht() {
        return letzterFehler != null ? letzterFehler.getMessage() : null;
    }

}
