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
package de.bund.bva.isyfact.batchrahmen.batch.rahmen;

/**
 * Das Ergebnis der Verarbeitung eines Satzes durch eine
 * {@link BatchAusfuehrungsBean} Bean.
 * 
 *
 */
public class VerarbeitungsErgebnis {
    
    /** Der verarbeitete Datenbankschluessel fuer das Wiederaufsetzen. */
    private String datenbankSchluessel;

    /** Fachliche ID des verarbeiteten Satzes. */
    private String fachlicheId;
    
    /** Falls wahr, wurden alle Sätze bearbeitet und der Batch beendet. */    
    private boolean alleSaetzeVerarbeitet;
    
    /**
     * setzt alle benoetigten Daten.
     * @param schluessel              der Datenbankschluessel
     * @param alleSaetzeVerarbeitet   wahr, falls alle Saetze verarbeitet werden.
     */
    public VerarbeitungsErgebnis(String schluessel, boolean alleSaetzeVerarbeitet) {
        datenbankSchluessel = schluessel;
        this.alleSaetzeVerarbeitet = alleSaetzeVerarbeitet;
    }
    /**
     * @return the datenbankSchluessel
     */
    public String getDatenbankSchluessel() {
        return datenbankSchluessel;
    }
    /**
     * @param datenbankSchluessel the datenbankSchluessel to set
     */
    public void setDatenbankSchluessel(String datenbankSchluessel) {
        this.datenbankSchluessel = datenbankSchluessel;
    }
    /**
     * Liefert das Feld 'fachlicheId' zurück.
     * @return Wert von fachlicheId
     */
    public String getFachlicheId() {
        return fachlicheId;
    }
    /**
     * Setzt das Feld 'fachlicheId'.
     * @param fachlicheId Neuer Wert für fachlicheId
     */
    public void setFachlicheId(String fachlicheId) {
        this.fachlicheId = fachlicheId;
    }
    /**
     * @return the alleSaetzeVerarbeitet
     */
    public boolean isAlleSaetzeVerarbeitet() {
        return alleSaetzeVerarbeitet;
    }
    /**
     * @param alleSaetzeVerarbeitet the alleSaetzeVerarbeitet to set
     */
    public void setAlleSaetzeVerarbeitet(boolean alleSaetzeVerarbeitet) {
        this.alleSaetzeVerarbeitet = alleSaetzeVerarbeitet;
    }
}
