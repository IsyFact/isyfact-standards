package de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0;

import de.bund.bva.isyfact.serviceapi.core.httpinvoker.user.User;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * RemoteBean-Interface of the Dummy-Service.
 */
public interface DummyServiceRemoteBean {

    /**
     * Returns the message.
     *
     * @param message The message.
     * @return The message.
     */
    String ping(String message);

    String ping(AufrufKontextTo aufrufKontextTo, String message);

    String addUser(User user);

}
