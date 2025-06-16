package de.bund.bva.isyfact.persistence.usertype;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.usertype.UserType;

/**
 * Abstract {@link UserType} for any immutable attributes.
 */
public abstract class AbstractImmutableUserType implements UserType {

    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    public int hashCode(Object x) {
        return Objects.hashCode(x);
    }

    public Object deepCopy(Object value) {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return original;
    }

}
