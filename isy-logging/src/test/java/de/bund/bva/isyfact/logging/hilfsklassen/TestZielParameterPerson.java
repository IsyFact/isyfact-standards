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
 * Hilfsklasse als Testparameter einer Methodinvocation.
 * 
 */
public class TestZielParameterPerson {

    /** Die Vornamen des Paramaters. */
    private String[] vornamen;

    /** Der Nachname des Parameters. */
    private String name;

    /**
     * Konstruktor der Klasse. Initialisiert die übergebenen Parameter.
     * 
     * @param vornamen die Vornamen.
     * @param name der Name.
     */
    public TestZielParameterPerson(String name, String... vornamen) {
        super();
        this.vornamen = vornamen;
        this.name = name;
    }

    /**
     * Liefert den Wert des Attributs 'vornamen'.
     * 
     * @return Wert des Attributs.
     */
    public String[] getVornamen() {
        return vornamen;
    }

    /**
     * Setzt den Wert des Attributs 'vornamen'.
     * 
     * @param vornamen
     *            Neuer Wert des Attributs.
     */
    public void setVornamen(String[] vornamen) {
        this.vornamen = vornamen;
    }

    /**
     * Liefert den Wert des Attributs 'name'.
     * 
     * @return Wert des Attributs.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Wert des Attributs 'name'.
     * 
     * @param name
     *            Neuer Wert des Attributs.
     */
    public void setName(String name) {
        this.name = name;
    }

}
