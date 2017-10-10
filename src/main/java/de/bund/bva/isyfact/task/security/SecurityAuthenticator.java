/**
 *
 */
package de.bund.bva.isyfact.task.security;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;

/**
 * Die Klasse SecurityAuthenticator ist eine Thread-sichere Klasse für die Verwendung von IsyFact-Sicherheit.
 */
public interface SecurityAuthenticator {
    void login();

    void logout();
}
