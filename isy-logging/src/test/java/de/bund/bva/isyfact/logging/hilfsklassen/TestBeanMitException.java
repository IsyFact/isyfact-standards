package de.bund.bva.isyfact.logging.hilfsklassen;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
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
 * #L%
 */

/**
 * Test-Bean, dass innerhalb eines Getters einen Fehler wirft.
 */
public class TestBeanMitException {
    
    /** Einfache Property bei "get" eine Exception erzeugt. */
    private String a;

    /**
     * Liefert den Wert des Attributs 'a'.
     * 
     * @return Wert des Attributs.
     */
    public String getA() {
        // Wirft Nullpointer
        return a.toString();
    }

    /**
     * Setzt den Wert des Attributs 'a'.
     *
     * @param a Neuer Wert des Attributs.
     */
    public void setA(String a) {
        this.a = a;
    }
    

}
