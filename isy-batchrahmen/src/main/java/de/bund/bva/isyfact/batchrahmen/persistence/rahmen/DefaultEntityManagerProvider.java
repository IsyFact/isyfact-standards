package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

public class DefaultEntityManagerProvider implements EntityManagerProvider {
    private final EntityManagerFactory factory;

    public DefaultEntityManagerProvider(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public EntityManager getTransactionalEntityManager() {
        return EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
    }
}