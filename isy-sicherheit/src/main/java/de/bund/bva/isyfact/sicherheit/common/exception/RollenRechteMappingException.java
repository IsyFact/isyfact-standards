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
package de.bund.bva.isyfact.sicherheit.common.exception;

/**
 * Diese Klasse sammelt alle Exceptions die beim Erstellen (aus XML) oder dem Zugriff auf das
 * RollenRechteMapping auftreten.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class RollenRechteMappingException extends SicherheitTechnicalRuntimeException {

    /**
     * SerialId.
     */
    private static final long serialVersionUID = -1532697675642517514L;

    /**
     * Erstellt eine Exception mit AusnahmeId.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     */
    public RollenRechteMappingException(String ausnahmeId) {
        super(ausnahmeId);
    }

    /**
     * Erstellt eine Exception mit AusnahmeId und Parametern mit denen der Fehlertext parametrisiert wird.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param parameter
     *            Den oder die Parameter für den Fehlertext
     */
    public RollenRechteMappingException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

    /**
     * Erstellt eine Exception mit AusnahmeId, auslösendem Fehler und Parameter für den Fehlertext.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param t
     *            Der auslösende Fehler
     * @param parameter
     *            Parameter für den Fehlertext
     */
    public RollenRechteMappingException(String ausnahmeId, Throwable t, String... parameter) {
        super(ausnahmeId, t, parameter);
    }
}
