package de.bund.bva.isyfact.task.security.impl;

import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;

/**
 *
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class SecurityAuthenticatorImpl implements SecurityAuthenticator {
    private volatile ThreadLocal<String> username
            = new ThreadLocal<>();
    private volatile ThreadLocal<String> password
            = new ThreadLocal<>();
    private volatile ThreadLocal<String> behoerdenkennzeichen
            = new ThreadLocal<>();
    private volatile ThreadLocal<Sicherheit<AufrufKontext>> sicherheit
            = new ThreadLocal<>();
    private volatile ThreadLocal<AufrufKontextFactory<AufrufKontext>> aufrufKontextFactory
            = new ThreadLocal<>();
    private volatile ThreadLocal<AufrufKontextVerwalter<AufrufKontext>> aufrufKontextVerwalter
            = new ThreadLocal<>();


    /**
     *
     */
    public SecurityAuthenticatorImpl() { }

    /**
     *
     * @param username
     * @param password
     * @param behoerdenkennzeichen
     * @param aufrufKontextVerwalter
     * @param aufrufKontextFactory
     * @param sicherheit
     */
    public SecurityAuthenticatorImpl(
            String username,
            String password,
            String behoerdenkennzeichen,
            AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter,
            AufrufKontextFactory<AufrufKontext> aufrufKontextFactory,
            Sicherheit<AufrufKontext> sicherheit) {
        this.username.set(username);
        this.password.set(password);
        this.behoerdenkennzeichen.set(behoerdenkennzeichen);
        this.sicherheit.set(sicherheit);
        this.aufrufKontextFactory.set(aufrufKontextFactory);
        this.aufrufKontextVerwalter.set(aufrufKontextVerwalter);
    }

    /**
     *
     */
    @Override
    public synchronized void login() {
        //TODO Die Properties müssen zunächst über Spring injiziert werden
         AufrufKontext kontext = aufrufKontextFactory.get().erzeugeAufrufKontext();
         aufrufKontextVerwalter.get().setAufrufKontext(kontext);
         kontext.setDurchfuehrenderBenutzerKennung(username.get());
         kontext.setDurchfuehrenderBenutzerPasswort(password.get());
         kontext.setDurchfuehrendeBehoerde(behoerdenkennzeichen.get());
         sicherheit.get().getBerechtigungsManagerUndAuthentifiziere(kontext);
    }

    /**
     *
     */
    @Override
    public synchronized void logout() {
        aufrufKontextVerwalter.get().setAufrufKontext(null);
    }

    @Override
    public synchronized String getUsername() {
        return username.get();
    }

    @Override
    public synchronized void setUsername(String username) {
        this.username.set(username);
    }

    @Override
    public synchronized String getPassword() {
        return password.get();
    }

    @Override
    public synchronized void setPassword(String password) {
        this.password.set(password);
    }

    @Override
    public synchronized String getBehoerdenkennzeichen() {
        return behoerdenkennzeichen.get();
    }

    @Override
    public synchronized void setBehoerdenkennzeichen(String behoerdenkennzeichen) {
        this.behoerdenkennzeichen.set(behoerdenkennzeichen);
    }

    @Override
    public synchronized Sicherheit<AufrufKontext> getSicherheit() {
        return sicherheit.get();
    }

    @Override
    public synchronized void setSicherheit(Sicherheit<AufrufKontext> sicherheit) {
        this.sicherheit.set(sicherheit);
    }

    @Override
    public synchronized AufrufKontextFactory<AufrufKontext> getAufrufKontextFactory() {
        return aufrufKontextFactory.get();
    }

    @Override
    public synchronized void setAufrufKontextFactory(AufrufKontextFactory<AufrufKontext> aufrufKontextFactory) {
        this.aufrufKontextFactory.set(aufrufKontextFactory);
    }

    @Override
    public synchronized AufrufKontextVerwalter<AufrufKontext> getAufrufKontextVerwalter() {
        return aufrufKontextVerwalter.get();
    }

    @Override
    public synchronized void setAufrufKontextVerwalter(AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter.set(aufrufKontextVerwalter);
    }

}
