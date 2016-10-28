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

import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Diese Annotation wird geworfen, wenn eine erwartete Annotation nicht gefunden wurde.
 */
public class AnnotationFehltRuntimeException extends SicherheitTechnicalRuntimeException {

    /** Serial Version UId. */
    private static final long serialVersionUID = 0L;

    /**
     * Erstellt die Exception mit der AusnahmeId und Paramtern für den Fehlertext.
     * 
     * @param parameter
     *            Die Parameter.
     */
    public AnnotationFehltRuntimeException(String... parameter) {
        super(SicherheitFehlerSchluessel.MSG_ANNOTATION_FEHLT_AN_METHODE, parameter);
    }
}
