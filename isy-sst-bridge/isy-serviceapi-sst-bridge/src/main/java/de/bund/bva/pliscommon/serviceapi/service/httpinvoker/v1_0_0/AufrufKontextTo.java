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
package de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0;

import java.io.Serializable;

/**
 * Transportobjekt fuer die bei einem Service-Aufruf zu uebertragenden Daten.
 * 
 */
public class AufrufKontextTo implements Serializable {

    /** Version-ID. */
    private static final long serialVersionUID = 4041583777226221568L;

    /** Name des Nutzers fuer die Berechtigungskontrolle. */
    private String durchfuehrenderBenutzerKennung;

    /** Passwort des Nutzers fuer die Berechtigungskontrolle. */
    private String durchfuehrenderBenutzerPasswort;

    /** Die Organisationseinheit zu welcher der Nutzer gehört. */
    private String durchfuehrendeBehoerde;

    /** Korrelations-Id. */
    private String korrelationsId;

    /** Rollen des Nutzers. */
    private String[] rolle;

    /**
     * Der Name (Vorname + Nachname) eines Anwenders oder der Name des Nutzers eines Fremdprogrammes.
     */
    private String durchfuehrenderSachbearbeiterName;

    /** */
    private boolean rollenErmittelt;

    /**
     * Getter für die Kennung des Benutzers.
     * @return Wert von durchfuehrenderBenutzerKennung
     */
    public String getDurchfuehrenderBenutzerKennung() {
        return durchfuehrenderBenutzerKennung;
    }

    /**
     * Setter für die Kennung des Benutzers.
     * @param durchfuehrenderBenutzerKennung
     *            Neuer Wert für die Benutzerkennung.
     */
    public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung) {
        this.durchfuehrenderBenutzerKennung = durchfuehrenderBenutzerKennung;
    }

    /**
     * Getter für das Passwort des Benutzers.
     * @return Wert von durchfuehrenderBenutzerPasswort
     */
    public String getDurchfuehrenderBenutzerPasswort() {
        return durchfuehrenderBenutzerPasswort;
    }

    /**
     * Setter für das Passwort des Benutzers.
     * @param durchfuehrenderBenutzerPasswort
     *            Neuer Wert für durchfuehrenderBenutzerPasswort
     */
    public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort) {
        this.durchfuehrenderBenutzerPasswort = durchfuehrenderBenutzerPasswort;
    }

    /**
     * Getter für das Behördenkennzeichen des Benutzers.
     * @return Wert von durchfuehrendeBehoerde
     */
    public String getDurchfuehrendeBehoerde() {
        return durchfuehrendeBehoerde;
    }

    /**
     * Setter für das Behördenkennzeichen des Benutzers.
     * @param durchfuehrendeBehoerde
     *            Neuer Wert für durchfuehrendeBehoerde
     */
    public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde) {
        this.durchfuehrendeBehoerde = durchfuehrendeBehoerde;
    }

    /**
     * Getter für die Korrelations-ID. Die Korrelations-ID wird zur eindeutigen Identifikation von
     * Service-Aufrufen verwendet. Sie wird z.B. in Log-Dateien als Logging-Kontext verwendet.
     * @return Wert von korrelationsId
     */
    public String getKorrelationsId() {
        return korrelationsId;
    }

    /**
     * Setter für die Korrelations-ID. Die Korrelations-ID wird zur eindeutigen Identifikation von
     * Service-Aufrufen verwendet. Sie wird z.B. in Log-Dateien als Logging-Kontext verwendet.
     * @param korrelationsId
     *            Neuer Wert für korrelationsId
     */
    public void setKorrelationsId(String korrelationsId) {
        this.korrelationsId = korrelationsId;
    }

    /**
     * Getter für die Rollen des Benutzers.
     * @return Wert von rolle
     */
    public String[] getRolle() {
        return rolle;
    }

    /**
     * Setter für die Rollen des Benutzers.
     * @param rolle
     *            Neuer Wert für rolle
     */
    public void setRolle(String[] rolle) {
        this.rolle = rolle;
    }

    /**
     * Gibt an, ob die Rollen des Nutzer bereits ermittelt wurden oder nicht. Falls hier true angegeben ist
     * und die Liste der RollenIds leer ist, bedeutet dies, dass der Nutzer keinerlei Rollen hat.
     * @return Wert von rollenErmittelt
     */
    public boolean isRollenErmittelt() {
        return rollenErmittelt;
    }

    /**
     * Setter für rollenErmittelt.
     * @param rollenErmittelt
     *            Neuer Wert für rollenErmittelt
     */
    public void setRollenErmittelt(boolean rollenErmittelt) {
        this.rollenErmittelt = rollenErmittelt;
    }

    /**
     * Getter für den Namen des Sachbearbeiters (Endnutzer).
     * 
     * @return Der Name des Sachbearbeiters (Vorname + Nachname)
     */
    public String getDurchfuehrenderSachbearbeiterName() {
        return durchfuehrenderSachbearbeiterName;
    }

    /**
     * Setter für den Namen des Sachbearbeiters (Endnutzer).
     * 
     * @param durchfuehrenderSachbearbeiterName
     *            Der Name des Sachbearbeiters (Vorname + Nachname)
     */
    public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName) {
        this.durchfuehrenderSachbearbeiterName = durchfuehrenderSachbearbeiterName;
    }
}
