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
package de.bund.bva.pliscommon.batchrahmen.batch.protokoll;

/**
 * Enthält die Daten für einen Statistik-Eintrag.
 * 
 *
 */
public class StatistikEintrag implements Comparable<StatistikEintrag> {
    /**
     * Id der Statistik.
     */
    private String id;

    /**
     * Langtext der Statistik.
     */
    private String text;

    /**
     * Wert der Statistik.
     */
    private int wert;
    
    /**
     * Legt die Reihenfolge für die Ausgabe fest.
     */
    private int reihenfolge;

    /**
     * Erzeugt Statistikeintrag mit Id und Text.
     * @param id Id zur Identifikation des Eintrags 
     * {@link DefaultBatchErgebnisProtokoll#getStatistikEintrag(String)}
     * @param text Langtext der Statistik für das Protokoll.
     */
    public StatistikEintrag(String id, String text) {
        this.id = id;
        this.text = text;
    }
    
    /**
     * Aktueller Wert dieser Statistik.
     * @return aktuellen Wert
     */
    public int getWert() {
        return wert;
    }
    
    /**
     * Setzt den Wert dieser Statistik.
     * @param neuerWert Neuer Wert.
     */
    public void setWert(int neuerWert) {
        wert = neuerWert;
    }
    
    /**
     * Erhöht den Wert der Statistik um eins.
     */
    public void erhoeheWert() {
        wert++;
    }

    /**
     * Liefert Id dieser Statistik.
     * @return Id dieser Statistik.
     */
    public String getId() {
        return id;
    }

    /**
     * Liefert den Text dieser Statistik.
     * @return Text fï¿½rs Protokoll.
     */
    public String getText() {
        return text;
    }

    /**
     * Liefert das Feld 'reihenfolge' zurück.
     * @return Wert von reihenfolge
     */
    public int getReihenfolge() {
        return reihenfolge;
    }

    /**
     * Setzt das Feld 'reihenfolge'.
     * @param reihenfolge Neuer Wert für reihenfolge
     */
    public void setReihenfolge(int reihenfolge) {
        this.reihenfolge = reihenfolge;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public int compareTo(StatistikEintrag o) {
        if (reihenfolge > o.reihenfolge) {
            return 1;
        } else if (reihenfolge < o.reihenfolge) {
            return -1;
        }
        return 0;
    }
}
