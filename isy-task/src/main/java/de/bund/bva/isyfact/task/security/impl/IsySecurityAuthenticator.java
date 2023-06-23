package de.bund.bva.isyfact.task.security.impl;

import org.springframework.security.core.context.SecurityContextHolder;

import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.task.security.Authenticator;

/**
 * Implementation of {@link Authenticator} for the use of isy-security.
 */
public class IsySecurityAuthenticator implements Authenticator {

    private final Authentifizierungsmanager authentifizierungsmanager;

    private final String registrationId;

    /**
     * Creates an instance of an {@link Authenticator} for isy-security.
     *
     * @param authentifizierungsmanager    Authneticationmanager of isy-security
     * @param registrationId Registration-ID of the client
     */
    public IsySecurityAuthenticator(
            Authentifizierungsmanager authentifizierungsmanager,
            String registrationId
    ) {
        this.authentifizierungsmanager = authentifizierungsmanager;
        this.registrationId = registrationId;
    }

    @Override
    public synchronized void login() {
        authentifizierungsmanager.authentifiziere(registrationId);
    }

    @Override
    public synchronized void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
