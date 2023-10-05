package de.bund.bva.isyfact.task.security.impl;

import de.bund.bva.isyfact.task.security.Authenticator;
import de.bund.bva.isyfact.task.security.AuthenticatorFactory;

/**
 * Erzeugt Instanzen von {@link NoOpAuthenticator}.
 */
public class NoOpAuthenticatorFactory implements AuthenticatorFactory {

    @Override
    public Authenticator getAuthenticator(String taskId) {
        return new NoOpAuthenticator();
    }
}
