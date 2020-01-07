package de.bund.bva.pliscommon.bridges.integration.sst.impl;

import de.bund.bva.pliscommon.bridges.integration.sst.PlisTestToException;
import de.bund.bva.pliscommon.bridges.integration.sst.PlisTestRemoteBean;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class PlisTestExceptionFassade implements PlisTestRemoteBean {

    public String ping(AufrufKontextTo aufrufKontextTo, boolean fail) throws PlisTestToException {
        if (fail) {
            throw new PlisTestToException("Dummy Fehlertext", "Dummy Ausnahme ID", "Dummy Unique ID");
        }
        return aufrufKontextTo.getDurchfuehrenderSachbearbeiterName();
    }

}
