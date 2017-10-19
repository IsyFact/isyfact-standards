package de.bund.bva.isyfact.task.sicherheit.impl;

import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;

public class NoOpAuthenticatorFactory implements AuthenticatorFactory {

    @Override
    public Authenticator getAuthenticator(String taskId) {
        return new NoOpAuthenticator();
    }
}
