package de.bund.bva.isyfact.task.sicherheit.impl;

import de.bund.bva.isyfact.task.sicherheit.Authenticator;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;

/**
 * Implementierung von {@link Authenticator} für die Verwendung von isy-sicherheit.
 */
public class IsySicherheitAuthenticator implements Authenticator {
    private String username;
    private String password;
    private String behoerdenkennzeichen;
    private Sicherheit<AufrufKontext> sicherheit;
    private AufrufKontextFactory<AufrufKontext> aufrufKontextFactory;
    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    /**
     *  Erstellt eine Instanz eines {@link Authenticator} für isy-sicherheit.
     *
     * @param username der Benutzername
     * @param password das Passwort
     * @param behoerdenkennzeichen das Behördenkennzeichen
     * @param aufrufKontextVerwalter der {@link AufrufKontextVerwalter}
     * @param aufrufKontextFactory die {@link AufrufKontextFactory}
     * @param sicherheit die {@link Sicherheit}
     */
    public IsySicherheitAuthenticator(
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

    @Override
    public synchronized void login() {
         AufrufKontext kontext = aufrufKontextFactory.erzeugeAufrufKontext();
         aufrufKontextVerwalter.setAufrufKontext(kontext);
         kontext.setDurchfuehrenderBenutzerKennung(username);
         kontext.setDurchfuehrenderBenutzerPasswort(password);
         kontext.setDurchfuehrendeBehoerde(behoerdenkennzeichen);
         sicherheit.getBerechtigungsManagerUndAuthentifiziere(kontext);
    }

    @Override
    public synchronized void logout() {
        aufrufKontextVerwalter.setAufrufKontext(null);
    }
}
