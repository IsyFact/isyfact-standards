package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class BatchRahmenDaoMock extends BatchStatusDao{

    private EntityManager entityManager;
    /**
     * setzt den EntityManager im DAO.
     *
     * @param entityManagerFactory die EntityManagerFactory
     */
    public BatchRahmenDaoMock(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
        super(entityManagerFactory);
        this.entityManager = entityManager;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
