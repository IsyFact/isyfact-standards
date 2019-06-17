package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.util.exception.MessageSourceFehlertextProvider;

/**
 * Die Exception wird geworfen, wenn ein TaskRunner auf einem bestimmten Ziel-Host ausgeführt werden soll,
 * der aktuelle Hostname, aber nicht dem Hostnamen des Ziel-Hosts entspricht.
 */
public class HostNotApplicableException extends TechnicalException {

    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new MessageSourceFehlertextProvider();

    public HostNotApplicableException(String hostname) {
        super(FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, FEHLERTEXT_PROVIDER, hostname);
    }

    public HostNotApplicableException(String hostname, Throwable cause) {
        super(FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, cause, FEHLERTEXT_PROVIDER, hostname);
    }

}
