package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {
    EntityManager getTransactionalEntityManager();
}
