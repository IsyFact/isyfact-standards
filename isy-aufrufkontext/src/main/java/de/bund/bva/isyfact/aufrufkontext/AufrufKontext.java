package de.bund.bva.isyfact.aufrufkontext;

/**
 * Enthält informationen darüber, welcher Nutzer die gerade ausgeführet Berechnung angestoßen hat.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public interface AufrufKontext {

    /**
     * Getter für die Kennung des Benutzers.
     * @return Wert von durchfuehrenderBenutzerKennung
     */
    public String getDurchfuehrenderBenutzerKennung();

    /**
     * Setter für die Kennung des Benutzers.
     * @param durchfuehrenderBenutzerKennung
     *            Neuer Wert für die Benutzerkennung.
     */
    public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung);

    /**
     * Getter für das Passwort des Benutzers.
     * @return Wert von durchfuehrenderBenutzerPasswort
     */
    public String getDurchfuehrenderBenutzerPasswort();

    /**
     * Setter für das Passwort des Benutzers.
     * @param durchfuehrenderBenutzerPasswort
     *            Neuer Wert für durchfuehrenderBenutzerPasswort
     */
    public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort);

    /**
     * Getter für das Behördenkennzeichen des Benutzers.
     * @return Wert von durchfuehrendeBehoerde
     */
    public String getDurchfuehrendeBehoerde();

    /**
     * Setter für das Behördenkennzeichen des Benutzers.
     * @param durchfuehrendeBehoerde
     *            Neuer Wert für durchfuehrendeBehoerde
     */
    public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde);

    /**
     * Getter für die Korrelations-ID. Die Korrelations-ID wird zur eindeutigen Identifikation von
     * Service-Aufrufen verwendet. Sie wird z.B. in Log-Dateien als Logging-Kontext verwendet.
     * @return Wert von korrelationsId
     */
    public String getKorrelationsId();

    /**
     * Setter für die Korrelations-ID. Die Korrelations-ID wird zur eindeutigen Identifikation von
     * Service-Aufrufen verwendet. Sie wird z.B. in Log-Dateien als Logging-Kontext verwendet.
     * @param korrelationsId
     *            Neuer Wert für korrelationsId
     */
    public void setKorrelationsId(String korrelationsId);

    /**
     * Getter für die Rollen des Benutzers.
     * @return Wert von rolle
     */
    public String[] getRolle();

    /**
     * Setter für die Rollen des Benutzers.
     * @param rolle
     *            Neuer Wert für rolle
     */
    public void setRolle(String[] rolle);

    /**
     * Gibt an, ob die Rollen des Nutzer bereits ermittelt wurden oder nicht. Falls hier true angegeben ist
     * und die Liste der RollenIds leer ist, bedeutet dies, dass der Nutzer keinerlei Rollen hat.
     * @return Wert von rollenErmittelt
     */
    public boolean isRollenErmittelt();

    /**
     * Setter für rollenErmittelt.
     * @param rollenErmittelt
     *            Neuer Wert für rollenErmittelt
     */
    public void setRollenErmittelt(boolean rollenErmittelt);

    /**
     * Getter für den Namen des Sachbearbeiters (Endnutzer).
     * 
     * @return Der Name des Sachbearbeiters (Vorname + Nachname)
     */
    public String getDurchfuehrenderSachbearbeiterName();

    /**
     * Setter für den Namen des Sachbearbeiters (Endnutzer).
     * 
     * @param durchfuehrenderSachbearbeiterName
     *            Der Name des Sachbearbeiters (Vorname + Nachname)
     */
    public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName);

    /**
     * Getter für die interne, unveränderliche Kennung des Benutzers. Diese Kennung kann als dauerhafter
     * Schlüssel für einen Benutzer verwendet werden, z.B. zum Speichern von anwendungsspezifischen
     * Benutzereinstellungen.
     * 
     * <p>
     * <strong>Wichtig:</strong> Diese interne Kennung wird aktuell nicht über das {@code AufrufKontextTo}
     * zwischen Systemen übertragen. Sie ist daher für interne Service-Aufrufe nicht befüllt.
     * </p>
     * 
     * @return Wert von durchfuehrenderBenutzerKennung
     */
    public String getDurchfuehrenderBenutzerInterneKennung();

    /**
     * Setter für die interne, unveränderliche Kennung des Benutzers. Diese Kennung kann als dauerhafter
     * Schlüssel für einen Benutzer verwendet werden, z.B. zum Speichern von anwendungsspezifischen
     * Benutzereinstellungen.
     * 
     * <p>
     * <strong>Wichtig:</strong> Diese interne Kennung wird aktuell nicht über das {@code AufrufKontextTo}
     * zwischen Systemen übertragen. Sie ist daher für interne Service-Aufrufe nicht befüllt.
     * </p>
     * 
     * @param durchfuehrenderBenutzerInterneKennung
     *            Neuer Wert für die Benutzerkennung.
     */
    public void setDurchfuehrenderBenutzerInterneKennung(String durchfuehrenderBenutzerInterneKennung);

}
