package de.bund.bva.isyfact.task.konfiguration;

import de.bund.bva.isyfact.task.exception.HostNotApplicableException;

/**
 * Der HostHandler ist ein Werkzeug zur Überprüfung von Host-Instanzen.
 */
public interface HostHandler {

    /**
     * Prüft, ob der Task auf dem richtigen Host läuft.
     *
     * @param expectedHostName
     *            der Name des Hosts, auf dem der Task laufen soll.
     * @return true, wenn der Host den richtigen Namen hat, sonst false
     * @throws HostNotApplicableException
     *             wenn bei der Prüfung ein Fehler aufgetreten ist
     */
    boolean isHostApplicable(String expectedHostName) throws HostNotApplicableException;
}
