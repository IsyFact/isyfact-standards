package de.bund.bva.isyfact.task.sicherheit.impl;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.konstanten.HinweisSchluessel;
import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.isyfact.task.sicherheit.AuthenticatorFactory;
import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;

/**
 * Erzeugt Authenticator-Instanzen für die Verwendung von isy-sicherheit.
 */
public class IsySicherheitAuthenticatorFactory implements AuthenticatorFactory {
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsySicherheitAuthenticatorFactory.class);

    private final IsyTaskConfigurationProperties configurationProperties;

    private final Sicherheit<AufrufKontext> sicherheit;

    private final AufrufKontextFactory<AufrufKontext> aufrufKontextFactory;

    private final AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    /**
     * Erstellt eine neue Instanz.
     *
     * @param configurationProperties die {@link IsyTaskConfigurationProperties}, aus der die Daten zur Authentifizierung gelesen werden
     * @param sicherheit              die {@link Sicherheit}
     * @param aufrufKontextFactory    die {@link AufrufKontextFactory}
     * @param aufrufKontextVerwalter  der {@link AufrufKontextVerwalter}
     */
    public IsySicherheitAuthenticatorFactory(IsyTaskConfigurationProperties configurationProperties,
        Sicherheit<AufrufKontext> sicherheit, AufrufKontextFactory<AufrufKontext> aufrufKontextFactory,
        AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter) {
        this.configurationProperties = configurationProperties;
        this.sicherheit = sicherheit;
        this.aufrufKontextFactory = aufrufKontextFactory;
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    /**
     * Stellt einen {@link IsySicherheitAuthenticator} für einen bestimmtenn Tasks bereit, wenn die
     * notwendigen Daten (Benutzername, ...) zur Authentifizierung gefunden werden können. Werden keine Daten
     * gefunden, wird ein {@link NoOpAuthenticator} zurückgegeben.
     *
     * @param taskId die Id des Tasks
     * @return den {@link IsySicherheitAuthenticator} für den Task, wenn die Daten zur Authentifizierung
     * vorhanden sind, sonst ein {@link NoOpAuthenticator}.
     */
    public synchronized Authenticator getAuthenticator(String taskId) {
        if (useTaskSpecificCredentials(taskId)) {
            return new IsySicherheitAuthenticator(
                configurationProperties.getTasks().get(taskId).getBenutzer(),
                configurationProperties.getTasks().get(taskId).getPasswort(),
                configurationProperties.getTasks().get(taskId).getBhkz(), aufrufKontextVerwalter,
                aufrufKontextFactory, sicherheit);
        } else if (useStandardCredentials()) {
            String nachricht = MessageSourceHolder
                .getMessage(HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, "benutzer, passwort, bhkz");
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, nachricht);
            return new IsySicherheitAuthenticator(
                configurationProperties.getDefault().getBenutzer(),
                configurationProperties.getDefault().getPasswort(),
                configurationProperties.getDefault().getBhkz(), aufrufKontextVerwalter,
                aufrufKontextFactory, sicherheit);
        } else {
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG,
                MessageSourceHolder.getMessage(HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG));
            return new NoOpAuthenticator();
        }
    }

    private boolean useTaskSpecificCredentials(String taskId) {
        return configurationProperties.getTasks().get(taskId).getBenutzer() != null
            && configurationProperties.getTasks().get(taskId).getPasswort() != null
            && configurationProperties.getTasks().get(taskId).getBhkz() != null;
    }

    private boolean useStandardCredentials() {
        return configurationProperties.getDefault().getBenutzer() != null
            && configurationProperties.getDefault().getPasswort() != null
            && configurationProperties.getDefault().getBhkz() != null;
    }

}