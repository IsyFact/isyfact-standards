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

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class LoggingKontextAspectService {

    private String korrelationsIDLetzterAufruf;

    public void aufrufOhneParameter() {
        saveKorrelationsID();
    }

    public void aufrufOhneAufrufKontext(Integer zahl) {
        saveKorrelationsID();
    }

    public void aufrufMitAufrufKontext(AufrufKontextTo to) {
        saveKorrelationsID();
    }

    public void aufrufMitException() throws Exception {
        saveKorrelationsID();
        throw new Exception();
    }

    public void saveKorrelationsID(){
        this.korrelationsIDLetzterAufruf = MdcHelper.liesKorrelationsId();
    }

    public String getKorrelationsIDLetzterAufruf() {
        return korrelationsIDLetzterAufruf;
    }

    public void setKorrelationsIDLetzterAufruf(String korrelationsIDLetzterAufruf) {
        this.korrelationsIDLetzterAufruf = korrelationsIDLetzterAufruf;
    }
}
