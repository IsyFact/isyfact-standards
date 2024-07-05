package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/**
 * Enthält Informationen zur Health eines Nachbarsystems.
 * Spring Webflux benötigt POJOs (mit Settern) um JSON-Objekte einzulesen,
 * daher kann nicht direkt {@link org.springframework.boot.actuate.health.Health} verwendet werden,
 * sondern Status und Details müssen separat eingelesen werden
 */
public class NachbarsystemHealth {

    /**
     * Nachbarsystem, für dass die Healthinformationen gelten.
     */
    private Nachbarsystem nachbarsystem;

    /**
     * @see Health#getStatus()
     */
    private Status status = Status.UNKNOWN;

    /**
     * @see Health#getDetails()
     */
    private Map<String, Object> details = new HashMap<>();

    public Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull Status status) {
        this.status = status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(@NotNull Map<String, Object> details) {
        this.details = details;
    }

    public Nachbarsystem getNachbarsystem() {
        return nachbarsystem;
    }

    public void setNachbarsystem(@NotNull Nachbarsystem nachbarsystem) {
        this.nachbarsystem = nachbarsystem;
    }

}
