package de.bund.bva.pliscommon.bridges.integration.sst;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public interface PlisTestRemoteBean {
    String ping(AufrufKontextTo aufrufKontextTo, boolean fail) throws PlisTestToException;
}
