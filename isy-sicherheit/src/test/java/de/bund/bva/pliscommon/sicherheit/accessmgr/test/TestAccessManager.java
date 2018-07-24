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
package de.bund.bva.pliscommon.sicherheit.accessmgr.test;

import java.util.Objects;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.pliscommon.sicherheit.common.exception.SicherheitTechnicalRuntimeException;

/**
 *
 */
public class TestAccessManager implements AccessManager<AufrufKontext, TestAuthentifizierungErgebnis> {

    private AufrufKontext paramAuthentifiziereAufrufKontext;

    private int countAuthentifziere;

    private TestAuthentifizierungErgebnis paramLogoutAuthentifzierungErgebnis;

    private TestAuthentifizierungErgebnis resultAuthentifiziere;

    private SicherheitTechnicalRuntimeException authentifiziereException;

    @Override
    public TestAuthentifizierungErgebnis authentifiziere(AufrufKontext unauthentifizierterAufrufKontext)
        throws AuthentifizierungTechnicalException, AuthentifizierungFehlgeschlagenException {
        this.paramAuthentifiziereAufrufKontext = unauthentifizierterAufrufKontext;
        this.countAuthentifziere++;
        if (this.authentifiziereException != null) {
            throw this.authentifiziereException;
        }
        return this.resultAuthentifiziere;
    }

    @Override
    public void logout(TestAuthentifizierungErgebnis authentifzierungErgebnis) {
        this.paramLogoutAuthentifzierungErgebnis = authentifzierungErgebnis;
    }

    @Override
    public boolean pingAccessManager() {
        return true;
    }

    @Override
    public boolean pingAccessManagerByLoginLogout(AufrufKontext unauthentifizierterAufrufKontext) {
        return true;
    }

    @Override
    public void befuelleAufrufkontext(AufrufKontext aufrufKontext,
        TestAuthentifizierungErgebnis authentifzierungErgebnis) {
        aufrufKontext.setRolle(authentifzierungErgebnis.getRollen());
        aufrufKontext.setRollenErmittelt(true);

    }

    @Override
    public Object erzeugeCacheSchluessel(AufrufKontext aufrufKontext) {
        // Dies ist nur eine beispielhafte Dummy-Implementierung. Die Nutzung von hashcode() ist nicht
        // eindeutig genug für einen produktiven Einsatz!
        return Objects.hash(
            aufrufKontext.getDurchfuehrenderBenutzerKennung(),
            aufrufKontext.getDurchfuehrenderBenutzerPasswort(),
            aufrufKontext.getDurchfuehrendeBehoerde());
    }

    public void reset() {
        this.paramAuthentifiziereAufrufKontext = null;
        this.paramLogoutAuthentifzierungErgebnis = null;
        this.resultAuthentifiziere = null;
        this.authentifiziereException = null;
        this.countAuthentifziere = 0;
    }

    public AufrufKontext getParamAuthentifiziereAufrufKontext() {
        return this.paramAuthentifiziereAufrufKontext;
    }

    public TestAuthentifizierungErgebnis getParamLogoutAuthentifzierungErgebnis() {
        return this.paramLogoutAuthentifzierungErgebnis;
    }

    public void setResultAuthentifiziere(String... rollen) {
        this.resultAuthentifiziere = new TestAuthentifizierungErgebnis();
        this.resultAuthentifiziere.setRollen(rollen);
    }

    public int getCountAuthentifziere() {
        return this.countAuthentifziere;
    }

    public void setAuthentifiziereException(SicherheitTechnicalRuntimeException authentifiziereException) {
        this.authentifiziereException = authentifiziereException;
    }
}
