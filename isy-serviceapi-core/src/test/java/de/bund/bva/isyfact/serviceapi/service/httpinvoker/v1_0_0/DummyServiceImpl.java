package de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0;

import java.util.ArrayList;
import java.util.List;

import de.bund.bva.isyfact.serviceapi.core.httpinvoker.user.User;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Implementation of the DummyService.
 *
 *
 */
public class DummyServiceImpl implements DummyServiceRemoteBean {
    /**
     * Milliseconds that the call is delayed.
     */
    private int waitTime;

    /**
     * Counter for calls.
     */
    private int anzahlAufrufe;

    private List<User> users = new ArrayList<>();

    /**
     * Getter for 'anzahlAufrufe'.
     * @return The value of anzahlAufrufe.
     */
    public int getAnzahlAufrufe() {
        return this.anzahlAufrufe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String ping(String message) {
        try {
            this.anzahlAufrufe++;
            Thread.sleep(this.waitTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public String ping(AufrufKontextTo aufrufKontextTo, String message) {
        return this.ping(message);
    }

    @Override
    public String addUser(User user) {
        if(user != null){
            this.users.add(user);
            return "Added user successful.";
        }
        return "Failed to add user";
    }

    /**
     * Setter for 'waitTime'.
     * @param waitTime
     *            New value for waitTime.
     */
    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}
