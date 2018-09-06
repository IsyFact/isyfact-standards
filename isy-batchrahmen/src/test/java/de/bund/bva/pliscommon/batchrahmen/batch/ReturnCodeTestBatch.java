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
package de.bund.bva.pliscommon.batchrahmen.batch;

import java.util.Date;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.StatistikEintrag;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.AuthenticationCredentials;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;

/**
 * Dieser Batch setzt aber im Gegensatz zum Basic Test Batch den Return Code im Batchprotokoll auf einen Wert
 * ungleich 0 um. Mit diesem Batch soll getestet werden, dass der Batchrahmen einen gesetzten ReturnCode nicht
 * mehr überschreibt.
 *
 */
public class ReturnCodeTestBatch implements BatchAusfuehrungsBean {

    /** Die Anzahl der Sätze, die geschrieben werden sollen. */
    public static final int MAX_SAETZE = 10000;

    /** Das Ergebnisprotokoll */
    private BatchErgebnisProtokoll protokoll;

    /** Der Logger. */
    public static final IsyLogger LOG = IsyLoggerFactory.getLogger(ReturnCodeTestBatch.class);

    /** StatistikEintraege fuer die Sollwertkontrolle */
    private StatistikEintrag statistik1;

    private StatistikEintrag statistik2;

    /** Der interne Zaehler */
    private int count;

    /** {@inheritDoc} */
    public void batchBeendet() {
        this.statistik2.setWert(999);
    }

    /** {@inheritDoc} */
    public void checkpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
            "Checkpoint für Satz {} geschrieben.", satzNummer);
    }

    /** {@inheritDoc} */
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
        BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
        throws BatchAusfuehrungsException {
        this.count = MAX_SAETZE;
        this.protokoll = protokoll;

        this.statistik1 = new StatistikEintrag("COUNT", "Zähler");
        this.statistik2 = new StatistikEintrag("COUNT2", "Zähler 2");

        protokoll.registriereStatistikEintrag(this.statistik1);
        protokoll.registriereStatistikEintrag(this.statistik2);

        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001, "Start-Typ ist: {}",
            startTyp);

        return this.count;
    }

    /** {@inheritDoc} */
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        this.count--;
        this.protokoll.ergaenzeMeldung(new VerarbeitungsMeldung("ID" + this.count, "REGNR" + this.count,
            MeldungTyp.INFO, "Satz " + this.count + " verarbeitet. Teststring: <>\"üaößÜÄÖ€"));
        this.statistik1.erhoeheWert();
        this.protokoll.setReturnCode(BatchReturnCode.FEHLER_AUSGEFUEHRT);
        return new VerarbeitungsErgebnis("" + this.count, this.count == 0);
    }

    /** {@inheritDoc} */
    public void rollbackDurchgefuehrt() {
        LOG.error(BatchRahmenEreignisSchluessel.EPLBAT00001, "Rollback durchgeführt");
    }

    /**
     * Dieser Batch verwendet keine Sicherung. {@inheritDoc}
     */
    public AuthenticationCredentials getAuthenticationCredentials(BatchKonfiguration konfiguration) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vorCheckpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vorRollbackDurchgefuehrt() {
        // TODO Auto-generated method stub

    }

}
