package de.bund.bva.isyfact.batchrahmen.batch.protokoll;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenProtokollException;

/**
 * Diese Interface nimmt die Daten für das Ergebnisprotokoll auf. Im Ergebnisprotokoll werden folgende
 * Elemente gespeichert:
 * <ul>
 * <li>Der ReturnCode des Batchs (siehe {@link #setReturnCode(BatchReturnCode)}</li>
 * <li>Meldungen, die während der Batch-Verabreitung erzeugt werden 
 * ({@link #ergaenzeMeldung(VerarbeitungsMeldung)}</li>
 * <li>Statistiken</li>
 * </ul>
 * 
 * Um Statistiken zu speichern müssen die möglichen Statisitkeinträge erst mit
 * {@link #registriereStatistikEintrag(StatistikEintrag)} festgelegt werden. Der Zugriff erfolgt dann ï¿½ber
 * {@link #getStatistikEintrag(String)} an Hand der zuvor vergebenen Id.
 * 
 * 
 */
public interface BatchErgebnisProtokoll {

    /**
     * Setzt den ReturnCode fürs Protokoll.
     * @param returnCode
     *            Der ReturnCode.
     */
    public void setReturnCode(BatchReturnCode returnCode);

    /**
     * Registriert einen neuen Statistik-Eintrag. Alle zu protokollierenden Statistiken müssen initial mit
     * dieser Methode registriert werden. Der Zugriff erfolgt dann mit {@link #getStatistikEintrag(String)}.
     * @param initialEintrag
     *            Initialier Eintrag in der Statistik.
     */
    public void registriereStatistikEintrag(StatistikEintrag initialEintrag);

    /**
     * Liefert den Statisitkeintrag mit passender Id.
     * @param id
     *            Die Id der Statistik.
     * @return Liefert den Statistikeintrag mit der Id <code>id</code>
     */
    public StatistikEintrag getStatistikEintrag(String id);
    
    /**
     * Liefert eine sortierte Liste der Statistikeinträge.
     * @return Liste mitte allen Statistikeinträgen, sortiert nach Reihenfolge.
     */
    public List<StatistikEintrag> getStatistikEintraege();

    /**
     * Ergänzt eine Meldung zum Protokoll.
     * @param meldung
     *            Protokollmeldung.
     * @throws BatchrahmenProtokollException
     *             Falls beim Zugriff auf die Liste ein Fehlerauftritt.
     */
    public void ergaenzeMeldung(VerarbeitungsMeldung meldung);

    /**
     * Liefert den ReturnCode. Wird intern zu erzeugung der Ergbnisdatei verwendet.
     * @return Der ReturnCode.
     */
    public BatchReturnCode getReturnCode();

    /**
     * Liefert die Liste aller Statistiken. Wird intern zu erzeugung der Ergbnisdatei verwendet.
     * @return List aller Statistiken.
     */
    public Map<String, StatistikEintrag> getStatistik();

    /**
     * Liefert ob die Liste der Meldungen eine Fehlermeldung enthält.
     * @return <code>true</code> oder <code>false</code>.
     */
    public boolean enthaeltFehlerNachrichten();
    
    /**
     * Protokolliert den Batchstart.
     * 
     * @param konfiguration Die Konfiguration des Batches.
     * @param args Die übergebenen Batchparameter
     */
    public void batchStart(BatchKonfiguration konfiguration, String[] args);
    
    /**
     * Protokolliert das Batchende.
     */
    public void batchEnde();
    
    
    /**
     * Setzt die BatchID.
     * @param batchId Batchid des Batches
     */
    public void setBatchId(String batchId);
    
    /**
     * Liefert das Feld 'batchId' zurück.
     * @return Wert von batchId
     */
    public String getBatchId();
    
    /**
     * Setzt das Feld 'endeDatum'.
     * @param endeDatum Neuer Wert für endeDatum
     */
    public void setEndeDatum(Date endeDatum);   
    
    /**
     * Liefert das Feld 'endeDatum' zurück.
     * @return Wert von endeDatum
     */
    public Date getEndeDatum();   
    
    /**
     * Setzt das Feld 'startDatum'.
     * @param startDatum Neuer Wert für startDatum
     */
    public void setStartDatum(Date startDatum);
    
    /**
     * Liefert das Feld 'startDatum' zurück.
     * @return Wert von startDatum
     */
    public Date getStartDatum();
    
    /**
     * Liefert das Feld 'parameter' zurück.
     * @return Wert von parameter
     */
    public String[] getParameter();
    
    /**
     * Setzt das Feld 'parameter'.
     * @param parameter Neuer Wert für parameter
     */
    public void setParameter(String[] parameter);  
    
    /**
     * Setzt das Feld batchabgebrochen.
     * @param wert Neuer Wert
     */
    public void setBatchAbgebrochen(boolean wert);
    
    /**
     * Setzt das Feld batchabgebrochen.
     * @return Wert von batchabgebrochen
     */
    public boolean isBatchAbgebrochen();
    
    /**
     * Liefert das Feld 'maximaleLaufzeitUeberschritten' zurück.
     * @return Wert von maximaleLaufzeitUeberschritten
     */
    public boolean isMaximaleLaufzeitUeberschritten();

    /**
     * Setzt das Feld 'maximaleLaufzeitUeberschritten'.
     * @param maximaleLaufzeitUeberschritten Neuer Wert für maximaleLaufzeitUeberschritten
     */
    public void setMaximaleLaufzeitUeberschritten(boolean maximaleLaufzeitUeberschritten);
}
