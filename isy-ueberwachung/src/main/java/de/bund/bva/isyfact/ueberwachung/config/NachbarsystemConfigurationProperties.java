package de.bund.bva.isyfact.ueberwachung.config;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;

/** Enthält die Nachbarsysteme, die mittels des Nachbarsystemchecks überprüft werden. */
public class NachbarsystemConfigurationProperties {

    /** Alle konfigurierten Nachbarsysteme. */
    private final Map<String, Nachbarsystem> nachbarsysteme = new HashMap<>();

    public Map<String, Nachbarsystem> getNachbarsysteme() {
        return nachbarsysteme;
    }
}
