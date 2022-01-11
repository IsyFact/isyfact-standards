package de.bund.bva.isyfact.serviceapi.core.bridge;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/** Adapter for IF1 AufrufKontextTo.
 * Delegate all calls to IF1 AufrufKontextTo. */
public class AufrufKontextToIF1Facade extends
    de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo {

    /** IsyFact 1 AufrufKontextTo to which all calls should be delegated. */
    private final AufrufKontextTo if1AufrufKontextTo;

    public AufrufKontextToIF1Facade(
        AufrufKontextTo if1AufrufKontextTo) {
        this.if1AufrufKontextTo = if1AufrufKontextTo;
    }

    @Override
    public String getDurchfuehrenderBenutzerKennung() {
        return if1AufrufKontextTo.getDurchfuehrenderBenutzerKennung();
    }

    @Override
    public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung) {
        if1AufrufKontextTo.setDurchfuehrenderBenutzerKennung(durchfuehrenderBenutzerKennung);
    }

    @Override
    public String getDurchfuehrenderBenutzerPasswort() {
        return if1AufrufKontextTo.getDurchfuehrenderBenutzerPasswort();
    }

    @Override
    public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort) {
        if1AufrufKontextTo.setDurchfuehrenderBenutzerPasswort(durchfuehrenderBenutzerPasswort);
    }

    @Override
    public String getDurchfuehrendeBehoerde() {
        return if1AufrufKontextTo.getDurchfuehrendeBehoerde();
    }

    @Override
    public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde) {
        if1AufrufKontextTo.setDurchfuehrendeBehoerde(durchfuehrendeBehoerde);
    }

    @Override
    public String getKorrelationsId() {
        return if1AufrufKontextTo.getKorrelationsId();
    }

    @Override
    public void setKorrelationsId(String korrelationsId) {
        if1AufrufKontextTo.setKorrelationsId(korrelationsId);
    }

    @Override
    public String[] getRolle() {
        return if1AufrufKontextTo.getRolle();
    }

    @Override
    public void setRolle(String[] rolle) {
        if1AufrufKontextTo.setRolle(rolle);
    }

    @Override
    public boolean isRollenErmittelt() {
        return if1AufrufKontextTo.isRollenErmittelt();
    }

    @Override
    public void setRollenErmittelt(boolean rollenErmittelt) {
        if1AufrufKontextTo.setRollenErmittelt(rollenErmittelt);
    }

    @Override
    public String getDurchfuehrenderSachbearbeiterName() {
        return if1AufrufKontextTo.getDurchfuehrenderSachbearbeiterName();
    }

    @Override
    public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName) {
        if1AufrufKontextTo.setDurchfuehrenderSachbearbeiterName(durchfuehrenderSachbearbeiterName);
    }
}
