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
package de.bund.bva.isyfact.aufrufkontext.common.exception;

/**
 * Der übergebene Parameter ist null oder leer.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security (OAuth2) and the isy-security library.
 */
@Deprecated
public class AufrufKontextFehlerhaftException extends AufrufKontextTechnicalRuntimeException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8687916866752337910L;

    /**
     * Der übergebene Parameter ist null oder leer.
     */
    public AufrufKontextFehlerhaftException() {
        super(AusnahmeId.UEBERGEBENER_PARAMETER_FALSCH, new String[] { "AufrufKontext" });
    }

}
