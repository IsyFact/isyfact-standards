package de.bund.bva.isyfact.task.security.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.isyfact.task.security.SecurityAuthenticatorFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

/**
 * Die Klasse IsySicherheitSecurityAuthenticatorFactory ist eine Werkzeugklasse für die Erzeugung von SecurityAuthenticator-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
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

	/**
	 * Erstellt einen SecurityAuthenticator und setzt ihn als Attribut in den TaskRunner.
	 *
	 * @return a SecurityAuthenticator
	 */
	public synchronized SecurityAuthenticator getSecurityAuthenticator(String taskId) {
		String benutzer = konfiguration.getAsString(PRAEFIX + taskId + BENUTZER);
		String passwort = konfiguration.getAsString(PRAEFIX + taskId + PASSWORT);
		String bhkz = konfiguration.getAsString(PRAEFIX + taskId + BEHOERDENKENNYEICHEN);

		return new IsySicherheitSecurityAuthenticator(benutzer, passwort, bhkz, aufrufKontextVerwalter,
			aufrufKontextFactory, sicherheit);
	}
}