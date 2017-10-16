package de.bund.bva.isyfact.task.security.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.konstanten.HinweisSchluessel;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.isyfact.task.security.SecurityAuthenticatorFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationException;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

/**
 * Erzeugt SecurityAuthenticator-Instanzen für die Verwendung von isy-sicherheit.
 */
public class IsySicherheitSecurityAuthenticatorFactory implements SecurityAuthenticatorFactory {
	private final static IsyLogger LOG = IsyLoggerFactory.getLogger(IsySicherheitSecurityAuthenticatorFactory.class);

	private final Konfiguration konfiguration;

	private final Sicherheit<AufrufKontext> sicherheit;

	private final AufrufKontextFactory<AufrufKontext> aufrufKontextFactory;

	private final AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

	public IsySicherheitSecurityAuthenticatorFactory(Konfiguration konfiguration,
		Sicherheit<AufrufKontext> sicherheit,
		AufrufKontextFactory<AufrufKontext> aufrufKontextFactory,
		AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter) {
		this.konfiguration = konfiguration;
		this.sicherheit = sicherheit;
		this.aufrufKontextFactory = aufrufKontextFactory;
		this.aufrufKontextVerwalter = aufrufKontextVerwalter;
	}

	public synchronized SecurityAuthenticator getSecurityAuthenticator(String taskId) {
        String benutzer;
        String passwort;
        String bhkz;

        try {
            benutzer = konfiguration.getAsString(PRAEFIX + taskId + BENUTZER);
            passwort = konfiguration.getAsString(PRAEFIX + taskId + PASSWORT);
            bhkz = konfiguration.getAsString(PRAEFIX + taskId + BEHOERDENKENNZEICHEN);

            return new IsySicherheitSecurityAuthenticator(benutzer, passwort, bhkz, aufrufKontextVerwalter,
                aufrufKontextFactory, sicherheit);
        } catch (KonfigurationException e) {
            String nachricht = MessageSourceHolder
                .getMessage(HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, "benutzer, passwort, bhkz");
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION, nachricht);
        }

        try {
            benutzer = konfiguration.getAsString(STANDARD_BENUTZER);
            passwort = konfiguration.getAsString(STANDARD_PASSWORT);
            bhkz = konfiguration.getAsString(STANDARD_BHKZ);

            return new IsySicherheitSecurityAuthenticator(benutzer, passwort, bhkz, aufrufKontextVerwalter,
                aufrufKontextFactory, sicherheit);
        } catch (KonfigurationException e) {
            LOG.info(LogKategorie.SICHERHEIT, HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG,
                MessageSourceHolder.getMessage(HinweisSchluessel.VERWENDE_KEINE_AUTHENTIFIZIERUNG));
        }

        return new NoOpSecurityAuthenticator();
    }
}