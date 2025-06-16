package de.bund.bva.isyfact.sicherheit.accessmgr.test;

import de.bund.bva.isyfact.sicherheit.accessmgr.AuthentifzierungErgebnis;

public class TestAuthentifizierungErgebnis implements AuthentifzierungErgebnis {

    private static final long serialVersionUID = 1L;

    private String[] rollen;

    public String[] getRollen() {
        return this.rollen;
    }

    public void setRollen(String[] rollen) {
        this.rollen = rollen;
    }

}
