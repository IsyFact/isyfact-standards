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
package de.bund.bva.pliscommon.konfiguration.common.impl;

import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;

/**
 * Abstrakte Basisklasse für {@link Konfiguration Konfigurations}-Implementierungen, die alle
 * Typkonvertierungen erledigt, das Ermitteln eines Konfigurationswerts aus einem Konfigurationsspeicher jedoch
 * an eine abstrakte Methode delegiert.
 * 
 */
public abstract class AbstractKonfiguration implements Konfiguration {

    /**
     * Prüft, ob zum gegebenen Konfigurationparameter ein Konfigurationswert hinterlegt ist.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @return <code>true</code>, wenn zum gegebenen Konfigurationparameter ein Konfigurationswert hinterlegt
     *         ist.
     */
    protected abstract boolean containsKey(String schluessel);

    /**
     * Liest den Konfigurationswert zum gegebenen Konfigurationsparameter.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @return der Konfigurationswert, oder <code>null</code>, falls keiner gefunden wurde.
     */
    protected abstract String getValue(String schluessel);

    /**
     * {@inheritDoc}
     */
    public String getAsRawString(String schluessel, String defaultWert) {
        if (!containsKey(schluessel)) {
            return defaultWert;
        }
        return getAsRawString(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public String getAsRawString(String schluessel) {
        if (!containsKey(schluessel)) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER, schluessel);
        }
        return getValue(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public String getAsString(String schluessel, String defaultWert) {
        if (!containsKey(schluessel)) {
            return defaultWert;
        }
        return getAsString(schluessel).trim();
    }

    /**
     * {@inheritDoc}
     */
    public String getAsString(String schluessel) {
        if (!containsKey(schluessel)) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER, schluessel);
        }
        return getValue(schluessel).trim();
    }

    /**
     * {@inheritDoc}
     */
    public int getAsInteger(String schluessel, int defaultWert) {
        if (!containsKey(schluessel)) {
            return defaultWert;
        }
        return getAsInteger(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public int getAsInteger(String schluessel) {
        if (!containsKey(schluessel)) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER, schluessel);
        }
        String propValue = getValue(schluessel).trim();
        try {
            return Integer.parseInt(propValue);
        } catch (NumberFormatException ex) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETERWERT_UNGUELTIG,
                new String[] { schluessel, propValue });
        }
    }

    /**
     * {@inheritDoc}
     */
    public double getAsDouble(String schluessel, double defaultWert) {
        if (!containsKey(schluessel)) {
            return defaultWert;
        }
        return getAsDouble(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public double getAsDouble(String schluessel) {
        if (!containsKey(schluessel)) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER, schluessel);
        }
        String propValue = getValue(schluessel).trim();
        try {
            return Double.parseDouble(propValue);
        } catch (NumberFormatException ex) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETERWERT_UNGUELTIG,
                new String[] { schluessel, propValue });
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getAsLong(String schluessel, long defaultWert) {
        if (!containsKey(schluessel)) {
            return defaultWert;
        }
        return getAsLong(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public long getAsLong(String schluessel) {
        if (!containsKey(schluessel)) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER, schluessel);
        }
        String propValue = getValue(schluessel).trim();
        try {
            return Long.parseLong(propValue);
        } catch (NumberFormatException ex) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETERWERT_UNGUELTIG,
                new String[] { schluessel, propValue });
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAsBoolean(String schluessel, boolean defaultWert) {
        if (!containsKey(schluessel)) {
            return defaultWert;
        }
        return getAsBoolean(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAsBoolean(String schluessel) {
        if (!containsKey(schluessel)) {
            throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER, schluessel);
        }
        String propValue = getValue(schluessel).trim();
        if (propValue.equalsIgnoreCase("true")) {
            return true;
        }
        if (propValue.equalsIgnoreCase("false")) {
            return false;
        }
        throw new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETERWERT_UNGUELTIG,
            new String[] { schluessel, propValue });
    }

}
