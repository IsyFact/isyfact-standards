package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model;

import java.net.URI;

/**
 * Informationen zu einem Nachbarsystem.
 */
public class Nachbarsystem {

    /** Name des Nachbarsystems. Wird für Loggingausgabe verwendet. */
    private String systemname;

    /** URI des Nachbarsystems für Anfragen. */
    private URI healthEndpoint;

    /**
     * Ist das Nachbarsystem für den eigenen Betrieb essentiell?
     * Wenn ja, werden Errors geloggt, wenn das Nachbarsystem nicht erreichbar ist.
     */
    private boolean essentiell;

    public String getSystemname() {
        return systemname;
    }

    public void setSystemname(String systemname) {
        this.systemname = systemname;
    }

    public URI getHealthEndpoint() {
        return healthEndpoint;
    }

    public void setHealthEndpoint(URI healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    public boolean isEssentiell() {
        return essentiell;
    }

    public void setEssentiell(boolean essentiell) {
        this.essentiell = essentiell;
    }
}
