package de.bund.bva.isyfact.batchrahmen.batch;

import java.util.Date;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
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
        // The flag 'laufErrorSofort' can be used to simulate an abort of the batch even before the first record is processed.
        if (this.konfiguration.getAsBoolean("laufErrorSofort", false)) {
            throw new RuntimeException("Sofortiger Abbruch in verarbeite Satz");
        }

        // Alternatively, the flag 'laufError' can also be used to simulate an abort.
        // However, records are already processed here before the abort occurs.
        this.count--;
        if (this.count < 10 && this.konfiguration.getAsBoolean("laufError", false)) {
            throw new RuntimeException("Abbruch in verarbeite Satz");
        }
        return new VerarbeitungsErgebnis(String.valueOf(this.count), this.count == 0);
    }

    @Override
    public void rollbackDurchgefuehrt() {
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
