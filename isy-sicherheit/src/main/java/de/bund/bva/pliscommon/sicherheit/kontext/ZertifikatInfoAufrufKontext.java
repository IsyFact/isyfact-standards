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
package de.bund.bva.pliscommon.sicherheit.kontext;

import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;

/**
 * Aufrufkontext zur Übermittlung von Zertifikatsinformationen.
 *
 */

public class ZertifikatInfoAufrufKontext extends AufrufKontextImpl {
    private String clientZertifikat;

    private String clientZertifikatDn;

    private String zertifikatOu;

    public String getClientZertifikat() {
        return this.clientZertifikat;
    }

    public void setClientZertifikat(String clientZertifikat) {
        this.clientZertifikat = clientZertifikat;
    }

    public String getClientZertifikatDn() {
        return this.clientZertifikatDn;
    }

    public void setClientZertifikatDn(String clientZertifikatDn) {
        this.clientZertifikatDn = clientZertifikatDn;
    }

    public String getZertifikatOu() {
        return this.zertifikatOu;
    }

    public void setZertifikatOu(String zertifikatOu) {
        this.zertifikatOu = zertifikatOu;
    }

}
