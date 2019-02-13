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
package de.bund.bva.pliscommon.sicherheit.annotation;

import java.util.HashSet;
import java.util.Set;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.sicherheit.Berechtigungsmanager;
import de.bund.bva.pliscommon.sicherheit.Rolle;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;

public class SicherheitStub implements Sicherheit<AufrufKontext> {

    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    private AufrufKontext letzterAufrufKontext;

    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    public AufrufKontext getLetzterAufrufKontext() {
        return this.letzterAufrufKontext;
    }

    public void reset() {
        this.letzterAufrufKontext = null;
    }

    @Override
    public Berechtigungsmanager getBerechtigungsManager() throws AuthentifizierungFehlgeschlagenException,
        AuthentifizierungTechnicalException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(
        AufrufKontext unauthentifizierterAufrufKontext) throws AuthentifizierungFehlgeschlagenException,
        AuthentifizierungTechnicalException {

        this.aufrufKontextVerwalter.setAufrufKontext(unauthentifizierterAufrufKontext);
        this.letzterAufrufKontext = unauthentifizierterAufrufKontext;
        return null;
    }

    @Override
    public Set<Rolle> getAlleRollen() {
        return new HashSet<Rolle>();
    }

    @Override
    public void leereCache() {
        // TODO
        // DIESE METHODE WIRD BISHER NICHT VERWENDET
        throw new UnsupportedOperationException();
    }

}
