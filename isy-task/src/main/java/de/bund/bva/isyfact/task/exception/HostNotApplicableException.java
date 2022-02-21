package de.bund.bva.isyfact.task.exception;

import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;

/**
 * This exception is thrown if a TaskRunner is to be executed on a specific target host, but the current
 * host does not match the target host.
 */
public class HostNotApplicableException extends TaskException {

    public HostNotApplicableException(String hostname) {
        super(FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, hostname);
    }

    public HostNotApplicableException(String hostname, Throwable cause) {
        super(FehlerSchluessel.HOSTNAME_STIMMT_NICHT_UEBEREIN, cause, hostname);
    }

}
