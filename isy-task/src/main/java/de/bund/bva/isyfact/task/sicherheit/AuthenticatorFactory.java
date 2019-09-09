package de.bund.bva.isyfact.task.sicherheit;

/**
 * Interface über das Instanzen von {@link Authenticator} erzeugt werden.
 */
public interface AuthenticatorFactory {

    /**
     * Gibt eine {@link Authenticator}-Instanz für einen bestimmen Task zurück.
     *
     * @param taskId
     *            die Id des Tasks
     * @return eine {@link Authenticator}-Instanz
     */
    Authenticator getAuthenticator(String taskId);
}
