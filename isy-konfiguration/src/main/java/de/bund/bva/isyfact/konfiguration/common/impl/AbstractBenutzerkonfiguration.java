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
package de.bund.bva.isyfact.konfiguration.common.impl;

import de.bund.bva.isyfact.konfiguration.common.Benutzerkonfiguration;

/**
 * Abstrakte Basisklasse für {@link Benutzerkonfiguration Benutzerkonfigurations}-Implementierungen, die alle
 * Typkonvertierungen erledigt, das Setzen von Konfigurationswerten aus einem Konfigurationsspeicher jedoch an
 * eine abstrakte Methode delegiert.
 * 
 */
public abstract class AbstractBenutzerkonfiguration extends AbstractKonfiguration implements
    Benutzerkonfiguration {

    /**
     * {@inheritDoc}
     */
    protected abstract boolean containsKey(String schluessel);

    /**
     * {@inheritDoc}
     */
    protected abstract String getValue(String schluessel);

    /**
     * {@inheritDoc}
     */
    public abstract void setValue(String schluessel, String wert);

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, int wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, long wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, double wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, boolean wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public abstract boolean removeValue(String schluessel);

}
