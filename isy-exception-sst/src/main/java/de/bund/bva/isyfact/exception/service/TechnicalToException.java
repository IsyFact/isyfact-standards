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
package de.bund.bva.isyfact.exception.service;

/**
 * Abstrakte Implementierung für alle <b>technischen Schnittstellen-Exceptions</b>.
 * 
 */
public abstract class TechnicalToException extends ToException {
    /**
     * Einziger Konstruktor. Es ist notwendig die Nachricht direkt zu übergeben, da diese nicht nachträglich
     * gesetzt werden kann. Zusätzlich nimmt dieser Konstrukt noch die Ausnahme-ID und die Unique-ID entgegen.
     * 
     * @param message
     *            Fehlertext.
     * @param ausnahmeId
     *            AusnahmeID
     * @param uniqueId
     *            eineindeutige ID des Fehlers
     */
    protected TechnicalToException(String message, String ausnahmeId, String uniqueId) {
        super(message, ausnahmeId, uniqueId);
    }
}
