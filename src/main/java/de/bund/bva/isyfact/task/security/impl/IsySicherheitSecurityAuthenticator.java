package de.bund.bva.isyfact.task.security.impl;

import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;

/**
 * Die Klasse IsySicherheitSecurityAuthenticator ist eine Thread-sichere Klasse für die Verwendung von IsyFact-Sicherheit.
 */
public class IsySicherheitSecurityAuthenticator implements SecurityAuthenticator {
    private String username;
    private String password;
    private String behoerdenkennzeichen;
    private Sicherheit<AufrufKontext> sicherheit;
    private AufrufKontextFactory<AufrufKontext> aufrufKontextFactory;
    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    /**
     *
     * @param username
     * @param password
     * @param behoerdenkennzeichen
     * @param aufrufKontextVerwalter
     * @param aufrufKontextFactory
     * @param sicherheit
     */
    public IsySicherheitSecurityAuthenticator(
            String username,
            String password,
            String behoerdenkennzeichen,
            AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter,
            AufrufKontextFactory<AufrufKontext> aufrufKontextFactory,
            Sicherheit<AufrufKontext> sicherheit) {
        this.username = username;
        this.password = password;
        this.behoerdenkennzeichen = behoerdenkennzeichen;
        this.sicherheit = sicherheit;
        this.aufrufKontextFactory = aufrufKontextFactory;
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    /**
     *
     */
    @Override
    public synchronized void login() {
         AufrufKontext kontext = aufrufKontextFactory.erzeugeAufrufKontext();
         aufrufKontextVerwalter.setAufrufKontext(kontext);
         kontext.setDurchfuehrenderBenutzerKennung(username);
         kontext.setDurchfuehrenderBenutzerPasswort(password);
         kontext.setDurchfuehrendeBehoerde(behoerdenkennzeichen);
         sicherheit.getBerechtigungsManagerUndAuthentifiziere(kontext);
    }

    /**
     *
     */
    @Override
    public synchronized void logout() {
        aufrufKontextVerwalter.setAufrufKontext(null);
    }
}
