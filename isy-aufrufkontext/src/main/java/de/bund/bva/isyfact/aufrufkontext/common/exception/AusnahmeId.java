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
 * Alle verfügbaren AusnahmeIds.
 * 
 *
 */
public enum AusnahmeId {

    /** Der übergebene Parameter ist null oder leer. */
    UEBERGEBENER_PARAMETER_FALSCH("AUFRU00001"),

    AUFRUFKONTEXT_KEIN_DEFAULT_KOSTRUKTOR("AUFRU00002");

    /** der Code für den FehlertextProvider. */
    private String code;

    /**
     * Liefert das Feld {@link #code} zurück.
     * @return Wert von code
     */
    public String getCode() {
        return code;
    }

    private AusnahmeId(String code) {
        this.code = code;
    }

}
