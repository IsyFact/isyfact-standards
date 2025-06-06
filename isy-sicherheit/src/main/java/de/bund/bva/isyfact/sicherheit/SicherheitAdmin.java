package de.bund.bva.isyfact.sicherheit;

/**
 * Interface zur Überprüfung des Verbindungsaufbaus zum Access Manager.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface SicherheitAdmin {

    /**
     * Dient zur Überprüfung der Verbindung zum Access Manager.
     *
     * @return <code>true</code>, falls die Verbindung erfolgreich aufgebaut werden konnte, ansonsten
     *         <code>false</code>.
     */
    boolean pingAccessManager();
}
