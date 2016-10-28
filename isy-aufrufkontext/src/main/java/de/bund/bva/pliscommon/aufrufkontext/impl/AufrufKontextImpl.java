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
package de.bund.bva.pliscommon.aufrufkontext.impl;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;

/**
 * Enthält informationen darüber, welcher Nutzer die gerade ausgeführet Berechnung angestoßen hat.
 * 
 */
public class AufrufKontextImpl implements AufrufKontext {

    /** Name des Nutzers fuer die Berechtigungskontrolle. */
    private String durchfuehrenderBenutzerKennung;

    /** Passwort des Nutzers fuer die Berechtigungskontrolle. */
    private String durchfuehrenderBenutzerPasswort;

    /** Die Organisationseinheit zu welcher der Nutzer gehört. */
    private String durchfuehrendeBehoerde;

    /** Korrelations-Id. */
    private String korrelationsId;

    /** Rollen des Nutzers. */
    private String[] rolle = new String[] {};

    /** Interne, unveränderliche Kennung des Benutzers. */
    private String durchfuehrenderBenutzerInterneKennung;

    /**
     * Der Name (Vorname + Nachname) eines Anwenders oder der Name des Nutzers eines Fremdprogrammes.
     */
    private String durchfuehrenderSachbearbeiterName;

    /** */
    private boolean rollenErmittelt;

    /**
     * {@inheritDoc}
     */
    public String getDurchfuehrenderBenutzerKennung() {
        return durchfuehrenderBenutzerKennung;
    }

    /**
     * {@inheritDoc}
     */
    public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung) {
        this.durchfuehrenderBenutzerKennung = durchfuehrenderBenutzerKennung;
    }

    /**
     * {@inheritDoc}
     */
    public String getDurchfuehrenderBenutzerPasswort() {
        return durchfuehrenderBenutzerPasswort;
    }

    /**
     * {@inheritDoc}
     */
    public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort) {
        this.durchfuehrenderBenutzerPasswort = durchfuehrenderBenutzerPasswort;
    }

    /**
     * {@inheritDoc}
     */
    public String getDurchfuehrendeBehoerde() {
        return durchfuehrendeBehoerde;
    }

    /**
     * {@inheritDoc}
     */
    public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde) {
        this.durchfuehrendeBehoerde = durchfuehrendeBehoerde;
    }

    /**
     * {@inheritDoc}
     */
    public String getKorrelationsId() {
        return korrelationsId;
    }

    /**
     * {@inheritDoc}
     */
    public void setKorrelationsId(String korrelationsId) {
        this.korrelationsId = korrelationsId;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getRolle() {
        return rolle.clone();
    }

    /**
     * {@inheritDoc}
     */
    public void setRolle(String[] rolle) {
        // Speichere eine Kopie der Rollen - nullsafe
        if (rolle != null) {
            this.rolle = rolle.clone();
        } else {
            this.rolle = new String[] {};
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRollenErmittelt() {
        return rollenErmittelt;
    }

    /**
     * {@inheritDoc}
     */
    public void setRollenErmittelt(boolean rollenErmittelt) {
        this.rollenErmittelt = rollenErmittelt;
    }

    /**
     * {@inheritDoc}
     */
    public String getDurchfuehrenderSachbearbeiterName() {
        return durchfuehrenderSachbearbeiterName;
    }

    /**
     * {@inheritDoc}
     */
    public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName) {
        this.durchfuehrenderSachbearbeiterName = durchfuehrenderSachbearbeiterName;
    }

    /**
     * {@inheritDoc}
     */
    public String getDurchfuehrenderBenutzerInterneKennung() {
        return durchfuehrenderBenutzerInterneKennung;
    }

    /**
     * {@inheritDoc}
     */
    public void setDurchfuehrenderBenutzerInterneKennung(String durchfuehrenderBenutzerInterneKennung) {
        this.durchfuehrenderBenutzerInterneKennung = durchfuehrenderBenutzerInterneKennung;
    }
}
