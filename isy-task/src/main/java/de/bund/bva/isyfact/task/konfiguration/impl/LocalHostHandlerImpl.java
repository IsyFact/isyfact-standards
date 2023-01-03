package de.bund.bva.isyfact.task.konfiguration.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;

/**
 * Der HostHandler ist eine Werkzeugklasse, die eine Host-Instanz überprüft.
 */
public class LocalHostHandlerImpl implements HostHandler {
    /** Logger der Klasse. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(LocalHostHandlerImpl.class);

    /**
     * Überprüft, ob der Task auf dem Host ausgeführt werden darf.
     */
    @Override
    public synchronized boolean isHostApplicable(String expectedHostName) throws HostNotApplicableException {

        InetAddress inetAdress;

        try {
            inetAdress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new HostNotApplicableException(expectedHostName, e);
        }

        LOG.debug("isHostApplicable: inetAdress: {}", inetAdress);

        if (inetAdress == null) {
            return false;
        }

        String currentHostName = inetAdress.getHostName();

        LOG.debug("isHostApplicable: currentHostName: {}", currentHostName);

        if (currentHostName == null || currentHostName.isEmpty()) {
            LOG.debug("isHostApplicable: inetAdress: {}", inetAdress);
            return false;
        }

        if (!currentHostName.matches(expectedHostName)) {
            LOG.debug("isHostApplicable: hostNames do not match! expectedHostName: {} currentHostName: {}",
                expectedHostName, currentHostName);
            return false;
        }

        return true;
    }
}
