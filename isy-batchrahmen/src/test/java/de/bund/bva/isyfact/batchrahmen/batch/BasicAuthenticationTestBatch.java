package de.bund.bva.isyfact.batchrahmen.batch;

import java.util.Date;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.StatistikEintrag;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

/**
 * Absolute base batch for the {@link de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen}.
 * This batch does nothing except increment an internal counter and update the BatchStatus table.
 */
public class BasicAuthenticationTestBatch implements BatchAusfuehrungsBean {

    /**
     * The number of blocks to be written.
     */
    public static final int MAX_SAETZE = 10;

    /**
     * The {@link BatchErgebnisProtokoll}.
     */
    private BatchErgebnisProtokoll protokoll;

    /**
     * The logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(BasicAuthenticationTestBatch.class);

    /**
     * The {@link StatistikEintrag} for control.
     */
    private StatistikEintrag statistik1;

    /**
     * The {@link StatistikEintrag} for control.
     */
    private StatistikEintrag statistik2;

    /**
     * The internal counter.
     */
    private int count;

    public void batchBeendet() {
        this.statistik2.setWert(999);
    }

    public void checkpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Checkpoint für Satz {} geschrieben.", satzNummer);
    }

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

    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        this.count--;
        this.protokoll.ergaenzeMeldung(new VerarbeitungsMeldung("ID" + this.count, "REGNR" + this.count,
                MeldungTyp.INFO, "Satz " + this.count + " verarbeitet. Teststring: <>\"üaößÜÄÖ€"));
        this.statistik1.erhoeheWert();
        return new VerarbeitungsErgebnis(String.valueOf(this.count), this.count == 0);
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
