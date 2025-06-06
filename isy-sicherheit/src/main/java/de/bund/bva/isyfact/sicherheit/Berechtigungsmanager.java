package de.bund.bva.isyfact.sicherheit;

import java.util.Set;

import de.bund.bva.isyfact.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;

/**
 * Der Berechtigungsmanager steuert die Berechtigungen innerhalb jeder Anwendung. Jede Anwendung kann Ihren
 * eigenen Berechtigungsmanager besitzen.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface Berechtigungsmanager {

    /**
     * Gibt alle Rechte zurück, die dem aktuellen Benutzer Aufgrund seiner Rollen zustehen, ggf. eine leere
     * Liste.
     * 
     * @return Alle Rechte des Benutzers
     * @throws RollenRechteMappingException
     *             Falls die Daten zur Abbildung von Rollen zu Rechten fehlen
     */
    Set<Recht> getRechte();

    /**
     * Ermittelt ob der aktuelle Benutzer ein Recht hat.
     * 
     * @param recht
     *            Die Id bzw. der Name des Rechtes dessen vorhandensein überprüft werden soll
     * @return true falls der Benutzer das Recht hat, sonst false
     * 
     * @throws RollenRechteMappingException
     *             Falls die Daten zur Abbildung von Rollen zu Rechten fehlen
     * @throws IllegalArgumentException
     *             Falls der Parameter recht null oder der leere String ist
     */
    boolean hatRecht(String recht);


    /**
     * Überprüft ob der aktuelle Benutzer ein Recht hat.
     * 
     * @param recht
     *            Die Id bzw. der Name des Rechtes dessen vorhandensein überprüft werden soll
     * 
     * @throws RollenRechteMappingException
     *             Falls die Daten zur Abbildung von Rollen zu Rechten fehlen
     * @throws IllegalArgumentException
     *             Falls der Parameter recht null oder der leere String ist
     * @throws AutorisierungFehlgeschlagenException
     *             Falls der aktuelle Benutzer das Recht nicht hat
     */
    void pruefeRecht(String recht) throws AutorisierungFehlgeschlagenException;

    /**
     * Gibt das Recht mit dem angegeben Namen/Id zurück.
     * 
     * @param recht
     *            Die Id bzw. der Name eines Rechtes
     * @return Das Recht mit dem angegebenen Namen oder null falls der aktuelle Benutzer kein solches Recht
     *         besitzt
     * @throws RollenRechteMappingException
     *             Falls die Daten zur Abbildung von Rollen zu Rechten fehlen
     * 
     * @throws IllegalArgumentException
     *             Falls der Parameter recht null oder der leere String ist
     */
    Recht getRecht(String recht);

	// TODO: Javadoc
    Set<Rolle> getRollen();
}
