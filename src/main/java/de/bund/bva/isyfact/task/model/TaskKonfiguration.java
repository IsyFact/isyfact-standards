package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.konstanten.Ausfuehrungsplan;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Beinhaltet die Konfiguration, die für alle Tasks gleich ist.
 */
public class TaskKonfiguration {

    private static final String KONFIGURATION_PRAEFIX = "isyfact.task.";

    private static final String KONFIGURATION_PLAN = ".plan";

    private static final String KONFIGURATION_HOST = ".host";

    private static final String KONFIGURATION_BENUTZER = ".benutzer";

    private static final String KONFIGURATION_PASSWORT = ".passwort";

    protected final Konfiguration konfiguration;

    protected final String praefix;

    public TaskKonfiguration(String taskName, Konfiguration konfiguration) {
        this.konfiguration = konfiguration;
        praefix = KONFIGURATION_PRAEFIX + taskName;
    }

    public Ausfuehrungsplan plan() {
        String plan = konfiguration.getAsString(praefix + KONFIGURATION_PLAN);
        return plan == null ? null : Ausfuehrungsplan.valueOf(plan);
    }

    public String beschraenkeAufHost() {
        return konfiguration.getAsString(praefix +KONFIGURATION_HOST);
    }

    public String benutzer() {
        return konfiguration.getAsString(praefix +KONFIGURATION_BENUTZER);
    }

    public String passwort() {
        return konfiguration.getAsString(praefix +KONFIGURATION_PASSWORT);
    }

}
