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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.Benutzerkonfiguration;

public class TestAbstractBenutzerKonfiguration {

    private static final String PARAMETER = "param";

    @Test
    public void testSetzeWertInteger() {
        Benutzerkonfiguration bk = new TestBenutzerKonfiguration();

        int wert = 14;
        bk.setValue(PARAMETER, wert);
        assertEquals(wert, bk.getAsInteger(PARAMETER));
    }

    @Test
    public void testSetzeWertLong() {
        Benutzerkonfiguration bk = new TestBenutzerKonfiguration();

        long wert = 1l;
        bk.setValue(PARAMETER, wert);
        assertEquals(wert, bk.getAsLong(PARAMETER));
    }

    @Test
    public void testSetzeWertDouble() {
        Benutzerkonfiguration bk = new TestBenutzerKonfiguration();

        double wert = 1.0;
        bk.setValue(PARAMETER, wert);
        assertEquals(wert, bk.getAsDouble(PARAMETER), 0.0);
    }

    @Test
    public void testSetzeWertBoolean() {
        Benutzerkonfiguration bk = new TestBenutzerKonfiguration();

        boolean wert = true;
        bk.setValue(PARAMETER, wert);
        assertEquals(wert, bk.getAsBoolean(PARAMETER));
    }

    class TestBenutzerKonfiguration extends AbstractBenutzerkonfiguration {

        private Map<String, String> konfiguration = new HashMap<>();

        @Override
        public Set<String> getSchluessel() {
            return this.konfiguration.keySet();
        }

        @Override
        protected boolean containsKey(String schluessel) {
            return this.konfiguration.containsKey(schluessel);
        }

        @Override
        protected String getValue(String schluessel) {
            return this.konfiguration.get(schluessel);
        }

        @Override
        public void setValue(String schluessel, String wert) {
            this.konfiguration.put(schluessel, wert);
        }

        @Override
        public boolean removeValue(String schluessel) {
            return this.konfiguration.remove(schluessel) != null;
        }

    }

}
