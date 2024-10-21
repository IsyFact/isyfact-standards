package de.bund.bva.isyfact.task.security.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.konstanten.HinweisSchluessel;
import de.bund.bva.isyfact.task.security.Authenticator;
import de.bund.bva.isyfact.task.security.AuthenticatorFactory;

/**
 * Creates Authenticators for authentication with isy-security.
 */
public class IsySecurityAuthenticatorFactory implements AuthenticatorFactory {
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsySecurityAuthenticatorFactory.class);

    private final IsyTaskConfigurationProperties configurationProperties;

    private final Authentifizierungsmanager authentifizierungsmanager;

    /** MessageSource to determine the messages. **/
    private final MessageSource messageSource;

    /**
     * Creates new instance.
     *
     * @param configurationProperties   {@link IsyTaskConfigurationProperties} provides credentials
     * @param authentifizierungsmanager {@link Authentifizierungsmanager} for authentication
     * @param messageSource {@link MessageSource} to determine the messages
     */
    public IsySecurityAuthenticatorFactory(
            IsyTaskConfigurationProperties configurationProperties,
            Authentifizierungsmanager authentifizierungsmanager,
            MessageSource messageSource
    ) {
        this.configurationProperties = configurationProperties;
        this.authentifizierungsmanager = authentifizierungsmanager;
        this.messageSource = messageSource;
    }

    /**
     * Provides an implementation of an {@link Authenticator} for a task, if the necessary credentials are configured.
     * If no credentials can be found, a {@link NoOpAuthenticator} will be returned.
     *
     * @param taskId Id of the task
     * @return {@link Authenticator} for task, return {@link NoOpAuthenticator} if no credentials have been found.
     */
    public synchronized Authenticator getAuthenticator(String taskId) {
        if (configurationProperties.getTasks().get(taskId) != null) {
            String oauth2ClientRegistrationId = configurationProperties.getTasks().get(taskId).getOauth2ClientRegistrationId();
            if (StringUtils.hasText(oauth2ClientRegistrationId)) {
                return new IsySecurityAuthenticator(authentifizierungsmanager, oauth2ClientRegistrationId);
            }
        }
        String defaultOauth2ClientRegistrationId = configurationProperties.getDefault().getOauth2ClientRegistrationId();
        if (StringUtils.hasText(defaultOauth2ClientRegistrationId)) {
            String nachricht = messageSource.getMessage(HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, new String[] { "oauth2ClientRegistrationId" }, Locale.GERMANY);
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, nachricht);
            return new IsySecurityAuthenticator(authentifizierungsmanager, defaultOauth2ClientRegistrationId
            );
        } else {
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG,
                messageSource.getMessage(HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG, null, Locale.GERMANY));
            return new NoOpAuthenticator();
        }
    }
}
