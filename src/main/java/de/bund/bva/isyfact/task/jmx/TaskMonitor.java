package de.bund.bva.isyfact.task.jmx;

import java.time.LocalDateTime;

import de.bund.bva.pliscommon.exception.PlisException;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description = "Überwacht eine zur wiederholten Ausführung geplante Aufgabe")
public class TaskMonitor {

    private volatile LocalDateTime letzteAusfuehrungAbgeschlossen;

    private volatile boolean letzteAusfuehrungErfolgreich;

    private volatile Exception letzterFehler;

    public void zeichneErfolgreicheAusfuehrungAuf() {
        // TODO isy-datetime?
        letzteAusfuehrungAbgeschlossen = LocalDateTime.now();
        letzteAusfuehrungErfolgreich = true;
        letzterFehler = null;
    }

    public void zeichneFehlgeschlageneAusfuehrungAuf(Exception letzterFehler) {
        // TODO isy-datetime?
        letzteAusfuehrungAbgeschlossen = LocalDateTime.now();
        letzteAusfuehrungErfolgreich = false;
        this.letzterFehler = letzterFehler;
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
        if (letzterFehler != null && letzterFehler instanceof PlisException) {
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
