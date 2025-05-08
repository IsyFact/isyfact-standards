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
