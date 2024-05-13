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

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenInitialisierungException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

/**
 * The BatchStatusDAO does not comply with the specifications for Hibernate as it operates in a sub-Spring context without
 * its own SessionFactory. Therefore, static methods and static reading of the Hibernate template are avoided.
 *
 */
public class BatchStatusDao {
    /**
     * The EntityManager.
     */
    private final EntityManagerProvider entityManagerProvider;

    /**
     * sets the EntityManager in the DAO.
     *
     * @param entityManagerProvider
     *            the EntityManagerProvider
     */
    public BatchStatusDao(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    /**
     * reads a record based on a key.
     *
     * @param batchId
     *            the ID of the batch.
     * @return the read batch record.
     */
    public BatchStatus leseBatchStatus(String batchId) {
        if (batchId == null || batchId.trim().isEmpty()) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT,
                    "Batch ID darf nicht null oder leer sein.");
        }
        EntityManager entityManager = entityManagerProvider.getTransactionalEntityManager();
        if (entityManager == null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT,
                    "Transactional EntityManager ist nicht verfügbar für Batch ID: " + batchId);
        }
        return entityManager.find(BatchStatus.class, batchId, LockModeType.PESSIMISTIC_WRITE);
    }


    /**
     * persists the given batch record.
     *
     * @param status
     *            the new record.
     */
    public void createBatchStatus(BatchStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status darf nicht null sein");
        }
        if (leseBatchStatus(status.getBatchId()) != null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_BATCH_IN_DB,
                    status.getBatchId());
        }
        EntityManager entityManager = entityManagerProvider.getTransactionalEntityManager();
        if (entityManager == null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_KONF_PARAMETER_FEHLT,
                    "Transactional EntityManager ist nicht verfügbar.");
        }
        entityManager.persist(status);
    }

}
