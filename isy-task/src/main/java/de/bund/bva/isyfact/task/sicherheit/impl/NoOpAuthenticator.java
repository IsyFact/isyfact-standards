package de.bund.bva.isyfact.task.sicherheit.impl;

import de.bund.bva.isyfact.task.sicherheit.Authenticator;

/**
 * Implementierung von {@link Authenticator}, die eine "leere" Authentifizierung durchführt.
 */
public class NoOpAuthenticator implements Authenticator {

    @Override
    public void login() {
        // leer
    }

    @Override
    public void logout() {
        // leer
    }
}
