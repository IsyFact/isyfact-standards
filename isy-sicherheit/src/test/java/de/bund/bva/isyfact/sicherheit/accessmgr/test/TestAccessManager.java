package de.bund.bva.isyfact.sicherheit.accessmgr.test;

import java.util.Objects;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.isyfact.sicherheit.common.exception.SicherheitTechnicalRuntimeException;

public class TestAccessManager implements AccessManager<AufrufKontext, TestAuthentifizierungErgebnis> {

    private AufrufKontext paramAuthentifiziereAufrufKontext;

    private int countAuthentifziere;

    private TestAuthentifizierungErgebnis paramLogoutAuthentifzierungErgebnis;

    private TestAuthentifizierungErgebnis resultAuthentifiziere;

    private SicherheitTechnicalRuntimeException authentifiziereException;

    @Override
    public TestAuthentifizierungErgebnis authentifiziere(AufrufKontext unauthentifizierterAufrufKontext)
        throws AuthentifizierungTechnicalException, AuthentifizierungFehlgeschlagenException {
        this.paramAuthentifiziereAufrufKontext = unauthentifizierterAufrufKontext;
        this.countAuthentifziere++;
        if (this.authentifiziereException != null) {
            throw this.authentifiziereException;
        }
        return this.resultAuthentifiziere;
    }

    @Override
    public void logout(TestAuthentifizierungErgebnis authentifzierungErgebnis) {
        this.paramLogoutAuthentifzierungErgebnis = authentifzierungErgebnis;
    }

    @Override
    public boolean pingAccessManager() {
        return true;
    }

    @Override
    public boolean pingAccessManagerByLoginLogout(AufrufKontext unauthentifizierterAufrufKontext) {
        return true;
    }

    @Override
    public void befuelleAufrufkontext(AufrufKontext aufrufKontext,
        TestAuthentifizierungErgebnis authentifzierungErgebnis) {
        aufrufKontext.setRolle(authentifzierungErgebnis.getRollen());
        aufrufKontext.setRollenErmittelt(true);

    }

    @Override
    public Object erzeugeCacheSchluessel(AufrufKontext aufrufKontext) {
        // Dies ist nur eine beispielhafte Dummy-Implementierung. Die Nutzung von hashcode() ist nicht
        // eindeutig genug für einen produktiven Einsatz!
        return Objects.hash(
            aufrufKontext.getDurchfuehrenderBenutzerKennung(),
            aufrufKontext.getDurchfuehrenderBenutzerPasswort(),
            aufrufKontext.getDurchfuehrendeBehoerde());
    }

    public void reset() {
        this.paramAuthentifiziereAufrufKontext = null;
        this.paramLogoutAuthentifzierungErgebnis = null;
        this.resultAuthentifiziere = null;
        this.authentifiziereException = null;
        this.countAuthentifziere = 0;
    }

    public AufrufKontext getParamAuthentifiziereAufrufKontext() {
        return this.paramAuthentifiziereAufrufKontext;
    }

    public TestAuthentifizierungErgebnis getParamLogoutAuthentifzierungErgebnis() {
        return this.paramLogoutAuthentifzierungErgebnis;
    }

    public void setResultAuthentifiziere(String... rollen) {
        this.resultAuthentifiziere = new TestAuthentifizierungErgebnis();
        this.resultAuthentifiziere.setRollen(rollen);
    }

    public int getCountAuthentifziere() {
        return this.countAuthentifziere;
    }

    public void setAuthentifiziereException(SicherheitTechnicalRuntimeException authentifiziereException) {
        this.authentifiziereException = authentifiziereException;
    }
}
