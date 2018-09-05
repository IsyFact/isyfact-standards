package de.bund.bva.isyfact.task.test;

import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;

public class AccessManagerDummy implements AccessManager<AufrufKontextImpl, AuthentifizierungsErgebnisDummy> {
    @Override
    public AuthentifizierungsErgebnisDummy authentifiziere(AufrufKontextImpl unauthentifizierterAufrufKontext)
        throws AuthentifizierungTechnicalException, AuthentifizierungFehlgeschlagenException {
        AuthentifizierungsErgebnisDummy ergebnis = null;

        switch (unauthentifizierterAufrufKontext.getDurchfuehrenderBenutzerKennung()) {
        case "TestUser1": ergebnis = new AuthentifizierungsErgebnisDummy();
                          ergebnis.setRollenIds(new String[] { "Rolle1" });
                          break;
        case "TestUser2": ergebnis = new AuthentifizierungsErgebnisDummy();
                          ergebnis.setRollenIds(new String[] { "Rolle2" });
                          break;
        case "TestUser3": ergebnis = new AuthentifizierungsErgebnisDummy();
                          ergebnis.setRollenIds(new String[] { });
                          break;
        }

        return ergebnis;
    }

    @Override
    public void logout(AuthentifizierungsErgebnisDummy authentifzierungErgebnis) {

    }

    @Override
    public boolean pingAccessManager() {
        return true;
    }

    @Override
    public boolean pingAccessManagerByLoginLogout(AufrufKontextImpl unauthentifizierterAufrufKontext) {
        return true;
    }

    @Override
    public void befuelleAufrufkontext(AufrufKontextImpl aufrufKontext,
        AuthentifizierungsErgebnisDummy authentifzierungErgebnis) {
            aufrufKontext.setRolle(authentifzierungErgebnis.getRollenIds());
            aufrufKontext.setRollenErmittelt(true);
    }

    @Override
    public Object erzeugeCacheSchluessel(AufrufKontextImpl aufrufKontext) {
        return null;
    }
}
