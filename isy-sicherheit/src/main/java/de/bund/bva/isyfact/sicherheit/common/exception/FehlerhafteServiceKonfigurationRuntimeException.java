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

import de.bund.bva.isyfact.sicherheit.annotation.Gesichert;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Diese Exception wird geworfen, wenn eine Methode durch eine
 * {@link Gesichert}-Annotation gesichert wurde, aber die
 * erforderlichen Rechte in der Annotation nicht angegeben wurden.
 * 
 */
public class FehlerhafteServiceKonfigurationRuntimeException extends SicherheitTechnicalRuntimeException {

    /** Serial Version UId. */
    private static final long serialVersionUID = -3537929251814846397L;

    /**
     * Erstellt die Exception mit der AusnahmeId und Paramtern für den Fehlertext.
     */
    public FehlerhafteServiceKonfigurationRuntimeException() {
        super(SicherheitFehlerSchluessel.MSG_TAG_KONFIGURATION_FEHLERHAFT, new String[0]);
    }
}
