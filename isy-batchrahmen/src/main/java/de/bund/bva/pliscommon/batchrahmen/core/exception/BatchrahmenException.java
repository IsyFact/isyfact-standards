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
package de.bund.bva.pliscommon.batchrahmen.core.exception;

import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.pliscommon.batchrahmen.core.rahmen.impl.NachrichtenProvider;

/**
 * Oberklasse für alle Exceptions des Batchrahmens.
 * 
 *
 */
public abstract class BatchrahmenException extends RuntimeException {
    /**
     * AusnahmeId, wird für Ergebnisprotokoll verwendet.
     */
    private String ausnahmeId;    
    
    /**
     * UID der Exception.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Ergzeugt einen Fehler.
     * @param ausnahmeId
     *            Die Id der Ausnahme, wird zum laden des Fehlertexts verdendet.
     * @param cause
     *            Die Ursache des Fehlers.
     * @param parameter
     *            Parameter für die Fehlernachricht.
     */
    public BatchrahmenException(String ausnahmeId, Throwable cause, String... parameter) {
        super(NachrichtenProvider.getMessage(ausnahmeId, parameter), cause);
        this.ausnahmeId = ausnahmeId;
    }
    
    /**
     * Ergzeugt einen Fehler.
     * @param ausnahmeId Die Id der Ausnahme, wird zum laden des Fehlertexts verdendet.
     * @param parameter Parameter für die Fehlernachricht.
     */
    public BatchrahmenException(String ausnahmeId, String... parameter) {
        super(NachrichtenProvider.getMessage(ausnahmeId, parameter));
        this.ausnahmeId = ausnahmeId;
    }    
    
    /**
     * Ergzeugt einen Fehler.
     * @param ausnahmeId Die Id der Ausnahme, wird zum laden des Fehlertexts verdendet.
     */
    public BatchrahmenException(String ausnahmeId) {
        super(NachrichtenProvider.getMessage(ausnahmeId));
        this.ausnahmeId = ausnahmeId;
    }

    /**
     * Liefert die Id der Ausnahme.
     * @return die AusnahmeId
     */
    public String getAusnahmeId() {
        return ausnahmeId;
    }    
    
    /**
     * Liefert den ReturnCode, den diese Exception auslösen soll.
     * @return Der ReturnCode mit dem der Batch beendet werden soll.
     */
    public abstract BatchReturnCode getReturnCode();
}
