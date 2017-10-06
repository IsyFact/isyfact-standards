package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Der HostHandler ist ein Werkzeug zur Überprüfung von Host-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface HostHandler {
    boolean isHostApplicable(String id, Konfiguration konfiguration) throws HostNotApplicableException;
}
