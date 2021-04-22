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
package de.bund.bva.isyfact.batchrahmen.sicherheit;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;

/**
 * This is a stub that is used by the "testGesicherterBatch*" tests in BatchRahmenTest to test
 * whether the Rechteprüfung in the batch is working properly.
 * <p>
 * It simulates an authentication request to the Keycloak server. The following behavior is implemented:
 * <ul>
 * <li>If an attempt is made to log on with the user "falscher_benutzer",
 * an AuthentifizierungFehlgeschlagenException is generated.</li>
 * <li>If it is logged on with the user "benutzer", the list of returned roles contains the role "Anwender".</li>
 * <li>All other users receive an empty role list</li>
 * </ul>
 * Only methods {@link #authentifiziere(String, String, String, String, String)},
 * {@link #authentifiziereNutzer(String, String, String, String, String)} and
 * {@link #holeSessionInhalte(String)} are supported.
 *
 */
public class AccessManagerStub implements AccessManager<AufrufKontext, AuthentifzierungErgebnisStub> {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(AccessManagerStub.class);

    /** the last authorized user. */
    private String nutzer;

    /**
     * {@inheritDoc}
     */
    public void logout(String sessionId) {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthentifzierungErgebnisStub authentifiziere(AufrufKontext unauthentifizierterAufrufKontext)
        throws AuthentifizierungTechnicalException, AuthentifizierungFehlgeschlagenException {
        String userName = unauthentifizierterAufrufKontext.getDurchfuehrenderBenutzerKennung();
        if ("falscher_benutzer".equals(userName)) {
            throw new AuthentifizierungFehlgeschlagenException("SIC2050");
        }
        this.nutzer = userName;
        LOG.info(LogKategorie.JOURNAL, "AnwendungKontext", "Erfolgreich authentifiziert");
        return new AuthentifzierungErgebnisStub();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout(AuthentifzierungErgebnisStub authentifzierungErgebnis) {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean pingAccessManager() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean pingAccessManagerByLoginLogout(AufrufKontext unauthentifizierterAufrufKontext) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void befuelleAufrufkontext(AufrufKontext aufrufKontext,
        AuthentifzierungErgebnisStub authentifzierungErgebnis) {
        aufrufKontext.setDurchfuehrendeBehoerde("900600");
        aufrufKontext.setDurchfuehrenderBenutzerKennung(this.nutzer);
        aufrufKontext.setRolle(new String[0]);
        if ("benutzer".equals(this.nutzer)) {
            aufrufKontext.setRolle(new String[] { "Anwender" });
        }
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object erzeugeCacheSchluessel(AufrufKontext aufrufKontext) {
        return null;

    }

}
