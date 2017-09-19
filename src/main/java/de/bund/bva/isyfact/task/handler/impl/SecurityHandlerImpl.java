package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.SecurityHandler;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.isyfact.task.security.impl.SecurityAuthenticatorImpl;

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
	 * @param username
	 * @param password
	 * @return a SecurityAuthenticator
	 */
	@Override
	public synchronized SecurityAuthenticator createSecurityAuthenticator(
			String username,
			String password) {
		SecurityAuthenticator securityAuthenticator = new SecurityAuthenticatorImpl();
		securityAuthenticator.setUsername(username);
		securityAuthenticator.setPassword(password);
		return securityAuthenticator;
	}
}