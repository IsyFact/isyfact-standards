package de.bund.bva.isyfact.serviceapi.core.aop.service.httpinvoker.v1_0_0;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereit;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Implementation of the DummyService.
 */
public class DummyKontextServiceImpl implements DummyKontextServiceRemoteBean {

    @Override
    public String stelltLoggingKontextNichtBereitOhneAufrufKontext() {
        return MdcHelper.liesKorrelationsId();
    }

    @Override
    public String stelltLoggingKontextNichtBereitMitAufrufKontext(AufrufKontextTo aufrufKontextTo) {
        return MdcHelper.liesKorrelationsId();
    }

    @Override
    @StelltLoggingKontextBereit(nutzeAufrufKontext = false)
    public String stelltLoggingKontextBereitOhneAufrufKontextErwartet() {
        return MdcHelper.liesKorrelationsId();
    }

    @Override
    @StelltLoggingKontextBereit(nutzeAufrufKontext = false)
    public String stelltLoggingKontextBereitMitAufrufKontextNichtErwartet(AufrufKontextTo aufrufKontextTo) {
        return MdcHelper.liesKorrelationsId();
    }

    @Override
    @StelltLoggingKontextBereit
    public String stelltLoggingKontextBereitOhneAufrufKontextNichtErwartet() {
        return MdcHelper.liesKorrelationsId();
    }

    @Override
    @StelltLoggingKontextBereit(nutzeAufrufKontext = true)
    public String stelltLoggingKontextBereitMitAufrufKontext(AufrufKontextTo aufrufKontextTo) {
        return MdcHelper.liesKorrelationsId();
    }

}
