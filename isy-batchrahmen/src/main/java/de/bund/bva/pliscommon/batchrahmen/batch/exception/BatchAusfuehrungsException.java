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
package de.bund.bva.pliscommon.batchrahmen.batch.exception;

import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;

/**
 * Diese Exception wird von Implementierungen des Interfaces
 * {@link BatchAusfuehrungsBean} geworfen, wenn bei der Verarbeitung ein Fehler
 * auftritt. Optional kann ein {@link BatchReturnCode} übergeben werden. Dieser
 * wird vom Batchrahmen ausgewertet. Falls kein {@link BatchReturnCode}
 * angegeben wird, bestimmt der Batchrahmen den ReturnCode.
 * 
 * 
 */
public class BatchAusfuehrungsException extends Exception {
    /**
     * AusnahmeId, wird für Ergbenisprotokoll verwendet.
     */
    private String ausnahmeId;    
    
    /**
     * ReturnCode, mit dem Batch beendet werden soll.
     */
    private BatchReturnCode returnCode;

    /** Die Versionskennung. */
    private static final long serialVersionUID = -6209498174178729341L;

    /**
     * Erzeuge neue Ausnahme.
     * 
     * @param ausnahmeId
     *            ID des Fehlers
     * @param msg
     *            die Fehlernachricht
     */
    public BatchAusfuehrungsException(String ausnahmeId, String msg) {
        super(msg);
        this.ausnahmeId = ausnahmeId;
    }

    /**
     * Erzeuge neue Ausnahme.
     * @param ausnahmeId
     *            ID des Fehlers
     * @param cause
     *            Ursache des Fehlers
     */
    public BatchAusfuehrungsException(String ausnahmeId, Throwable cause) {
        super(cause);
        this.ausnahmeId = ausnahmeId;        
    }

    /**
     * Erzeuge neue Ausnahme.
     * 
     * @param ausnahmeId
     *            ID des Fehlers
     * @param msg
     *            die Fehlernachricht
     * @param cause
     *            der Ursprungs-Fehler
     */
    public BatchAusfuehrungsException(String ausnahmeId, String msg, Throwable cause) {
        super(msg, cause);
        this.ausnahmeId = ausnahmeId;
    }

    /**
     * Erzeuge {@link BatchAusfuehrungsException} zur einer
     * {@link PlisException}. AusnahmeId, Message und Cause werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     */
    public BatchAusfuehrungsException(PlisException cause) {
        super(cause.getMessage(), cause);
        this.ausnahmeId = cause.getAusnahmeId();
    }

    /**
     * Erzeuge {@link BatchAusfuehrungsException} zur einer
     * {@link PlisTechnicalRuntimeException}. AusnahmeId, Message und Cause
     * werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     */
    public BatchAusfuehrungsException(PlisTechnicalRuntimeException cause) {
        super(cause.getMessage(), cause);
        this.ausnahmeId = cause.getAusnahmeId();
    }

    /**
     * Erzeuge neue Ausnahme.
     * 
     * @param ausnahmeId
     *            ID des Fehlers
     * @param msg
     *            die Fehlernachricht
     * @param returnCode
     *            mit dem der Batch beendet werden soll.
     */
    public BatchAusfuehrungsException(String ausnahmeId, String msg, BatchReturnCode returnCode) {
        super(msg);
        this.ausnahmeId = ausnahmeId;
        this.returnCode = returnCode;
    }

    /**
     * Erzeuge neue Ausnahme.
     * @param ausnahmeId
     *            ID des Fehlers
     * @param cause
     *            Ursaceh des Fehlers
     * @param returnCode
     *            mit dem der Batch beendet werden soll.
     */
    public BatchAusfuehrungsException(String ausnahmeId, Throwable cause, BatchReturnCode returnCode) {
        super(cause);
        this.ausnahmeId = ausnahmeId;        
        this.returnCode = returnCode;
    }

    /**
     * Erzeuge neue Ausnahme.
     * 
     * @param ausnahmeId
     *            ID des Fehlers
     * @param msg
     *            die Fehlernachricht
     * @param cause
     *            der Ursprungs-Fehler
     * @param returnCode
     *            mit dem der Batch beendet werden soll.
     */
    public BatchAusfuehrungsException(String ausnahmeId, String msg, Throwable cause,
            BatchReturnCode returnCode) {
        super(msg, cause);
        this.ausnahmeId = ausnahmeId;
        this.returnCode = returnCode;
    }

    /**
     * Erzeuge {@link BatchAusfuehrungsException} zur einer
     * {@link PlisException}. AusnahmeId, Message und Cause werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     * @param returnCode
     *            mit dem der Batch beendet werden soll.
     */
    public BatchAusfuehrungsException(PlisException cause, BatchReturnCode returnCode) {
        super(cause.getMessage(), cause);
        this.ausnahmeId = cause.getAusnahmeId();
        this.returnCode = returnCode;
    }

    /**
     * Erzeuge {@link BatchAusfuehrungsException} zur einer
     * {@link PlisTechnicalRuntimeException}. AusnahmeId, Message und Cause
     * werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     * @param returnCode
     *            mit dem der Batch beendet werden soll.
     */
    public BatchAusfuehrungsException(PlisTechnicalRuntimeException cause, BatchReturnCode returnCode) {
        super(cause.getMessage(), cause);
        this.ausnahmeId = cause.getAusnahmeId();        
        this.returnCode = returnCode;
    }

    /**
     * Liefert ReturnCode, mit dem Batch beendet werden soll.
     * @return ReturnCode, mit dem Batch beendet werden soll.
     */
    public BatchReturnCode getReturnCode() {
        return returnCode;
    }
    
    /**
     * Liefert die ausnahmeId.
     * @return Die ausnahmeId
     */
    public String getAusnahmeId() {
        return ausnahmeId;
    }    

}
