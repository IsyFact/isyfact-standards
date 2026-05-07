package de.bund.bva.isyfact.batchrahmen.batch;

import java.util.Date;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;

/**
 * Test batch that adds a FEHLER-type message to the protocol and finishes normally.
 * Used to verify that {@code bestimmeReturnCode} returns {@code FEHLER_AUSGEFUEHRT}
 * when the protocol contains error messages, even without an explicit return code being set.
 */
public class FehlerNachrichtTestBatch implements BatchAusfuehrungsBean {

    private BatchErgebnisProtokoll protokoll;

    @Override
    public void batchBeendet() {
        // nothing to do
    }

    @Override
    public void checkpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        // nothing to do
    }

    @Override
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
                              BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException {
        this.protokoll = protokoll;
        return 1;
    }

    @Override
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        this.protokoll.ergaenzeMeldung(new VerarbeitungsMeldung("FERR001", MeldungTyp.FEHLER, "Testfehler im Batch."));
        return new VerarbeitungsErgebnis(null, true);
    }

    @Override
    public void rollbackDurchgefuehrt() {
        // nothing to do
    }

    @Override
    public void vorCheckpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException {
        // nothing to do
    }

    @Override
    public void vorRollbackDurchgefuehrt() {
        // nothing to do
    }
}
