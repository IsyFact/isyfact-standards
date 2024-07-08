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
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

/**
 * Example of a batch that is only terminated by a "kill -s INT pid" ordered.
 * Under Windows the batch can be terminated with taskkill.
 * The batch is implemented so that it shuts down in order after the signal "interrupt".
 */
public class InfiniteTestBatch implements BatchAusfuehrungsBean {
    private BatchErgebnisProtokoll protokoll;

    /**
     * The logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(InfiniteTestBatch.class);

    public void batchBeendet() {
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001, "Batch beendet aufgerufen!");
    }

    public void checkpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Checkpoint für Satz {} geschrieben.", satzNummer);
    }

    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
                              BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException {
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Intialisierung aufgerufen!");
        this.protokoll = protokoll;
        return -1;
    }

    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        try {
            LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                    "Batch-Schritt ausgeführt!");
            this.protokoll.ergaenzeMeldung(new VerarbeitungsMeldung("VERARB", MeldungTyp.INFO,
                    "Verarbeite Satz."));
            Thread.sleep(1000);
            return new VerarbeitungsErgebnis(null, false);
        } catch (InterruptedException e) {
            return new VerarbeitungsErgebnis(null, true);
        }
    }

    public void rollbackDurchgefuehrt() {
        LOG.error(BatchRahmenEreignisSchluessel.EPLBAT00001, "Rollback durchgeführt");
    }

    @Override
    public void vorCheckpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        // blank
    }

    @Override
    public void vorRollbackDurchgefuehrt() {
        // blank
    }

}
