package de.bund.bva.isyfact.persistence.usertype;

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
