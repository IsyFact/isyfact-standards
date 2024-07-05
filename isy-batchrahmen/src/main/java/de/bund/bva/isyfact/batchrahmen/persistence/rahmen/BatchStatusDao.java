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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenInitialisierungException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;

/**
 * Das BatchStatusDAO entspricht nicht den Vorgaben fuer Hibernate, da es in einem Unter-Spring-Kontext ohne
 * eigene SessionFactory arbeitet. Auf statische Methoden und statisches Auslesen des Hibernate-Templates wird
 * deshalb verzichtet.
 *
 *
 */
public class BatchStatusDao {
    /**
     * Der EntityManager.
     */
    private final EntityManagerFactory factory;

    /**
     * setzt den EntityManager im DAO.
     *
     * @param entityManagerFactory
     *            die EntityManagerFactory
     */
    public BatchStatusDao(EntityManagerFactory entityManagerFactory) {
        this.factory = entityManagerFactory;
    }

    /**
     * liest einen Datensatz anhand eines Schluessels aus.
     *
     * @param batchId
     *            die ID des Batches.
     * @return der gelesene Batch-Datensatz.
     */
    public BatchStatus leseBatchStatus(String batchId) {
        EntityManager entityManager = getEntityManager();
        return entityManager.find(BatchStatus.class, batchId, LockModeType.PESSIMISTIC_WRITE);
    }

    /**
     * persistiert den gegebenen Batch-Datensatz.
     *
     * @param status
     *            der neue Datensatz.
     */
    public void createBatchStatus(BatchStatus status) {
        EntityManager entityManager = getEntityManager();
        if (leseBatchStatus(status.getBatchId()) != null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_BATCH_IN_DB, status.getBatchId());
        }
        entityManager.persist(status);
    }

    protected EntityManager getEntityManager() {
        EntityManager entityManager = EntityManagerFactoryUtils.getTransactionalEntityManager(this.factory);
        if (entityManager == null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_BATCH_INIT_DB);
        }
        return entityManager;
    }
}
