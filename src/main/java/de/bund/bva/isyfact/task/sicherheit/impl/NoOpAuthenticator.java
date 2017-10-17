package de.bund.bva.isyfact.task.sicherheit.impl;

import de.bund.bva.isyfact.task.sicherheit.Authenticator;

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
