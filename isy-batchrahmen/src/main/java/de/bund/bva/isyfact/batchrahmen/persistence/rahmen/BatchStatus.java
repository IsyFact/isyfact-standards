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
package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Dies ist das Entität fuer einen Batch-Aufruf. Sie enthaelt die Kontext-
 * Informationen ueber den Aufruf sowie Informationen ueber den aktuellen
 * Status des Batches.
 */
@Entity
public class BatchStatus {
    /** Die ID des Batches. */
    @Id
    private String batchId;
    /** Der Name des Batches. */
    private String batchName;
    /** Der Status des Batches. Einer der Werte "ERFOLG", "FEHLER", "LAEUFT". */
    private String batchStatus;
    /** Die Liste der Aufruf-Parameter im Aufruf des Batches. */
    @ElementCollection
    private Set<BatchKonfigurationsParameter> konfigurationsParameter;
    /** Die Satznummer, bei welcher der letzte Commit durchgeführt wurde. */
    private long satzNummerLetztesCommit;
    /** Der Datenbank-Schluessel (bei DB-Queries), bei dem der letzte Commit 
     *  durchgefuehrt wurde. Hierbei ist es notwendig, dass in der Query ueber
     *  den Schluessel sortiert wurde.
     */
    private String schluesselLetztesCommit;
    /** Das Datum des letzten Batch-Starts. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumLetzterStart;
    /** Das Datum des letzten Fehler-Abbruch des Batches. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumLetzterAbbruch;
    /** Das Datum des letzten erfolgreichen Durchlauf des Batches. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumLetzterErfolg;
    
    /**
     * @return the batchId
     */
    public String getBatchId() {
        return batchId;
    }
    /**
     * @param batchId the batchId to set
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    /**
     * @return the batchName
     */
    public String getBatchName() {
        return batchName;
    }
    /**
     * @param batchName the batchName to set
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }
    /**
     * @return the batchStatus
     */
    public String getBatchStatus() {
        return batchStatus;
    }
    /**
     * @param batchStatus the batchStatus to set
     */
    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }
    /**
     * @return the aufrufParameter
     */
    public Set<BatchKonfigurationsParameter> getKonfigurationsParameter() {
        return konfigurationsParameter;
    }
    /**
     * @param konfigurationsParameter the Parameter to set
     */
    public void setKonfigurationsParameter(Set<BatchKonfigurationsParameter> konfigurationsParameter) {
        this.konfigurationsParameter = konfigurationsParameter;
    }
    /**
     * @return the satzNummerLetztesCommit
     */
    public long getSatzNummerLetztesCommit() {
        return satzNummerLetztesCommit;
    }
    /**
     * @param satzNummerLetztesCommit the satzNummerLetztesCommit to set
     */
    public void setSatzNummerLetztesCommit(long satzNummerLetztesCommit) {
        this.satzNummerLetztesCommit = satzNummerLetztesCommit;
    }
    /**
     * @return the schluesselLetztesCommit
     */
    public String getSchluesselLetztesCommit() {
        return schluesselLetztesCommit;
    }
    /**
     * @param schluesselLetztesCommit the schluesselLetztesCommit to set
     */
    public void setSchluesselLetztesCommit(String schluesselLetztesCommit) {
        this.schluesselLetztesCommit = schluesselLetztesCommit;
    }
    /**
     * @return the datumLetzterStart
     */
    public Date getDatumLetzterStart() {
        return datumLetzterStart;
    }
    /**
     * @param datumLetzterStart the datumLetzterStart to set
     */
    public void setDatumLetzterStart(Timestamp datumLetzterStart) {
        this.datumLetzterStart = datumLetzterStart;
    }
    /**
     * @return the datumLetzterAbbruch
     */
    public Date getDatumLetzterAbbruch() {
        return datumLetzterAbbruch;
    }
    /**
     * @param datumLetzterAbbruch the datumLetzterAbbruch to set
     */
    public void setDatumLetzterAbbruch(Date datumLetzterAbbruch) {
        this.datumLetzterAbbruch = datumLetzterAbbruch;
    }
    /**
     * @return the datumLetzterErfolg
     */
    public Date getDatumLetzterErfolg() {
        return datumLetzterErfolg;
    }
    /**
     * @param datumLetzterErfolg the datumLetzterErfolg to set
     */
    public void setDatumLetzterErfolg(Date datumLetzterErfolg) {
        this.datumLetzterErfolg = datumLetzterErfolg;
    }

    
}
