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
package de.bund.bva.pliscommon.batchrahmen.sicherheit;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;

/**
 * Dies ist ein Stub, der von den Tests "testGesicherterBatch*" in BatchrahmenTest verwendet wird, um zu
 * testen, ob die Rechteprüfung im Batch ordnungsgemäß funktioniert.
 * <p>
 * Er simuliert eine Authentifizierungsanfrage an den CAMS Server. Folgendes Verhalten ist implementiert:
 * <ul>
 * <li>Wird versucht sich mit dem Benutzer "falscher_benutzer" anzumelden - wird eine
 * AuthentifizierungFehlgeschlagenException erzeugt</li>
 * <li>Wird sich mit dem Benutzer "benutzer" angemeldet, enthält die Liste zurückgegebener Rollen die Rolle
 * "Anwender"</li>
 * <li>Alle anderen Benutzer erhalten eine leere Rollenliste</li>
 * </ul>
 * Es werden nur die Methoden {@link #authentifiziere(String, String, String, String, String)},
 * {@link #authentifiziereNutzer(String, String, String, String, String)} und
 * {@link #holeSessionInhalte(String)} unterstützt.
 *
 */
public class AccessManagerStub implements AccessManager<AufrufKontext, AuthentifzierungErgebnisStub> {

    /** der zuletzt autorisierte Nutzer. */
    private String nutzer;

    // /**
    // * {@inheritDoc}
    // */
    // public HashMap<String, Object> holeSessionInhalte(String sessionId)
    // throws AuthentifizierungTechnicalException {
    // HashMap<String, Object> hashMap = new HashMap<String, Object>();
    // hashMap.put(AccessManager.STRING_ANMELDENAME, nutzer);
    // hashMap.put(AccessManager.STRING_ORGANISATIONSEINHEIT, "900600");
    // ArrayList<String> list = new ArrayList<String>();
    // if ("benutzer".equals(nutzer)) {
    // list.add("Anwender");
    // }
    // hashMap.put(AccessManager.STRING_LIST_ROLLEN_IDS, list);
    // return hashMap;
    // }
    //
    // public String authentifiziere(String correlationId, String userName, String userPasswort,
    // String clientZertifikat, String clientZertifikatDN) throws AuthentifizierungFehlgeschlagenException,
    // AuthentifizierungTechnicalException {
    // if ("falscher_benutzer".equals(userName)) {
    // throw new AuthentifizierungFehlgeschlagenException("SIC2050");
    // }
    // nutzer = userName;
    // return RandomStringUtils.randomAlphabetic(16);
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // public String authentifiziereNutzer(String correlationId, String userName, String userPasswort,
    // String bhknz, String zertifikatOu) throws AuthentifizierungFehlgeschlagenException,
    // AuthentifizierungTechnicalException {
    // if ("falscher_benutzer".equals(userName)) {
    // throw new AuthentifizierungFehlgeschlagenException("SIC2050");
    // }
    // nutzer = userName;
    // return RandomStringUtils.randomAlphabetic(16);
    // }

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
