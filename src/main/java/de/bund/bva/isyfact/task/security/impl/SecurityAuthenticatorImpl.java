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
    private volatile ThreadLocal<String> usernameThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<String> passwordThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<String> behoerdenkennzeichenThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<Sicherheit<AufrufKontext>> sicherheitThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<AufrufKontextFactory<AufrufKontext>> aufrufKontextFactoryThreadLocal
            = new ThreadLocal<>();
    private volatile ThreadLocal<AufrufKontextVerwalter<AufrufKontext>> aufrufKontextVerwalterThreadLocal
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
        this.usernameThreadLocal.set(username);
        this.passwordThreadLocal.set(password);
        this.behoerdenkennzeichenThreadLocal.set(behoerdenkennzeichen);
        this.sicherheitThreadLocal.set(sicherheit);
        this.aufrufKontextFactoryThreadLocal.set(aufrufKontextFactory);
        this.aufrufKontextVerwalterThreadLocal.set(aufrufKontextVerwalter);
    }

    /**
     *
     */
    @Override
    public synchronized void login() {
        //TODO Die Properties müssen zunächst über Spring injiziert werden
         AufrufKontext kontext = aufrufKontextFactoryThreadLocal.get().erzeugeAufrufKontext();
         aufrufKontextVerwalterThreadLocal.get().setAufrufKontext(kontext);
         kontext.setDurchfuehrenderBenutzerKennung(usernameThreadLocal.get());
         kontext.setDurchfuehrenderBenutzerPasswort(passwordThreadLocal.get());
         kontext.setDurchfuehrendeBehoerde(behoerdenkennzeichenThreadLocal.get());
         sicherheitThreadLocal.get().getBerechtigungsManagerUndAuthentifiziere(kontext);
    }

    /**
     *
     */
    @Override
    public synchronized void logout() {
        aufrufKontextVerwalterThreadLocal.get().setAufrufKontext(null);
    }

    @Override
    public synchronized String getUsername() {
        return usernameThreadLocal.get();
    }

    @Override
    public synchronized void setUsername(String username) {
        this.usernameThreadLocal.set(username);
    }

    @Override
    public synchronized String getPassword() {
        return passwordThreadLocal.get();
    }

    @Override
    public synchronized void setPassword(String password) {
        this.passwordThreadLocal.set(password);
    }

    @Override
    public synchronized String getBehoerdenkennzeichen() {
        return behoerdenkennzeichenThreadLocal.get();
    }

    @Override
    public synchronized void setBehoerdenkennzeichen(String behoerdenkennzeichen) {
        this.behoerdenkennzeichenThreadLocal.set(behoerdenkennzeichen);
    }

    @Override
    public synchronized Sicherheit<AufrufKontext> getSicherheit() {
        return sicherheitThreadLocal.get();
    }

    @Override
    public synchronized void setSicherheit(Sicherheit<AufrufKontext> sicherheit) {
        this.sicherheitThreadLocal.set(sicherheit);
    }

    @Override
    public synchronized AufrufKontextFactory<AufrufKontext> getAufrufKontextFactory() {
        return aufrufKontextFactoryThreadLocal.get();
    }

    @Override
    public synchronized void setAufrufKontextFactory(AufrufKontextFactory<AufrufKontext> aufrufKontextFactory) {
        this.aufrufKontextFactoryThreadLocal.set(aufrufKontextFactory);
    }

    @Override
    public synchronized AufrufKontextVerwalter<AufrufKontext> getAufrufKontextVerwalter() {
        return aufrufKontextVerwalterThreadLocal.get();
    }

    @Override
    public synchronized void setAufrufKontextVerwalter(AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalterThreadLocal.set(aufrufKontextVerwalter);
    }

}
