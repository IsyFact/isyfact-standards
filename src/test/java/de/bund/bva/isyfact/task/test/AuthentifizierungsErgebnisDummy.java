package de.bund.bva.isyfact.task.test;

import de.bund.bva.pliscommon.sicherheit.accessmgr.AuthentifzierungErgebnis;

public class AuthentifizierungsErgebnisDummy implements AuthentifzierungErgebnis {

    private static final long serialVersionUID = 1L;
    private String[] rollenIds;

    public String[] getRollenIds() {
        return rollenIds;
    }

    public void setRollenIds(String[] rollenIds) {
        this.rollenIds = rollenIds;
    }
}
