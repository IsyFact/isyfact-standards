package de.bund.bva.isyfact.polling.common.konstanten;
 
/**
 * Diese Klasse enthält die Konstanten für Fehler-IDs des Pollings.
 * 
 */
public abstract class Fehlerschluessel {

    /** MBean-Objekt-Name ist leer. */
    public static final String MSG_MBEAN_OBJEKT_NAME_LEER = "POLLI01001";

    /** MBean-Objekt-Name "{0}" ist nicht korrekt aufgebaut. */
    public static final String MSG_MBEAN_OBJEKT_NAME_FEHLERHAFT = "POLLI01002";

    /** Fehler bei der Verbindung zur Polling-Anwendung mit der IP-Adresse "{0}" im Polling-Cluster {1}. */
    public static final String MSG_VERBINDUNGSFEHLER = "POLLI01003";

    /** 
     * Der JMX-Verbindungsparameter ipadresseport mit dem Wert "{0}" für die 
     * JMX-Verbindungs-ID "{1}" ist fehlerhaft. 
     */
    public static final String MSG_JMX_IPADRESSEPORT_FEHLERHAFT = "POLLI01004";

    /** Der Polling-Cluster "{0}" ist unbekannt. */
    public static final String MSG_POLLING_CLUSTER_UBEKANNT = "POLLI01005";

    /** Die JMX-Verbindungsparameter mit der ID "{0}" im Cluster mit der ID "{1}" sind unbekannt. */
    public static final String MSG_UNBEKANNTE_VERBINDUNGSZUORDNUNG = "POLLI01006";

    /** Das MBean-Attribut "{0}" auf dem Server mit der IP-Adresse "{1} wurde nicht gefunden. */
    public static final String MSG_MBEAN_ATTRIBUT_NICHT_GEFUNDEN = "POLLI01007";

    /** Die MBean-Instanz "{0}" auf dem Server mit der IP-Adresse "{1} wurde nicht gefunden. */
    public static final String MSG_MBEAN_INSTANZ_NICHT_GEFUNDEN = "POLLI01008";

    /** Beim Zugriff auf die MBean-Instanz "{0}" auf dem Server mit der IP-Adresse "{1} ist ein Fehler aufgetreten. */
    public static final String MSG_MBEAN_ZUGRIFF_FEHLER = "POLLI01009";

}



