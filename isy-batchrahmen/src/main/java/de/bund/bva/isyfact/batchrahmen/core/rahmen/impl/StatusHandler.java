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
package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.sql.Timestamp;

import javax.persistence.EntityManagerFactory;

import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchStatus;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchStatusDao;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.DefaultEntityManagerProvider;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.EntityManagerProvider;

/**
 * This handler encapsulates the logic around the BatchStatus table.
 *
 */
public class StatusHandler {
    /** Reference to the DAO object for the status table. */
    private BatchStatusDao batchStatusDao;

    /** Batch ID, used to search for the status record. */
    private String batchId;

    /**
     * Sets the required cross-sectional data in the instance.
     *
     * @param factory
     *            {@link EntityManagerFactory} Current EntityManagerFactory for access to
     *            persistence.
     */
    public StatusHandler(EntityManagerFactory factory) {
        EntityManagerProvider entityManagerProvider = new DefaultEntityManagerProvider(factory);
        this.batchStatusDao = new BatchStatusDao(entityManagerProvider);
    }

    /**
     * performs the following actions:<br>
     * <ul>
     * <li>Read or create the status database record.
     * <li>Check if the start parameters harmonize with the database.
     * <li>Update the status database record (status running etc.)
     * <li>On start: Update the configuration parameters in the database
     * <li>On restart: Read the configuration parameters from the database and update the own
     * configuration.
     * </ul>
     * @param konfiguration
     *            The configuration of the batch call. This is updated to the configuration stored in the status database on restart.
     * @return The BatchStatus record for the batch.
     */
    public BatchStatus statusSatzInitialisieren(BatchKonfiguration konfiguration) {
        this.batchId = konfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_ID);
        String name = konfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_NAME);
        // Read the Batch Status, possibly create the record
        BatchStatus status = this.batchStatusDao.leseBatchStatus(this.batchId);
        if (status == null) {
            status = new BatchStatus();
            status.setBatchId(this.batchId);
            status.setBatchName(name);
            status.setBatchStatus(BatchStatusTyp.NEU.getName());
            this.batchStatusDao.createBatchStatus(status);
        }
        pruefeStatusDbGegenAufrufParameter(status, konfiguration);
        return status;
    }

    /**
     * Records the batch as running in the BatchStatus.
     * @param konfiguration
     *            The BatchConfiguration
     */
    public void setzteStatusSatzAufLaufend(BatchKonfiguration konfiguration) {
        // Update the batch status.
        BatchStatus status = leseBatchStatus();
        status.setBatchStatus(BatchStatusTyp.LAEUFT.getName());
        status.setDatumLetzterStart(new Timestamp(System.currentTimeMillis()));
        if (BatchStartTyp.START.equals(konfiguration.getStartTyp())) {
            status.setSatzNummerLetztesCommit(0);
            status.setSchluesselLetztesCommit(null);
        }
    }

    /**
     * Reads the status record for the batch.
     * @return Status record of the batch or <code>null</code> if none exists.
     */
    public BatchStatus leseBatchStatus() {
        // Read the batch status, possibly create the record
        return this.batchStatusDao.leseBatchStatus(this.batchId);
    }

    /**
     * Checks whether the parameters for starting or restarting the batch match the status in the database.
     *
     * @param status
     *            the status in the database
     * @param konfig
     *            the specified parameters.
     */
    private void pruefeStatusDbGegenAufrufParameter(BatchStatus status, BatchKonfiguration konfig) {
        if (BatchStatusTyp.LAEUFT.getName().equals(status.getBatchStatus())
            && !konfig.getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF)) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_BATCH_AKTIV,
                KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF);
        }
        if (BatchStatusTyp.ABGEBROCHEN.getName().equals(status.getBatchStatus())
            && status.getSatzNummerLetztesCommit() >= 0 && konfig.getStartTyp() == BatchStartTyp.START
            && !konfig.getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART)) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_IGNORIERE_RESTART,
                KonfigurationSchluessel.KOMMANDO_PARAM_RESTART,
                KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART);
        }
    }

}
