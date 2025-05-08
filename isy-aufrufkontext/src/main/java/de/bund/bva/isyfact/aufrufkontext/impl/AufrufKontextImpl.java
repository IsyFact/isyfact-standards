package de.bund.bva.isyfact.aufrufkontext.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;

/**
 * Enthält informationen darüber, welcher Nutzer die gerade ausgeführet Berechnung angestoßen hat.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
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
