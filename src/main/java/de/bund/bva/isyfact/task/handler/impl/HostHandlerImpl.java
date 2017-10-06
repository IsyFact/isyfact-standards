package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.handler.HostHandler;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.HOST;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;

/**
 * Der HostHandler ist eine Werkzeugeklasse, die eine Host-Instanz überprüft.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class HostHandlerImpl implements HostHandler {
	private final static IsyLogger LOG = IsyLoggerFactory.getLogger(HostHandlerImpl.class);

	/**
	 * Überprüft, ob der TaskData auf dem aktuellen Host ausgeführt werden darf.
	 *
	 */
	@Override
	public synchronized boolean isHostApplicable(String id, Konfiguration konfiguration)
			throws HostNotApplicableException {
		String expectedHostName = konfiguration.getAsString(PRAEFIX + id + HOST);
		if(expectedHostName == null || expectedHostName.equalsIgnoreCase("localhost")) {
			return true;
		}
		LOG.debug("isHostApplicable: ", " expectedHostName: " + expectedHostName);

		InetAddress inetAdress = null;
		try {
			inetAdress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new HostNotApplicableException(expectedHostName, e);
		}
		LOG.debug("isHostApplicable: ", " inetAdress: " + inetAdress);
		if (inetAdress == null) {
			return false;
		}

		String currentHostName = inetAdress.getHostName();
		LOG.debug("isHostApplicable: ", " currentHostName: " + currentHostName);
		if (currentHostName == null || currentHostName.length() == 0) {
			LOG.debug("isHostApplicable: ", " inetAdress: " + inetAdress);
			return false;
		}

		if(!expectedHostName.equals(currentHostName)) {
			LOG.debug("isHostApplicable: ", " hostNames don't match! expectedHostName: "
			 + expectedHostName + " currentHostName: " + currentHostName);
			throw new HostNotApplicableException(expectedHostName);
		}

		return true;
	}
}