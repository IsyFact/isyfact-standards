package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Der SecurityHandler dient der Erzeugung von SecurityAuthenticator-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface SecurityHandler {
    SecurityAuthenticator getSecurityAuthenticator(String id, Konfiguration konfiguration);
}
