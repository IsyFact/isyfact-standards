package de.bund.bva.isyfact.batchrahmen.core.rahmen;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchAusfuehrungsBean;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.impl.BatchrahmenImpl;

/**
 * Das Interface der Batchrahmen-Bean. Diese wird von der
 * {@link BatchLauncher} Klasse
 * aufgerufen.
 * <p>
 * Die Bean selbst (meist Klasse
 * {@link BatchrahmenImpl})
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
    void runBatch(BatchKonfiguration konfiguration, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException;
}
