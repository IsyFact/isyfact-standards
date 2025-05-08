package de.bund.bva.isyfact.serviceapi.core.aop.test;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class LoggingKontextAspectService {

    private String korrelationsIDLetzterAufruf;

    public void aufrufOhneParameter() {
        saveKorrelationsID();
    }

    public void aufrufOhneAufrufKontext(Integer zahl) {
        saveKorrelationsID();
    }

    public void aufrufMitAufrufKontext(AufrufKontextTo to) {
        saveKorrelationsID();
    }

    public void aufrufMitException() throws Exception {
        saveKorrelationsID();
        throw new Exception();
    }

    public void saveKorrelationsID() {
        this.korrelationsIDLetzterAufruf = MdcHelper.liesKorrelationsId();
    }

    public String getKorrelationsIDLetzterAufruf() {
        return korrelationsIDLetzterAufruf;
    }

    public void setKorrelationsIDLetzterAufruf(String korrelationsIDLetzterAufruf) {
        this.korrelationsIDLetzterAufruf = korrelationsIDLetzterAufruf;
    }
}
