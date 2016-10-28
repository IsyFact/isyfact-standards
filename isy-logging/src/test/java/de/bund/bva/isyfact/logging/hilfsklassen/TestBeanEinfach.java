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

import java.util.Arrays;
import java.util.List;

/**
 * Test-Bean für die Serialisierungstests mit einfachen Attributen.
 */
public class TestBeanEinfach {

    /** Referenz auf irgend ein Objekt in de/bund/bva. */
    private TestBeanKomplex inneresBean;

    /** Referenz auf einen String. */
    private String einString;

    /** Referenz auf eine Liste. */
    private List<TestBeanKomplex> eineListe;

    /**
     * Konstruktor der Klasse.
     */
    public TestBeanEinfach() {
        this.inneresBean = new TestBeanKomplex(false);
        this.einString = "einString";
        this.eineListe = Arrays.asList(new TestBeanKomplex(false), new TestBeanKomplex(false));
    }

    /**
     * Liefert den Wert des Attributs 'inneresBean'.
     * 
     * @return Wert des Attributs.
     */
    public TestBeanKomplex getInneresBean() {
        return inneresBean;
    }

    /**
     * Liefert den Wert des Attributs 'einString'.
     * 
     * @return Wert des Attributs.
     */
    public String getEinString() {
        return einString;
    }

    /**
     * Liefert den Wert des Attributs 'eineListe'.
     * 
     * @return Wert des Attributs.
     */
    public List<TestBeanKomplex> getEineListe() {
        return eineListe;
    }

    /**
     * Setzt den Wert des Attributs 'inneresBean'.
     *
     * @param inneresBean Neuer Wert des Attributs.
     */
    public void setInneresBean(TestBeanKomplex inneresBean) {
        this.inneresBean = inneresBean;
    }

    /**
     * Setzt den Wert des Attributs 'einString'.
     *
     * @param einString Neuer Wert des Attributs.
     */
    public void setEinString(String einString) {
        this.einString = einString;
    }

    /**
     * Setzt den Wert des Attributs 'eineListe'.
     *
     * @param eineListe Neuer Wert des Attributs.
     */
    public void setEineListe(List<TestBeanKomplex> eineListe) {
        this.eineListe = eineListe;
    }

}
