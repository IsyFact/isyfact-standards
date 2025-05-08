package de.bund.bva.isyfact.sicherheit.kontext;

import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;

/**
 * Aufrufkontext zur Übermittlung von Zertifikatsinformationen.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class ZertifikatInfoAufrufKontext extends AufrufKontextImpl {
    private String clientZertifikat;

    private String clientZertifikatDn;

    private String zertifikatOu;

    public String getClientZertifikat() {
        return this.clientZertifikat;
    }

    public void setClientZertifikat(String clientZertifikat) {
        this.clientZertifikat = clientZertifikat;
    }

    public String getClientZertifikatDn() {
        return this.clientZertifikatDn;
    }

    public void setClientZertifikatDn(String clientZertifikatDn) {
        this.clientZertifikatDn = clientZertifikatDn;
    }

    public String getZertifikatOu() {
        return this.zertifikatOu;
    }

    public void setZertifikatOu(String zertifikatOu) {
        this.zertifikatOu = zertifikatOu;
    }

}
