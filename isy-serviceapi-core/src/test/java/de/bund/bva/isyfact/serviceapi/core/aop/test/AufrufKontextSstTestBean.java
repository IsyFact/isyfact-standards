/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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
