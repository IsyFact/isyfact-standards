package de.bund.bva.isyfact.task.sicherheit.impl;

import org.springframework.util.StringUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.konstanten.HinweisSchluessel;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

/**
 * Creates Authenticators for authentication with isy-security.
 */
public class IsySecurityAuthenticatorFactory implements AuthenticatorFactory {
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsySecurityAuthenticatorFactory.class);

    private final IsyTaskConfigurationProperties configurationProperties;

    private final Authentifizierungsmanager authentifizierungsmanager;

    /**
     * Creates new instance.
     *
     * @param configurationProperties   {@link IsyTaskConfigurationProperties} provides credentials
     * @param authentifizierungsmanager {@link Authentifizierungsmanager} for authentication
     */
    public IsySecurityAuthenticatorFactory(
            IsyTaskConfigurationProperties configurationProperties,
            Authentifizierungsmanager authentifizierungsmanager
    ) {
        this.configurationProperties = configurationProperties;
        this.authentifizierungsmanager = authentifizierungsmanager;
    }

    /**
     * Provides an implementation of an {@link Authenticator} for a task, if the necessary credentials are configured.
     * If no credentials can be found, a {@link NoOpAuthenticator} will be returned.
     *
     * @param taskId Id of the task
     * @return {@link Authenticator} for task, return {@link NoOpAuthenticator} if no credentials have been found.
     */
    public synchronized Authenticator getAuthenticator(String taskId) {
        String registrationId = configurationProperties.getTasks().get(taskId).getRegistrationId();
        if (StringUtils.hasText(registrationId)) {
            return new IsySecurityAuthenticator(
                    authentifizierungsmanager,
                    registrationId
            );
        }
        String defaultRegistrationId = configurationProperties.getDefault().getRegistrationId();
        if (StringUtils.hasText(defaultRegistrationId)) {
            String nachricht = MessageSourceHolder
                    .getMessage(HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, "registrationId");
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, nachricht);
            return new IsySecurityAuthenticator(
                    authentifizierungsmanager,
                    defaultRegistrationId
            );
        } else {
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG,
                    MessageSourceHolder.getMessage(HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG));
            return new NoOpAuthenticator();
        }
    }
}