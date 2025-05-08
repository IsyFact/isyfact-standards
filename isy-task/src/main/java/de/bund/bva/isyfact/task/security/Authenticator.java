
package de.bund.bva.isyfact.task.security;

/**
 * Interface über das Tasks bei der Ausführung authentifizieren können.
 */
public interface Authenticator {

    /**
     * Authentifizierung vor Ausführung des Tasks.
     */
    void login();

    /**
     * Logout nach Beendigung des Tasks.
     */
    void logout();
}
