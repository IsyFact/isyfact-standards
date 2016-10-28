package de.bund.bva.isyfact.logging.impl;

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
 * Auflistung aller Schlüssel (Namen) für Marker.
 * 
 */
public enum MarkerSchluessel {

    /** Rootmarker, zur Sammlung weitere Marker. */
    ROOTMARKER("rootmarker"),
    /** Kategorie des Logeintrags. */
    KATEGORIE("kategorie"),
    /** Schlüssel des Logeintrags. */
    SCHLUESSEL("schluessel"),
    /** Logeintrag enthält Fachdaten. */
    FACHDATEN("fachdaten"),
    /** Dauer des Logeintrags. */
    DAUER("dauer"),
    /** Methode eines geloggten Methodenaufruf. */
    METHODE("methode"),
    /** Parameter eines geloggten Methodenaufruf. */
    AUFRUFPARAMETER("aufrufparameter"),
    /** Ergebnis eines geloggten Methodenaufruf. */
    ERGEBNIS("ergebnis");

    /**
     * Der Wert des Markers.
     */
    private final String wert;

    /**
     * Konstruktor der Klasse.
     * 
     * @param wert
     *            die zum Schlüssel gehörende Wert.
     */
    private MarkerSchluessel(String wert) {
        this.wert = wert;
    }

    /**
     * Liefert den Wert des Attributs 'wert'.
     * 
     * @return Wert des Attributs.
     */
    public String getWert() {
        return wert;
    }
}
