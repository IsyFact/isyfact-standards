package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import java.io.Serial;
import java.io.Serializable;

/**
 * Die Persistenz-Klasse fuer einen Konfigurations-Parameter des Batches.
 * Dies sind sowohl Aufruf-Parameter als auch Einträge der Property-Datei.
 * 
 * 
 */
public class BatchKonfigurationsParameter implements Serializable {

    /** Die Versions-ID.  */
    @Serial
    private static final long serialVersionUID = -8564991741012915474L;


    /** Der Wert des Konfigurations-Parameters. */
    private String parameterWert;
    
    /** Die ID des Batches fuer diesen Parameter. */
    private String batchId;

    /** Der Name des Konfigurations-Parameters. */
    private String parameterName;
    
    
    /**
     * Konstruktor mit allen Werten.
     * 
     * @param batchId         die Batch-Id.
     * @param parameterName   der Name des Parameters.
     * @param parameterWert   der Wert des Parameters.
     */
    public BatchKonfigurationsParameter(String batchId, String parameterName, String parameterWert) {
        this.parameterName = parameterName;
        this.batchId = batchId;
        this.parameterWert = parameterWert;
    }    

    /**
     * Konstruktor fuer Hibernate.
     */
    public BatchKonfigurationsParameter() {
        super();
    }    

    /**
     * gibt die ID des Batches zurueck.
     * 
     * @return die Batch-ID.
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * setzt die ID des Batches.
     * 
     * @param batchId
     *            die zu setzende Batch-ID.
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    /**
     * gibt den Namen des Parameters zurueck.
     * 
     * @return der Name des Parameters.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * setzt den Namen des Parameters.
     * @param parameterName
     *            der zu setzende Parameter-Name.
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * liest den Wert des Parameters aus.
     * 
     * @return the parameterWert der Wert des Parameters.
     */
    public String getParameterWert() {
        return parameterWert;
    }

    /**
     * setzt den Wert des Parameters.
     * @param parameterWert
     *            der Parameter-Wert.
     */
    public void setParameterWert(String parameterWert) {
        this.parameterWert = parameterWert;
    }    


    /**
     * errechnet den HashCode ueber die Batch-ID und den Parameter-Namen.
     * @return der Hash-Code.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchId == null) ? 0 : batchId.hashCode());
        result =
                prime
                        * result
                        + ((parameterName == null) ? 0 : parameterName
                                .hashCode());
        result =
                prime
                        * result
                        + ((parameterWert == null) ? 0 : parameterWert
                                .hashCode());
        return result;
    }

    /**
     * berechnet equals ueber die Batch-Id und den Parameter-Namen.
     * @param obj
     *            das andere Objekt.
     * @return wahr, falls gleich. Falsch, falls nicht.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BatchKonfigurationsParameter other =
                (BatchKonfigurationsParameter) obj;
        if (batchId == null) {
            if (other.batchId != null) {
                return false;
            }
        } else if (!batchId.equals(other.batchId)) {
            return false;
        }
        if (parameterName == null) {
            if (other.parameterName != null) {
                return false;
            }
        } else if (!parameterName.equals(other.parameterName)) {
            return false;
        }
        if (parameterWert == null) {
            if (other.parameterWert != null) {
                return false;
            }
        } else if (!parameterWert.equals(other.parameterWert)) {
            return false;
        }
        return true;
    }

}
