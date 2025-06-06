package de.bund.bva.isyfact.batchrahmen.batch.exception;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

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
     * {@link BaseException}. AusnahmeId, Message und Cause werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     */
    public BatchAusfuehrungsException(BaseException cause) {
        super(cause.getMessage(), cause);
        this.ausnahmeId = cause.getAusnahmeId();
    }

    /**
     * Erzeuge {@link BatchAusfuehrungsException} zur einer
     * {@link TechnicalRuntimeException}. AusnahmeId, Message und Cause
     * werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     */
    public BatchAusfuehrungsException(TechnicalRuntimeException cause) {
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
     * {@link BaseException}. AusnahmeId, Message und Cause werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     * @param returnCode
     *            mit dem der Batch beendet werden soll.
     */
    public BatchAusfuehrungsException(BaseException cause, BatchReturnCode returnCode) {
        super(cause.getMessage(), cause);
        this.ausnahmeId = cause.getAusnahmeId();
        this.returnCode = returnCode;
    }

    /**
     * Erzeuge {@link BatchAusfuehrungsException} zur einer
     * {@link TechnicalRuntimeException}. AusnahmeId, Message und Cause
     * werden übernommen.
     * 
     * @param cause
     *            Ursprünglicher Fehler
     * @param returnCode
     *            mit dem der Batch beendet werden soll.
     */
    public BatchAusfuehrungsException(TechnicalRuntimeException cause, BatchReturnCode returnCode) {
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
