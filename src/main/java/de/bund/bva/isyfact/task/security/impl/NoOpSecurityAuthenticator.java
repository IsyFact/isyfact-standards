package de.bund.bva.isyfact.task.security.impl;

import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

public class NoOpSecurityAuthenticator implements SecurityAuthenticator {

    @Override
    public void login() {
        // leer
    }

    @Override
    public void logout() {
        // leer
    }
}
