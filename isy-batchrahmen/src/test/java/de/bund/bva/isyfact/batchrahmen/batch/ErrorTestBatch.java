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
package de.bund.bva.isyfact.batchrahmen.batch;

import java.util.Date;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.AuthenticationCredentials;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;

public class ErrorTestBatch implements BatchAusfuehrungsBean {
    private int count = 500;

    private BatchKonfiguration konfiguration;

    @Override
    public void batchBeendet() {
    }

    @Override
    public void checkpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
    }

    @Override
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
        BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
        throws BatchAusfuehrungsException {
        this.konfiguration = konfiguration;
        if (konfiguration.getAsBoolean("initError", false)) {
            throw new RuntimeException("Abbruch in Init");
        }
        return this.count;
    }

    @Override
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        // Mit dem Flag 'laufErrorSofort' kann ein Abbruch des Batches noch vor der Verarbeitung des ersten
        // Satzes simuliert werden.
        if (this.konfiguration.getAsBoolean("laufErrorSofort", false)) {
            throw new RuntimeException("Sofortiger Abbruch in verarbeite Satz");
        }

        // Alternativ kann mit dem Flag 'laufError' ebenfalls ein Abbruch simuliert werden. Allerdings werden
        // hier bereits Sätze verarbeitet, bevor es zu dem Abbruch kommt.
        this.count--;
        if (this.count < 10 && this.konfiguration.getAsBoolean("laufError", false)) {
            throw new RuntimeException("Abbruch in verarbeite Satz");
        }
        return new VerarbeitungsErgebnis("" + this.count, this.count == 0);
    }

    @Override
    public void rollbackDurchgefuehrt() {
    }

    /**
     * Dieser Batch verwendet keine Sicherung. {@inheritDoc}
     */
    @Override
    public AuthenticationCredentials getAuthenticationCredentials(BatchKonfiguration konfiguration) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vorCheckpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        // leer
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vorRollbackDurchgefuehrt() {
        // leer
    }

}
