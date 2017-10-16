package de.bund.bva.isyfact.task.konfiguration;

import de.bund.bva.isyfact.task.exception.HostNotApplicableException;

/**
 * Der HostHandler ist ein Werkzeug zur Überprüfung von Host-Instanzen.
 */
public interface HostHandler {
    boolean isHostApplicable(String expectedHostName) throws HostNotApplicableException;
}
