package de.bund.bva.isyfact.batchrahmen.core.protokoll;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;

import org.xml.sax.SAXException;

import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.StatistikEintrag;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenProtokollException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

/**
 * Standardimplementierung eines {@link BatchErgebnisProtokoll}.
 *
 *
 */
public class DefaultBatchErgebnisProtokoll implements BatchErgebnisProtokoll {
    /**
     * Der Logger.
     */
    private IsyLogger log;

    /**
     * Liste der Statistik-Einträge.
     */
    private Map<String, StatistikEintrag> statistik = new HashMap<String, StatistikEintrag>();

    /**
     * ReturnCode des Batchs.
     */
    private BatchReturnCode returnCode;

    /**
     * Flag ob Fehlernachrichten enthalten sind.
     */
    private boolean enthaeltFehlermeldung;

    /**
     * Flag ob der Batch abgerochen wurde.
     */
    private boolean isBatchAbgebrochen;

    /**
     * FileOutputStream zum Protokollschreiben.
     */
    private ProtokollGenerator protokollGenerator;

    /**
     * Startdatum des Batches.
     */
    private Date startDatum;

    /**
     * Enddatum des Batches.
     */
    private Date endeDatum;

    /**
     * Die BatchID des ausgeführten Batches.
     */
    private String batchId;

    /**
     * Die Parameter, mit denen der Batch gestartet wurde.
     */
    private String[] parameter;

    /**
     * Flag ob die maximale Laufzeit überschritten wurde.
     */
    private boolean maximaleLaufzeitUeberschritten;

    /**
     * Erzeugt ein neues ErgebnisProtokoll.
     * @param ergebnisDatei
     *            Ausgabedatei
     * @throws IOException
     *             Falls die temporäre Datei für die Meldungen nicht angelegt werden kann.
     */
    public DefaultBatchErgebnisProtokoll(String ergebnisDatei) throws IOException {
        // Keine statische Logger konfiguration, da der Batchrahmen Logback erst zur Laufzeit konfiguriert.
        this.log = IsyLoggerFactory.getLogger(DefaultBatchErgebnisProtokoll.class);

        if (!"".equals(ergebnisDatei) && ergebnisDatei != null) {
            if (this.log != null) {
                this.log.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                    "Erstelle Ergebnisprotokoll '{}' und XMLProtokollGenerator...", ergebnisDatei);
            }
            try {
                // ProtokollGenerator erstellen
                this.protokollGenerator = new XmlProtokollGenerator(new FileOutputStream(ergebnisDatei));
            } catch (TransformerConfigurationException e) {
                throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
            } catch (SAXException e) {
                throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setReturnCode(BatchReturnCode returnCode) {
        this.returnCode = returnCode;
    }

    /**
     * {@inheritDoc}
     */
    public void registriereStatistikEintrag(StatistikEintrag initialEintrag) {
        if (initialEintrag.getReihenfolge() == 0) {
            initialEintrag.setReihenfolge(ermittleMaximaleReihenfolge() + 1);
        }
        this.statistik.put(initialEintrag.getId(), initialEintrag);
    }

    /**
     * Ermittelt den maximalen Wert für die Reihenfolge der Statistik-Einträge.
     * @return Maximum der Reihenfolge Werte
     */
    private int ermittleMaximaleReihenfolge() {
        int max = 0;
        for (StatistikEintrag eintrag : this.statistik.values()) {
            max = Math.max(max, eintrag.getReihenfolge());
        }
        return max;
    }

    /**
     * {@inheritDoc}
     */
    public StatistikEintrag getStatistikEintrag(String id) {
        return this.statistik.get(id);
    }

    /**
     * {@inheritDoc}
     * @throws BatchProtokollException
     */
    public void ergaenzeMeldung(VerarbeitungsMeldung meldung) {
        if (meldung.getTyp().equals(MeldungTyp.FEHLER)) {
            this.enthaeltFehlermeldung = true;
        }
        if (this.protokollGenerator != null) {
            this.protokollGenerator.erzeugeMeldung(meldung);
            this.protokollGenerator.flusheOutput();
        }
    }

    /**
     * {@inheritDoc}
     */
    public BatchReturnCode getReturnCode() {
        return this.returnCode;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, StatistikEintrag> getStatistik() {
        return this.statistik;
    }

    /**
     * {@inheritDoc}
     */
    public boolean enthaeltFehlerNachrichten() {
        return this.enthaeltFehlermeldung;
    }

    /**
     * {@inheritDoc}
     */
    public List<StatistikEintrag> getStatistikEintraege() {
        List<StatistikEintrag> result = new ArrayList<StatistikEintrag>(this.statistik.values());
        Collections.sort(result);
        return result;
    }

    /**
     * Liefert das Feld 'startDatum' zurück.
     * @return Wert von startDatum
     */
    public Date getStartDatum() {
        return this.startDatum;
    }

    /**
     * Setzt das Feld 'startDatum'.
     * @param startDatum
     *            Neuer Wert für startDatum
     */
    public void setStartDatum(Date startDatum) {
        this.startDatum = startDatum;
    }

    /**
     * Liefert das Feld 'endeDatum' zurück.
     * @return Wert von endeDatum
     */
    public Date getEndeDatum() {
        return this.endeDatum;
    }

    /**
     * Setzt das Feld 'endeDatum'.
     * @param endeDatum
     *            Neuer Wert für endeDatum
     */
    public void setEndeDatum(Date endeDatum) {
        this.endeDatum = endeDatum;
    }

    /**
     * Liefert das Feld 'batchId' zurück.
     * @return Wert von batchId
     */
    public String getBatchId() {
        return this.batchId;
    }

    /**
     * Setzt das Feld 'batchId'.
     * @param batchId
     *            Neuer Wert für batchId
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    /**
     * Liefert das Feld 'parameter' zurück.
     * @return Wert von parameter
     */
    public String[] getParameter() {
        return this.parameter;
    }

    /**
     * Setzt das Feld 'parameter'.
     * @param parameter
     *            Neuer Wert für parameter
     */
    public void setParameter(String[] parameter) {
        this.parameter = parameter;
    }

    /**
     *
     * {@inheritDoc}.
     */
    public void batchEnde() {
        setEndeDatum(new Date());
        if (this.protokollGenerator != null) {
            this.protokollGenerator.erzeugeStatistik(this);
            this.protokollGenerator.erzeugeEndeInfoElement(this);
            this.protokollGenerator.erzeugeReturnCodeElement(this);
            this.protokollGenerator.close();
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    public void batchStart(BatchKonfiguration konfiguration, String[] args) {
        setStartDatum(new Date());
        setBatchId(konfiguration.getProperties().getProperty("BatchId"));
        setParameter(args);

        if (this.protokollGenerator != null) {
            this.protokollGenerator.erzeugeStartInfoElement(this);
        }
    }

    /**
     * Liefert das Feld 'isBatchAbgebrochen' zurück.
     * @return Wert von isBatchAbgebrochen
     */
    public boolean isBatchAbgebrochen() {
        return this.isBatchAbgebrochen;
    }

    /**
     * Setzt das Feld 'isBatchAbgebrochen'.
     * @param isBatchAbgebrochen
     *            Neuer Wert für isBatchAbgebrochen
     */
    public void setBatchAbgebrochen(boolean isBatchAbgebrochen) {
        this.isBatchAbgebrochen = isBatchAbgebrochen;
    }

    /**
     * Liefert das Feld 'maximaleLaufzeitUeberschritten' zurück.
     * @return Wert von maximaleLaufzeitUeberschritten
     */
    public boolean isMaximaleLaufzeitUeberschritten() {
        return this.maximaleLaufzeitUeberschritten;
    }

    /**
     * Setzt das Feld 'maximaleLaufzeitUeberschritten'.
     * @param maximaleLaufzeitUeberschritten
     *            Neuer Wert für maximaleLaufzeitUeberschritten
     */
    public void setMaximaleLaufzeitUeberschritten(boolean maximaleLaufzeitUeberschritten) {
        this.maximaleLaufzeitUeberschritten = maximaleLaufzeitUeberschritten;
    }

}
