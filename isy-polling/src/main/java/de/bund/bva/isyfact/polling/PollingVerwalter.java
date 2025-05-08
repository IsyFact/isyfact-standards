package de.bund.bva.isyfact.polling;

/**
 * Interface der Komponente Polling Verwalter. Der Polling-Verwalter verwaltet mehrere Polling-Cluster 
 * und unterstützt die Synchronisierung von Polling-Aktivitäten innerhalb eines Polling-Clusters.
 * <br>
 * Ein Polling-Cluster besteht aus Menge von Servern, die jeweils die gleiche Nachrichtenquelle abfragen 
 * und wird durch eine innerhalb der Anwendung eindeutige Id identifiziert.
 * 
 */
public interface PollingVerwalter {

    /**
     * Ermittelt, ob die aktuelle Instanz der Anwendung das Polling im angegebenen Cluster durchführen kann.
     * Ist das der Fall, wird der letzte Ausführungszeitpunkt aktualisiert und true zurückgegeben. 
     * 
     * @param clusterId
     *          ID, für den der Polling-Status ermittelt wird.
     * @return true, falls diese Instanz der Anwendung das Polling starten kann, 
     *         false sonst.
     */
    boolean startePolling(String clusterId);
    
    /**
     * Setzt den Zeitpunkt der letzten Polling-Aktivität für den angegebenen Polling-Cluster.
     * 
     *  @param clusterId
     *          ID des Polling-Clusters.
     */
    void aktualisiereZeitpunktLetztePollingAktivitaet(String clusterId);

    /**
     * Liefert den Zeitpunkt der letzten Polling-Aktivität für den angegebenen Polling-Cluster.
     * 
     * @param clusterId
     *          ID des Polling-Clusters.
     * @return Zeitpunkt der letzten Polling-Aktivität       
     * 
     */
    long getZeitpunktLetztePollingAktivitaet(String clusterId);
}



