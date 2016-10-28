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
package de.bund.bva.pliscommon.sicherheit.common.exception;

import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Wird geworfen, wenn der {@link AccessManager} die Authentifizierung abgelehnt hat.
 * 
 */
public class AuthentifizierungFehlgeschlagenException extends SicherheitTechnicalRuntimeException {

    /**
     * Serial Uid.
     */
    private static final long serialVersionUID = 2428584102863876150L;

    /**
     * Erstellt eine Exception mit AusnahmeId und Parametern mit denen der Fehlertext parametrisiert wird.
     * 
     * @param detailMessage
     *            Den oder die Parameter für den Fehlertext
     */
    public AuthentifizierungFehlgeschlagenException(String detailMessage) {
        super(SicherheitFehlerSchluessel.MSG_AUTHENTIFIZIERUNG_FEHLGESCHLAGEN, detailMessage);
    }

}
