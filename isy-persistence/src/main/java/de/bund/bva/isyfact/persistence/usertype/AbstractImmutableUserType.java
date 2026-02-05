package de.bund.bva.isyfact.persistence.usertype;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.usertype.UserType;

/**
 * Abstract {@link UserType} for any immutable attributes.
 */
public abstract class AbstractImmutableUserType<T> implements UserType<T> {

    @Override
    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) {
        return Objects.hashCode(x);
    }

    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(T value) {
        return (Serializable) value;
    }

    @Override
    public T assemble(Serializable cached, Object owner) {
        return (T) cached;
    }

    @Override
    public T replace(T original, T target, Object owner) {
        return original;
    }

}
