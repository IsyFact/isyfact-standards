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
package de.bund.bva.isyfact.persistence.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Abstract {@link UserType} for any immutable attributes that are persisted as a string in a VARCHAR column.
 */
public abstract class AbstractImmutableStringUserType extends AbstractImmutableUserType {


    /**
     * SQL-Typ der DB-Spalte.
     */
    private static final int SQL_TYPE = Types.VARCHAR;

    @Override
    public int getSqlType() {
        return SQL_TYPE;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (rs.wasNull()) {
            return null;
        }
        return convertStringToInstance(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, SQL_TYPE);
        } else {
            returnedClass().cast(value);
            st.setString(index, convertInstanceToString(value));
        }
    }

    /**
     * Converts the string representation from the database into the attribute value.
     *
     * @param value the persistent string representation
     * @return the attribute value
     */
    protected abstract Object convertStringToInstance(String value);

    /**
     * Converts an attribute value into the string representation to be persisted.
     *
     * @param value the attribute value
     * @return the string representation to be persisted
     */
    protected abstract String convertInstanceToString(Object value);
}
