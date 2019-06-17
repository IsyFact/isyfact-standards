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

import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Die Basisklasse für alle technischen RuntimeExceptions der Komponente Sicherheit.
 * 
 */
public abstract class SicherheitTechnicalRuntimeException extends TechnicalRuntimeException {

    /**
     * UID.
     */
    private static final long serialVersionUID = 5561951645613590935L;

    /**
     * Konstruktor mit AusnahmeId und Parameter für einen Fehlertext, welche an die Vaterklasse weitergegeben
     * werden.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param parameter
     *            Die Parameter
     */
    protected SicherheitTechnicalRuntimeException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, new SicherheitFehlertextProvider(), parameter);
    }

    /**
     * Konstruktor mit AusnahmeId, auslösendem Fehler und Parametern für die Fehlernachricht.
     * 
     * @param ausnahmeId
     *            Die Id der Ausnahme
     * @param throwable
     *            Der auslösende Fehler
     * @param parameter
     *            Parameter für den Fehlertext
     */
    protected SicherheitTechnicalRuntimeException(String ausnahmeId, Throwable throwable, String... parameter) {
        super(ausnahmeId, throwable, new SicherheitFehlertextProvider(), parameter);
    }

}
