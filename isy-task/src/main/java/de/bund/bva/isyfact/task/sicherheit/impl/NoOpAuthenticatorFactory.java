package de.bund.bva.isyfact.task.sicherheit.impl;

import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;

/**
 * Erzeugt Instanzen von {@link NoOpAuthenticator}.
 */
public class NoOpAuthenticatorFactory implements AuthenticatorFactory {

    @Override
    public Authenticator getAuthenticator(String taskId) {
        return new NoOpAuthenticator();
    }
}
