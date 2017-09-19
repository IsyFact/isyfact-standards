package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

/**
 * Der SecurityHandler dient der Erzeugung von SecurityAuthenticator-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface SecurityHandler {
    SecurityAuthenticator createSecurityAuthenticator(
            String username,
            String password);
}
