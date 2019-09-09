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
package de.bund.bva.isyfact.batchrahmen.core.protokoll;

import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;

/**
 * Definiert ein Interface um Protokolle zu schreiben oder zu versenden.
 * 
 *
 */
public interface ProtokollGenerator {

    /**
     * Erzeugt das StartInfo-Element aus BatchID, Datum, Uhrzeit und Parameter.
     * @param protokoll
     *            Das BatchProtokoll
     */
    public void erzeugeStartInfoElement(BatchErgebnisProtokoll protokoll);

    /**
     * Erzeugt das Ende-Element aus Datum und Uhrzeit.
     * @param protokoll
     *            Das BatchProtokoll
     */
    public void erzeugeEndeInfoElement(BatchErgebnisProtokoll protokoll);

    /**
     * Erzeugt eine Meldung.
     * @param meldung
     *            Die übergebene Meldung
     */
    public void erzeugeMeldung(VerarbeitungsMeldung meldung);

    /**
     * Erzeugt die abschließende Statistik-Information.
     * @param protokoll
     *            Das BatchProtokoll
     */
    public void erzeugeStatistik(BatchErgebnisProtokoll protokoll);

    /**
     * Erzeugt die Return-Code Informationen.
     * @param protokoll
     *            Das BatchProtokoll
     */
    public void erzeugeReturnCodeElement(BatchErgebnisProtokoll protokoll);

    /**
     * Beendet des Generierens. Schließen der Connection oder Handles.
     */
    public void close();

    /**
     * Output flushen.
     */
    public void flusheOutput();
}
