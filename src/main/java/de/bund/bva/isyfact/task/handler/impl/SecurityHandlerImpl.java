package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.SecurityHandler;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.isyfact.task.security.impl.SecurityAuthenticatorImpl;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.BENUTZER;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PASSWORT;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;

/**
 * Die Klasse SecurityHandlerImpl ist eine Werkzeugklasse für die Erzeugung von SecurityAuthenticator-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class SecurityHandlerImpl implements SecurityHandler {
	private final static IsyLogger LOG = IsyLoggerFactory.getLogger(SecurityHandlerImpl.class);

	/**
	 * Erstellt einen SecurityAuthenticator und setzt ihn als Attribut in den Task.
	 *
	 * @return a SecurityAuthenticator
	 */
	@Override
	public synchronized SecurityAuthenticator getSecurityAuthenticator(String id, Konfiguration konfiguration) {
		String benutzer = konfiguration.getAsString(PRAEFIX + id + BENUTZER);
		String passwort = konfiguration.getAsString(PRAEFIX + id + PASSWORT);

		SecurityAuthenticator securityAuthenticator = new SecurityAuthenticatorImpl();
		securityAuthenticator.setUsername(benutzer);
		securityAuthenticator.setPassword(passwort);
		return securityAuthenticator;
	}
}