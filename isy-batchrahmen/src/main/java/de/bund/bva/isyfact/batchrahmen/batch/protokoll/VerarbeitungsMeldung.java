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
package de.bund.bva.isyfact.batchrahmen.batch.protokoll;

import java.io.Serializable;

/**
 * Speichert die Daten für eine Verarbeitungsmeldung.
 * 
 *
 */
public class VerarbeitungsMeldung implements Serializable {
    /**
     * Die UID.
     */
    private static final long serialVersionUID = -8443300338938265834L;

    /**
     * Id der Meldung.
     */
    private String id;
    
    /**
     * Fachliche Id der Meldung.
     */
    private String fachlicheId;    

    /**
     * Typ der Meldung.
     */
    private MeldungTyp typ;

    /**
     * Meldungstext.
     */
    private String text;
    
    /**
     * Erzeugt eine Meldung.
     * @param id Id
     * @param typ Typ
     * @param text Langtext
     */
    public VerarbeitungsMeldung(String id, MeldungTyp typ, String text) {
        this.id = id;
        this.typ = typ;
        this.text = text;
    }
    
    /**
     * Erzeugt eine Meldung mit fachlicher Id.
     * @param id Id
     * @param fachlicheId Die fachliche ID
     * @param typ Typ
     * @param text Langtext
     */
    public VerarbeitungsMeldung(String id, String fachlicheId, MeldungTyp typ, String text) {
        this.id = id;
        this.fachlicheId = fachlicheId;
        this.typ = typ;
        this.text = text;
    }    

    /**
     * Liefert die Id der Meldung.
     * @return die Id
     */
    public String getId() {
        return id;
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
     * Liefert den Typ der Meldung.
     * @return der Typ
     */
    public MeldungTyp getTyp() {
        return typ;
    }

    /**
     * Liefert den eigentlichen Text der Meldung.
     * @return den Text.
     */    
    public String getText() {
        return text;
    }
}
