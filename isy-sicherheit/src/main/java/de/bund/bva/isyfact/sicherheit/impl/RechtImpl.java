package de.bund.bva.isyfact.sicherheit.impl;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.sicherheit.Recht;

/**
 * Implementierung des Interfaces Recht, die Variablen der Klasse werden über den Konstruktor gesetzt und
 * können danach nur noch ausgelesen werden.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class RechtImpl implements Recht {

    /**
     * Die Id (Name/Bezeichner) des Rechtes.
     */
    private String rechtId;

    /**
     * Property des Rechtes, kann null sein.
     */
    private Map<String, Object> rechtProperties = new HashMap<String, Object>();

    /**
     * Konstruktor des Rechtes in dem die Id und optional Properties gesetzt werden.
     * 
     * @param id
     *            Die Id des Rechtes
     * @param properties
     *            Die optionale Property
     * @exception IllegalArgumentException
     *                Falls als id null oder der leere String übergeben wurde
     */
    public RechtImpl(String id, Map<String, Object> properties) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("Ein Recht ohne Id ist nicht erlaubt");
        }
        rechtId = id;
        if (properties != null) {
            rechtProperties = properties;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty(String propertyName) {
        return rechtProperties.get(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return rechtId;
    }

    /**
     * Zur Berechnung des Hashcodes wird nur die Id verwendet.
     * 
     * @return Der Hashcode der Id
     */
    @Override
    public int hashCode() {
        return rechtId.hashCode();
    }

    /**
     * Beim Vergleich wird nur die Id verglichen.
     * 
     * @param obj
     *            Das Recht mit dem Verglichen wrid
     * @return true wenn die Id der Rechte übereinstimmt, ansonsten false
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
        final RechtImpl other = (RechtImpl) obj;
        if (rechtId == null) {
            if (other.rechtId != null) {
                return false;
            }
        } else if (!rechtId.equals(other.rechtId)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RechtId: " + rechtId + " RechtProperties: " + rechtProperties;
    }
}
