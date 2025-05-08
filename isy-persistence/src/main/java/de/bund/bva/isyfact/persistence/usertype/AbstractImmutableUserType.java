package de.bund.bva.isyfact.persistence.usertype;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.usertype.UserType;

/**
 * Abstrakter {@link UserType} für beliebige Immutable-Attribute.
 * 
 */
public abstract class AbstractImmutableUserType implements UserType {

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode(Object x) {
        return Objects.hashCode(x);
    }

    /**
     * {@inheritDoc}
     */
    public Object deepCopy(Object value) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable disassemble(Object value) {
        return (Serializable) value;
    }

    /**
     * {@inheritDoc}
     */
    public Object assemble(Serializable cached, Object owner) {
        return cached;
    }

    /**
     * {@inheritDoc}
     */
    public Object replace(Object original, Object target, Object owner) {
        return original;
    }

}
