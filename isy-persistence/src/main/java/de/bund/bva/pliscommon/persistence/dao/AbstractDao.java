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
package de.bund.bva.pliscommon.persistence.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

/**
 * Abstrakte Basisklasse für Data Access Objects (DAOs).
 *
 * @param <T>
 *            die Entitätsklasse
 * @param <ID>
 *            die Primärschlüsselklasse
 *
 */
@Repository
public abstract class AbstractDao<T, ID extends Serializable> implements Dao<T, ID> {

    /** Logger. */
    protected final IsyLogger log = IsyLoggerFactory.getLogger(getClass());

    /** Der EntityManager. */
    private EntityManager entityManager;

    /** Die persistente Klasse. */
    private final Class<T> persistentClass;

    /**
     * Konstruktor.
     */
    @SuppressWarnings("unchecked")
    protected AbstractDao() {
        Type type = getClass();
        Class<T> persClass = null;

        do {
            if (type instanceof ParameterizedType) {
                persClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        } while (persClass == null);

        this.persistentClass = persClass;
    }

    /**
     * Setzt den EntityManager.
     *
     * @param entityManager
     *            der EntityManager
     */
    @Required
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Liefert den EntityManager.
     *
     * @return der EntityManager
     */
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * Liefert die persistente Klasse.
     *
     * @return die persistente Klasse
     */
    protected Class<T> getPersistentClass() {
        return this.persistentClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void speichere(T entitaet) {
        getEntityManager().persist(entitaet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loesche(T entitaet) {
        getEntityManager().remove(entitaet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T sucheMitId(ID id) {
        return getEntityManager().find(this.persistentClass, id);
    }

    /**
     * Liefert das eine, optionale Ergebnis einer Query.
     *
     * @param <X>
     *            der Ergebnistyp
     * @param query
     *            die Query
     * @return das eine Ergebnis oder <code>null</code>
     */
    protected <X> X getSingleOptionalResult(TypedQuery<X> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
