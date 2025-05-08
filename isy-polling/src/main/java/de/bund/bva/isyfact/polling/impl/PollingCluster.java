package de.bund.bva.isyfact.polling.impl;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;

/**
 * Ein Polling-Cluster besteht aus Menge von Servern, die jeweils die gleiche Nachrichtenquelle abfragen
 * und wird durch eine innerhalb der Anwendung eindeutige Id identifiziert.
 * <br>
 * Diese Klasse enthält die beschreibenden Attribute eines Polling-Clusters.
 *
 */
class PollingCluster {

    /** ID des Polling-Clusters. */
    private String clusterId;

    /** Wartezeit bis zur Übernahme der Polling-Aktivität in Sekunden. */
    private int wartezeit;

    /** Name des Clusters. Dieser Name wird zur Bildung der MBean-Identifikation verwendet. */
    private String clusterName;

    /**
     * Liste der der Verbindungsangaben zu den JMX-Services der Anwendungen,
     * die außer der eigenen Anwendung noch zu diesem Polling-Cluster gehören.
     */
    private List<JMXConnectionParameter> jmxConnectionParameterListe = new ArrayList<>(5);

    /** MBean-Objekt-Name. Wird aus der Cluster-Id abgeleitet */
    private String mBeanObjektName;

    /** Zeitstempel der letzten durchgeführten Polling-Aktivität. */
    private long zeitstempel;


    /**
     * Erzeugt einen neuen Polling-Cluster.
     *
     * @param jmxDomain
     *          Domain-Name für die JMX-MBeans.
     * @param clusterId
     *          ID des Polling-Clusters.
     * @param clusterName
     *          Name des Clusters. Dieser Name wird zur Bildung der MBean-Identifikation verwendet.
     * @param wartezeit
     *          Wartezeit bis zur Übernahme der Polling-Aktivität in Sekunden.
     * @param jmxConnectionParameterListe
     *          Liste mit Verbindungsangaben.
     */
    PollingCluster(String jmxDomain, String clusterId, String clusterName, int wartezeit,
        List<JMXConnectionParameter> jmxConnectionParameterListe) {

        if (clusterId == null) {
            throw new IllegalArgumentException("Die ClusterId darf nicht leer sein!");
        }
        this.clusterId = clusterId;

        if (clusterName == null) {
            throw new IllegalArgumentException("Der Cluster-Name darf nicht leer sein!");
        }
        this.clusterName = clusterName;

        mBeanObjektName = jmxDomain + ":type=PollingStatus,name=\"Polling-Aktivitaet-" + clusterName + "\"";

        if (wartezeit < 10) {
            throw new IllegalArgumentException("Die Wartezeit darf nicht kleiner als 10 Sekunden sein!");
        }
        this.wartezeit = wartezeit;

        if (jmxConnectionParameterListe == null) {
            throw new IllegalArgumentException("Die Service-Liste darf nicht leer sein!");
        }
        this.jmxConnectionParameterListe = jmxConnectionParameterListe;
    }

    /**
     * Liefert die Id des Polling-Clusters.
     * Unter dieser Id ist er auch im Polling-Verwalter registriert.
     *
     * @return Id des Polling-Clusters.
     */
    String getClusterId() {
        return clusterId;
    }

    /**
     * Liefert das Feld 'clusterName' zurück.
     * @return Wert von clusterName
     */
    public String getClusterName() {
        return clusterName;
    }

    /**
     * Liefert die Wartezeit, nach der diese Instanz das Polling übernehmen darf.
     *
     * @return Wartezeit in Sekunden
     */
    int getWartezeit() {
        return wartezeit;
    }

    /**
     * Liefert die Liste der Verbindungsangaben zu den JMX-Services der Anwendungen,
     * die außer der eigenen Anwendung noch zu diesem Polling-Cluster gehören.
     *
     * @return Array mit JMX-Verbindungsangaben.
     */
    JMXConnectionParameter[] getJmxConnectionParameter() {
        if (jmxConnectionParameterListe.size() == 0) {
            return new JMXConnectionParameter[0];
        }
        return jmxConnectionParameterListe.toArray(new JMXConnectionParameter[jmxConnectionParameterListe.size()]);
    }

    /**
     * Liefert das Feld 'mBeanObjektName' zurück.
     * @return Wert von mBeanObjektName
     */
    String getMBeanObjektName() {
        return mBeanObjektName;
    }

    /**
     * Liefert den letzten gesetzten Zeitstempel für eine durchgeführte Polling-Aktion.
     *
     * @return Zeitstempel in Millisekunden
     */
    long getZeitpunktLetztePollingAktivitaet() {
        return zeitstempel;
    }

    /**
     * Setzt den Zeitstempel für die letzte durchgeführte Polling-Aktivität auf den aktuellen Zeitpunkt.
     */
    void aktualisiereZeitpunktLetztePollingAktivitaet() {
        zeitstempel = DateTimeUtil.getClock().instant().toEpochMilli();
    }
}
