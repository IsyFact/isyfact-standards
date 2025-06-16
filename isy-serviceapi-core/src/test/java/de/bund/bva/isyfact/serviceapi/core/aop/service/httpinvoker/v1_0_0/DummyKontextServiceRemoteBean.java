package de.bund.bva.isyfact.serviceapi.core.aop.service.httpinvoker.v1_0_0;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * RemoteBean interface of the dummy service.
 */
public interface DummyKontextServiceRemoteBean {

    String stelltLoggingKontextNichtBereitOhneAufrufKontext();

    String stelltLoggingKontextNichtBereitMitAufrufKontext(AufrufKontextTo aufrufKontextTo);

    String stelltLoggingKontextBereitOhneAufrufKontextErwartet();

    String stelltLoggingKontextBereitMitAufrufKontextNichtErwartet(AufrufKontextTo aufrufKontextTo);

    String stelltLoggingKontextBereitOhneAufrufKontextNichtErwartet();

    String stelltLoggingKontextBereitMitAufrufKontext(AufrufKontextTo aufrufKontextTo);

}
