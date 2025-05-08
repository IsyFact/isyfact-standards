package de.bund.bva.isyfact.serviceapi.core.aop.test;

import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereit;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * A class for testing the {@link StelltAufrufKontextBereit} annotation.
 * <p>
 * Provides methods with different configurations of the annotation.
 */
public class AufrufKontextSstTestBean {

    public void stelltAufrufKontextNichtBereitOhneParameter() {
        // noop
    }

    public void stelltAufrufKontextNichtBereitMitParameter(AufrufKontextTo aufrufKontextTo) {
        // noop
    }

    @StelltAufrufKontextBereit
    public void stelltAufrufKontextBereitOhneParameter() {
        // noop
    }

    @StelltAufrufKontextBereit
    public void stelltAufrufKontextBereitMitParameter(AufrufKontextTo aufrufKontextTo) {
        // noop
    }

    @StelltAufrufKontextBereit
    public void stelltAufrufKontextBereitMitMehrerenParametern(AufrufKontextTo aufrufKontextTo, String dummy) {
        // noop
    }

    @StelltAufrufKontextBereit
    public void stelltAufrufKontextBereitMitMehrerenParameterKontextHinten(String dummy,
                                                                           AufrufKontextTo aufrufKontextTo) {
        // noop
    }

    @StelltAufrufKontextBereit
    public void stelltAufrufKontextBereitMitMehrerenParameterOhneKontext(String dummy, String aufrufKontextTo) {
        // noop
    }

    @StelltAufrufKontextBereit
    public void stelltAufrufKontextBereitMitMehrerenParameterMehrereKontexte(AufrufKontextTo aufrufKontextTo,
                                                                             AufrufKontextTo createAufrufKontextTo) {
        // noop

    }

}
