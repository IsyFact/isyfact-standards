package de.bund.bva.isyfact.task.security.impl;

import org.springframework.security.core.context.SecurityContextHolder;

import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.task.security.Authenticator;

/**
 * Implementation of {@link Authenticator} for the use of isy-security.
 */
public class IsySecurityAuthenticator implements Authenticator {

    private final Authentifizierungsmanager authentifizierungsmanager;

    private final String oauth2ClientRegistrationId;

    /**
     * Creates an instance of an {@link Authenticator} for isy-security.
     *
     * @param authentifizierungsmanager
     *         authentication manager of isy-security
     * @param oauth2ClientRegistrationId
     *         OAuth 2.0 Client Registration ID of the client to use for authentication
     */
    public IsySecurityAuthenticator(
            Authentifizierungsmanager authentifizierungsmanager,
            String oauth2ClientRegistrationId
    ) {
        this.authentifizierungsmanager = authentifizierungsmanager;
        this.oauth2ClientRegistrationId = oauth2ClientRegistrationId;
    }

    @Override
    public synchronized void login() {
        authentifizierungsmanager.authentifiziere(oauth2ClientRegistrationId);
    }

    @Override
    public synchronized void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
