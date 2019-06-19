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
