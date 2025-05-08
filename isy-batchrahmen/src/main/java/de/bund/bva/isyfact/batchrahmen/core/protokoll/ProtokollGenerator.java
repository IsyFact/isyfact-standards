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
