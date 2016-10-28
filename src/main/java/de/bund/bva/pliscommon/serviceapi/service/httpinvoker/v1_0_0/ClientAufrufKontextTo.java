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
package de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0;

import java.io.Serializable;

/**
 * Das Transportobjekt für Client-Authentifizierungsdaten. Wird von ClientProxies verwendet, wenn interne
 * Services externe Services über das Servicegateway aufrufen.
 * 
 */
public class ClientAufrufKontextTo implements Serializable {

    /** UID. */
    private static final long serialVersionUID = -5564941823332253846L;

    /**
     * Die Kennung zur Authentifzierung beim externen Service.
     */
    private String kennung;

    /**
     * Das Kennwort zur Authentifzierung beim externen Service.
     */
    private String kennwort;

    /**
     * Zertifikat und privater Schlüssel im PKCS12-Format zur Authentifizierung beim externen Service.
     */
    private byte[] zertifikat;

    /**
     * Das Kennwort für den privaten Schlüssel in {@link #zertifikat}.
     */
    private String zertifikatKennwort;

    /**
     * @return the kennung
     */
    public String getKennung() {
        return kennung;
    }

    /**
     * @param kennung
     *            the kennung to set
     */
    public void setKennung(String kennung) {
        this.kennung = kennung;
    }

    /**
     * @return the kennwort
     */
    public String getKennwort() {
        return kennwort;
    }

    /**
     * @param kennwort
     *            the kennwort to set
     */
    public void setKennwort(String kennwort) {
        this.kennwort = kennwort;
    }

    /**
     * @return the zertifikat
     */
    public byte[] getZertifikat() {
        return zertifikat;
    }

    /**
     * @param zertifikat
     *            the zertifikat to set
     */
    public void setZertifikat(byte[] zertifikat) {
        this.zertifikat = zertifikat;
    }

    /**
     * @return the zertifikatKennwort
     */
    public String getZertifikatKennwort() {
        return zertifikatKennwort;
    }

    /**
     * @param zertifikatKennwort
     *            the zertifikatKennwort to set
     */
    public void setZertifikatKennwort(String zertifikatKennwort) {
        this.zertifikatKennwort = zertifikatKennwort;
    }

}
