package de.bund.bva.isyfact.bridges.integration.sst;

import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public interface IsyTestRemoteBean {
    String ping(AufrufKontextTo aufrufKontextTo, boolean fail) throws IsyTestToException;
    String ping(String str);
}
