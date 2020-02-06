package de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.util.Assert;

/**
 * Transferobjekt für Spring-Health-Objekte.
 * Spring Webflux benötigt POJOs (mit Settern) um JSON-Objekte einzulesen,
 * daher kann nicht direkt {@link org.springframework.boot.actuate.health.Health} verwendet werden.
 * Das HealthTO Objekt dient zum vorübergehenden speichern der Health-Daten.
 */
public class HealthTO {

    /** @see Health#getStatus() */
    private Status status;

    /** @see Health#getDetails() */
    private Map<String, Object> details = new HashMap<>();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        //sollte immer mindestens leere Map sein
        Assert.notNull(details, "Details must not be null");

        this.details = details;
    }

    @Override
    public String toString() {
        return "HealthStatus{" + "status=" + status + ", details=" + details + '}';
    }

    public Health convertToHealth() {
        return Health
            .status(getStatus())
            .withDetails(getDetails())
            .build();
    }
}
