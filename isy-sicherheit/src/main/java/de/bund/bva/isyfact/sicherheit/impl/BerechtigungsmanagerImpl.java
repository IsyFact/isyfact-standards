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
package de.bund.bva.isyfact.sicherheit.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Recht;
import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.isyfact.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;
import de.bund.bva.isyfact.sicherheit.common.exception.SicherheitFehlertextProvider;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Standard implementation of the Berechtigungsmanager interface, which manages the mapping of roles
 * to rights set via the setRollenRechteMapping method.
 *
 */
public class BerechtigungsmanagerImpl implements Berechtigungsmanager {

    /** Application-specific mapping of roles to rights. */
    private RollenRechteMapping mapping;

    /**
     * List of the user's roles.
     */
    private final Set<Rolle> rollen;

    /**
     * The rights calculated from the roles of the current user.
     */
    private Set<Recht> rechte;

    /**
     * Constructor of the class, roles must be provided.
     *
     * @param rollenIds
     *            the IDs of the roles of the current user
     * @throws AuthentifizierungTechnicalException
     *             if no roles are provided
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
     * Calculates the rights of the user from the available roles. Called once when rights are first needed.
     *
     * @throws RollenRechteMappingException
     *             If the mapping of roles to rights is missing
     */
    private void berechneRechteAusRollen() {
        rechte = new HashSet<>();
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
                // A role that does not belong to the current system was specified
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
            throw new IllegalArgumentException(
                    new SicherheitFehlertextProvider().getMessage(
                            SicherheitFehlerSchluessel.MSG_PARAMETER_FEHLT, "recht")
            );
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
    @Override
    public Recht getRecht(String recht) {
        if (recht == null || recht.isEmpty()) {
            throw new IllegalArgumentException(
                    new SicherheitFehlertextProvider().getMessage(
                            SicherheitFehlerSchluessel.MSG_PARAMETER_FEHLT, "recht"
                    )
            );
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
     * Sets the mapping of roles to rights for all Berechtigungsmanager of an application.
     *
     * @param mapping
     *            Mapping of roles to rights
     */
    public void setRollenRechteMapping(RollenRechteMapping mapping) {
        this.mapping = mapping;
    }

    public Set<Rolle> getRollen() {
        return rollen;
    }

}
