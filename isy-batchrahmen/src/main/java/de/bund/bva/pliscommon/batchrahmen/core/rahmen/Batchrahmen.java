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
package de.bund.bva.pliscommon.batchrahmen.core.rahmen;

import de.bund.bva.pliscommon.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;

/**
 * Das Interface der Batchrahmen-Bean. Diese wird von der
 * {@link de.bund.bva.pliscommon.batchrahmen.core.launcher.BatchLauncher} Klasse
 * aufgerufen.
 * <p>
 * Die Bean selbst (meist Klasse
 * {@link de.bund.bva.pliscommon.batchrahmen.core.rahmen.impl.BatchrahmenImpl})
 * verwaltet die Batch-Statustabelle und handled die Transaktionen.
 * 
 * Sie ruft fuer die Verarbeitung eine {@link BatchAusfuehrungsBean} Bean auf.
 * 
 * 
 */
public interface Batchrahmen {

    /**
     * startet den Batch. Mit der Konfiguration werden die Kommandozeilen-
     * Parameter sowie der Inhalt der Property-Datei ï¿½bergeben.
     * 
     * @param konfiguration
     *            die Konfiguration des Batchrahmens.
     * @param protokoll
     *            das Protokoll fuer die Batchausführung.
     * @throws BatchAusfuehrungsException
     *             Wenn ein Fehler wï¿½hrend der Batchausfï¿½hrung autritt.
     * 
     */
    public void runBatch(BatchKonfiguration konfiguration, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException;
}
