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
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface SecurityAuthenticator {
    void login();

    void logout();

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getBehoerdenkennzeichen();

    void setBehoerdenkennzeichen(String behoerdenkennzeichen);

    Sicherheit<AufrufKontext> getSicherheit();

    void setSicherheit(Sicherheit<AufrufKontext> sicherheit);

    AufrufKontextFactory<AufrufKontext> getAufrufKontextFactory();

    void setAufrufKontextFactory(AufrufKontextFactory<AufrufKontext> aufrufKontextFactory);

    AufrufKontextVerwalter<AufrufKontext> getAufrufKontextVerwalter();

    void setAufrufKontextVerwalter(AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter);
}
