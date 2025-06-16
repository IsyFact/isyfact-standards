package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Implementierung des Interface Rolle, die Id der Rolle wird im Konstruktor gesetzt und kann danach nur noch
 * gelesen werden.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class RolleImpl implements Rolle {

    /**
     * Die Id (Name/Bezeichner) der Rolle.
     */
    private String id;

    /**
     * Der (menschenlesbare) Name der Rolle.
     */
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Konstruktor bei dem die Id der Rolle gesetzt wird.
     *
     * @param id
     *            Die Id der Rolle. Falls id null ist, wird eine RollenRechteMappingException geworfen.
     */
    public RolleImpl(String id) {
        if (id == null) {
            throw new RollenRechteMappingException(
                SicherheitFehlerSchluessel.MSG_INITIALISIERUNGS_ELEMENT_FEHLT, "id");
        }
        this.id = id;
        this.name = "";
    }

    /**
     * Konstruktor bei dem die Id der Rolle gesetzt wird.
     *
     * @param id
     *            Die Id der Rolle. Falls id null ist, wird eine RollenRechteMappingException geworfen.
     * @param name
     *            Name der Rolle
     */
    public RolleImpl(String id, String name) {
        if (id == null) {
            throw new RollenRechteMappingException(
                SicherheitFehlerSchluessel.MSG_INITIALISIERUNGS_ELEMENT_FEHLT, "id");
        }
        this.id = id;
        this.name = name;
    }

    /**
     * This method gets the field <tt>name</tt>.
     *
     * @return the field name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * This method sets the field <tt>name</tt>.
     *
     * @param name
     *            the new value of the field name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Zur Berechnung des Hashcodes wird die Id verwendet. Falls Id null ist, wird der Name der Klasse
     * verwendet
     *
     * @return Der Hashcode der Id oder des Klassennamens, falls Id null ist.
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * Beim Vergleich wird die Id und der Name verglichen.
     *
     * @param obj
     *            Die Rolle mit der Verglichen wird
     * @return true wenn die Id und der Name beider Rollen übereinstimmt, ansonsten false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RolleImpl other = (RolleImpl) obj;

        if (this.id.equals(other.id)) {

            if ((this.name == null && other.name == null)
                || (this.name != null && this.name.equals(other.name))) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RolleId: " + this.id + ", RolleName: " + this.name;
    }
}
