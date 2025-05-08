package de.bund.bva.isyfact.sicherheit.accessmgr;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;

/**
 * Interface zum Zugriff auf den AccessManager.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface AccessManager<K extends AufrufKontext, E extends AuthentifzierungErgebnis> {
    /**
     * 
     * @param unauthentifizierterAufrufKontext
     *            Das unauthentifizierte AufrufKontext-Objekt mit den Authentifizierungsdaten
     * @return Ein Token mit dem Ergebnis der Authentifzierung.
     * 
     * @throws AuthentifizierungTechnicalException
     *             Falls es technische Problemen bei der Authentifizierung gibt.
     * @throws AuthentifizierungFehlgeschlagenException
     *             Falls der {@link AccessManager} die Authentifizierung abgelehnt hat.
     */
    E authentifiziere(K unauthentifizierterAufrufKontext) throws AuthentifizierungTechnicalException,
        AuthentifizierungFehlgeschlagenException;

    /**
     * Führt ein Logout für die angegebene Authentifzierung durch.
     * 
     * @param authentifzierungErgebnis
     *            Die Authentifizierung für die der Logout durchgeführt werden soll
     */
    void logout(E authentifzierungErgebnis);

    /**
     * Dient zur Überprüfung der Verbindung zum Authentifizierungs-/Autorisierungs-Server.
     * 
     * @return <code>true</code>, falls die Verbindung erfolgreich aufgebaut werden konnte, ansonsten
     *         <code>false</code>.
     */
    boolean pingAccessManager();

    /**
     * 
     * Dient zur Überprüfung der Verbindung zum Authentifizierungs-/Autorisierungs-Server, indem ein Login/
     * Logout durchgeführt wird. Hierdurch wird geprüft, ob der Server Anfragen korrekt verarbeiten kann.
     * 
     * @param unauthentifizierterAufrufKontext
     *            Das unauthentifizierte AufrufKontext-Objekt mit den Authentifizierungsdaten
     * 
     * @return <code>true</code>, falls ein Login/ Logout erfolgreich durchgeführt werden konnte, ansonsten
     *         <code>false</code>.
     */
    boolean pingAccessManagerByLoginLogout(K unauthentifizierterAufrufKontext);

    /**
     * Befüllt den übergebenen AufrufKontext mit den Ergebnissen des Authentifizierungsaufurf.
     * @param aufrufKontext
     *            Der zu befüllende Aufrufkontext
     * @param authentifzierungErgebnis
     *            Das Ergebnis der Authentifzierung
     */
    void befuelleAufrufkontext(K aufrufKontext, E authentifzierungErgebnis);

    /**
     * Erzeugt den Schlüssel unter dem der Aufrufkontext gecached wird. Im Cache von isy-sicherheit wird
     * anhand des hier zurückgegebenen Keys geprüft, ob bereits eine Authentifizierung durchgeführt wurde.
     * Falls ja, wird das {@link AuthentifzierungErgebnis} aus dem Cache verwendet und der
     * {@link AccessManager} nicht erneut aufgerufen. Der Cache-Key muss also alle für die Authentifizierung
     * relevanten Attribute umfassen (Kennung, Passwort, etc). Wird <code>null</code> zurückgeliefert, wird
     * kein Caching durchgeführt.
     * @param aufrufKontext
     *            AufrufKontext für den gerade eine Authentifzierung durchgeführt wird.
     * @return Key für das Caching des Aufrufkontext oder <code>null</code>.
     */
    Object erzeugeCacheSchluessel(K aufrufKontext);
}
