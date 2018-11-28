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
package de.bund.bva.pliscommon.persistence.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Abstrakter {@link UserType} für beliebige Immutable-Attribute, die als String in eine VARCHAR-Spalte
 * persistiert werden.
 *
 */
public abstract class AbstractImmutableStringUserType extends AbstractImmutableUserType {

    /** SQL-Typ der DB-Spalte. */
    private static final int SQL_TYPE = Types.VARCHAR;

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] sqlTypes() {
        return new int[] { SQL_TYPE };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sessionImplementor,
        Object owner) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (rs.wasNull()) {
            return null;
        }
        return convertStringToInstance(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
        SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, SQL_TYPE);
        } else {
            returnedClass().cast(value); // Typprüfung
            st.setString(index, convertInstanceToString(value));
        }
    }

    /**
     * Konvertiert die Stringdarstellung aus der Datenbank in den Attributwert.
     *
     * @param value
     *            die persistente Stringdarstellung
     * @return der Attributwert
     */
    protected abstract Object convertStringToInstance(String value);

    /**
     * Konvertiert einen Attributwert in die zu persistierende Stringdarstellung.
     *
     * @param value
     *            der Attributwert
     * @return die zu persistierende Stringdarstellung
     */
    protected abstract String convertInstanceToString(Object value);
}
