package de.bund.bva.isyfact.sicherheit;

import java.util.Set;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;

/**
 * Interface zum Zugriff auf Berechtigungsmanager.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface Sicherheit<K extends AufrufKontext> {

    /**
     * Erzeugt und Initialisiert einen Berechtigungsmanager. Es werden die im Rahmen der Authentifizierung am
     * Access-Manager bereits ermittelten Rollen über den vorliegenden Aufrufkontext ermittelt.
     *
     * @return Eine korrekt initialisierte Instanz des Berechtigungsmanager
     *
     * @throws AuthentifizierungFehlgeschlagenException
     *             Wird geworfen, wenn der Benutzer durch ungültige Parameter nicht am
     *             Authentifizierungs-Server authentifiziert werden kann.
     * @throws AuthentifizierungTechnicalException
     *             Wird geworfen wenn kein AufrufKontext gefunden werden kann, oder falls der Zugriff auf den
     *             Authentifizierungs-Server nicht erfolgreich ist.
     */
    Berechtigungsmanager getBerechtigungsManager() throws AuthentifizierungTechnicalException;

    /**
     * Leert den Cache von isy-sicherheit. Authentifizierungsdaten werden nach Aufruf der Methode geloescht.
     * Es erfolgt fuer folgende Benutzeraufrufe zunaechst jeweils wieder eine Authentifizierung ueber den
     * {@link AccessManager}.
     */
    void leereCache();

    /**
     * Erzeugt und initialisiert einen Berechtigungsmanager. Dazu erfolgt zunächst eine Authentifzierung des
     * Benutzers am Access-Manager mithilfe der Access-Manager-spezifischen Authentifizierungsdaten
     *
     * @param unauthentifizierterAufrufKontext
     *            Das unauthentifizierte AufrufKontext-Objekt mit den Authentifizierungsdaten
     * @return Den Berechtigungsmanager, falls die Authentifizierung erfolgreich ist.
     * @throws AuthentifizierungFehlgeschlagenException
     *             Falls die Authentifizierung fachlich nicht erfolgreich ist.
     * @throws AuthentifizierungTechnicalException
     *             Falls es technische Problemen bei der Authentifizierung gibt.
     */
    Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(K unauthentifizierterAufrufKontext)
        throws AuthentifizierungTechnicalException;

    /**
     * Liefert alle im System hinterlegten Rollen.
     * @return Alle im RollenRechte-Mapping hinterlegten Rollen.
     */
    Set<Rolle> getAlleRollen();
}
