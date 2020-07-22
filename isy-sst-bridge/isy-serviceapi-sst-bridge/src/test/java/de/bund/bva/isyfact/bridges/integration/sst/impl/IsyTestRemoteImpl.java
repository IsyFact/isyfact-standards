package de.bund.bva.isyfact.bridges.integration.sst.impl;

import de.bund.bva.isyfact.bridges.integration.sst.IsyTestRemoteBean;
import de.bund.bva.isyfact.bridges.integration.sst.IsyTestToException;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class IsyTestRemoteImpl implements IsyTestRemoteBean {

    public String ping(AufrufKontextTo aufrufKontextTo, boolean fail) throws IsyTestToException {
        if (fail) {
            throw new IsyTestToException("Dummy Fehlertext", "Dummy Ausnahme ID", "Dummy Unique ID");
        }
        return aufrufKontextTo.getDurchfuehrenderSachbearbeiterName();
    }

    @Override
    public String ping(String str) {
        return new StringBuilder(str).reverse().toString();
    }

}
