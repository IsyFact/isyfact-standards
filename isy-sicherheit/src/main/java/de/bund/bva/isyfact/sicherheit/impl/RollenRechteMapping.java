package de.bund.bva.isyfact.sicherheit.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.Recht;

/**
 * Diese Klasse dient zum Speichern des Mappings von Rollen zu Rechten für eine Anwendung.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class RollenRechteMapping {

    /**
     * Die Id der Anwendung zu der das Mapping gehört.
     */
    private String anwendungsId;

    /**
     * Enthält das anwendungesspezifische Mapping von Rollen zu Rechten.
     */
    private Map<Rolle, List<Recht>> rollenRechteMapping = new HashMap<>();

    /**
     * alle in der Anwendung definierten Rechte.
     */
    private Set<Recht> alleDefiniertenRechte = new HashSet<>();

    /**
     * Getter für das Rollen zu Rechte Mapping.
     * 
     * @return Das Mapping von Rollen zu Rechten
     */
    public Map<Rolle, List<Recht>> getRollenRechteMapping() {
        return rollenRechteMapping;
    }

    /**
     * Setter für das Rollen zu Rechte Mapping.
     * 
     * @param rollenRechteMapping
     *            Das Mapping
     */
    public void setRollenRechteMapping(Map<Rolle, List<Recht>> rollenRechteMapping) {
        this.rollenRechteMapping = rollenRechteMapping;
    }

    /**
     * Getter für die Id der Anwendung zu der das Mapping gehört.
     * 
     * @return Die Id der Anwendung
     */
    public String getAnwendungsId() {
        return anwendungsId;
    }

    /**
     * Setter für die Id der Anwendung zu der das Mapping gehört.
     * 
     * @param anwendungsId
     *            Die Id der Anwendung
     */
    public void setAnwendungsId(String anwendungsId) {
        this.anwendungsId = anwendungsId;
    }

    /**
     * Ausgabe der Instanz als String.
     * 
     * @return Ausgabe des Mappings als String
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("AnwendungsId:").append(anwendungsId).append("\n");
        result.append("RollenRechteMapping:").append(rollenRechteMapping.toString());
        return result.toString();
    }

    /**
     * @return Eine Sammlung aller in der Anwendung (rollenrechte.xml) definierten Rechte.
     */
    public Set<Recht> getAlleDefiniertenRechte() {
        return Collections.unmodifiableSet(alleDefiniertenRechte);
    }

    /**
     * Setzt das Feld {@link #alleDefiniertenRechte}.
     * @param collection Neuer Wert für alleDefiniertenRechte
     */
    public void setAlleDefiniertenRechte(Collection<Recht> collection) {
        this.alleDefiniertenRechte = new HashSet<>(collection);
    }
}
