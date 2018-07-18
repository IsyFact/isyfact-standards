/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.pliscommon.sicherheit.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bund.bva.pliscommon.sicherheit.Berechtigungsmanager;
import de.bund.bva.pliscommon.sicherheit.Recht;
import de.bund.bva.pliscommon.sicherheit.Rolle;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.RollenRechteMappingException;
import de.bund.bva.pliscommon.sicherheit.common.exception.SicherheitFehlertextProvider;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Standard-Implementierung des Interface BerechtigungsManager, die Informationen über das Mapping von Rollen
 * zu Rechten wird über die Methode setRollenRechteMapping gesetzt.
 * 
 */
public class BerechtigungsmanagerImpl implements Berechtigungsmanager {

    /** Anwendungsspezifische Abbildung von Rollen auf Rechte. */
    private RollenRechteMapping mapping;

    /**
     * Liste der Rollen des Benutzers.
     */
    private final Set<Rolle> rollen;

    /**
     * Die Rechte die aus den Rollen des aktuellen Benutzers berechnet wurden.
     */
    private Set<Recht> rechte;

    /**
     * Konstruktor der Klasse, Rollen müssen übergeben werden.
     * 
     * @param rollenIds
     *            die IDs der Rollen des aktuellen Nutzers
     * @throws AuthentifizierungTechnicalException
     *             wenn keine Rollen übergeben werden
     */
    public BerechtigungsmanagerImpl(String[] rollenIds) throws AuthentifizierungTechnicalException {
        rollen = new HashSet<Rolle>();
        if (rollenIds == null) {
            return; 
        }
        for (String rollenId : rollenIds) {
            rollen.add(new RolleImpl(rollenId));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<Recht> getRechte() {
        if (rechte == null) {
            berechneRechteAusRollen();
        }
        return Collections.unmodifiableSet(rechte);
    }

    /**
     * Berechnet aus den vorhandenen Rollen die Rechte des Benutzers. Wird einmalig aufgerufen, wenn zum
     * ersten mal Rechte benötigt werden.
     * 
     * @throws RollenRechteMappingException
     *             Falls das Mapping der Rollen zu Rechten fehlt
     */
    private void berechneRechteAusRollen() {
        rechte = new HashSet<Recht>();
        if (rollen == null) {
            return;
        }
        if (mapping == null) {
            throw new RollenRechteMappingException(
                SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLT);
        }

        for (Rolle rolle : rollen) {
            List<Recht> rollenRechte = mapping.getRollenRechteMapping().get(rolle);

            if (rollenRechte == null) {
                // Es wurde eine Rolle angegeben, die nicht zum aktuellen System gehört
                continue;
            }
            rechte.addAll(rollenRechte);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hatRecht(String recht) {
        if (recht == null || recht.isEmpty()) {
            throw new IllegalArgumentException(new SicherheitFehlertextProvider().getMessage(
                SicherheitFehlerSchluessel.MSG_PARAMETER_FEHLT, "recht"));
        }
       
        RechtImpl rechtImpl = new RechtImpl(recht, null);
        if (getRechte().contains(rechtImpl)) {
            return true;
        } else {
            if (!mapping.getAlleDefiniertenRechte().contains(rechtImpl)) {
                throw new RollenRechteMappingException(
                    SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_RECHT_UNDEFINIERT, recht);
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pruefeRecht(String recht) throws AutorisierungFehlgeschlagenException {
        if (!hatRecht(recht)) {
            throw new AutorisierungFehlgeschlagenException(
                SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_FEHLGESCHLAGEN, recht);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Recht getRecht(String recht) {
        if (recht == null || recht.isEmpty()) {
            throw new IllegalArgumentException(new SicherheitFehlertextProvider().getMessage(
                SicherheitFehlerSchluessel.MSG_PARAMETER_FEHLT, "recht"));
        }
        if (rechte == null) {
            berechneRechteAusRollen();
        }
        for (Recht aktuellesRecht : rechte) {
            if (aktuellesRecht.getId().equals(recht)) {
                return aktuellesRecht;
            }
        }
        return null;
    }

    /**
     * Setzt das Mapping von Rollen zu Rechten für alle Berechtigungsmanager einer Anwendung.
     * 
     * @param mapping
     *            Mapping von Rollen zu Rechten
     */
    public void setRollenRechteMapping(RollenRechteMapping mapping) {
        this.mapping = mapping;
    }

    public Set<Rolle> getRollen() {
        return rollen;
}

}
